package com.baltsarak.cryptopricealert.data.repository

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.baltsarak.cryptopricealert.data.database.AppDatabase
import com.baltsarak.cryptopricealert.data.database.RemoteDatabaseService
import com.baltsarak.cryptopricealert.data.database.entities.WatchListCoinDbModel
import com.baltsarak.cryptopricealert.data.mapper.CoinMapper
import com.baltsarak.cryptopricealert.data.network.ApiFactory
import com.baltsarak.cryptopricealert.data.worker.PriceMonitoringWorker
import com.baltsarak.cryptopricealert.domain.CoinRepository
import com.baltsarak.cryptopricealert.domain.entities.CoinInfo
import com.baltsarak.cryptopricealert.domain.entities.CoinName
import com.baltsarak.cryptopricealert.domain.entities.News
import com.baltsarak.cryptopricealert.domain.entities.TargetPrice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext

class CoinRepositoryImpl(
    private val application: Application
) : CoinRepository {

    private val coinInfoDao = AppDatabase.getInstance(application).coinInfoDao()
    private val watchListCoinInfoDao = AppDatabase.getInstance(application).watchListCoinInfoDao()
    private val coinPriceHistoryDao = AppDatabase.getInstance(application).coinPriceHistoryDao()

    private val remoteDatabaseService = RemoteDatabaseService()

    private val apiService = ApiFactory.apiService

    private val mapper = CoinMapper()

    private var _watchListLiveData = MutableLiveData<List<CoinInfo>>()
    val watchListLiveData: LiveData<List<CoinInfo>> = _watchListLiveData

    private var networkConnected = false

    override suspend fun addCoinToWatchList(
        fromSymbol: String,
        targetPrice: Double?,
        higherThenCurrentPrice: Boolean
    ): Long {
        val size = watchListCoinInfoDao.getWatchListSize()
        withContext(Dispatchers.IO) {
            remoteDatabaseService.addToRemoteDatabase(
                fromSymbol,
                targetPrice,
                higherThenCurrentPrice,
                size + 1
            )
        }
        return watchListCoinInfoDao.insertCoinToWatchList(
            WatchListCoinDbModel(
                0,
                fromSymbol,
                targetPrice,
                higherThenCurrentPrice,
                size + 1
            )
        )
    }

    override suspend fun loadingWatchlistAndStartWorker() {
        val watchList = remoteDatabaseService.getWatchListFromRemoteDatabase()
        watchList.forEach { watchListCoinInfoDao.insertCoinToWatchList(it) }
        startWorker()
    }

    override suspend fun rewriteWatchList(watchList: List<CoinInfo>) {
        val listCoinDbModel = watchList.flatMapIndexed { index, coinInfo ->
            coinInfo.targetPrice.map { targetPrice ->
                targetPrice?.let {
                    WatchListCoinDbModel(
                        it.id,
                        coinInfo.fromSymbol,
                        targetPrice.targetPrice,
                        targetPrice.higherThenCurrent,
                        index + 1
                    )
                }
            }
        }
        listCoinDbModel.forEach {
            if (it != null) {
                watchListCoinInfoDao.insertCoinToWatchList(it)
            }
        }
    }

    override suspend fun rewriteWatchListInRemoteDatabase() {
        remoteDatabaseService.deleteAllFromRemoteDatabase()
        remoteDatabaseService.sendWatchListToRemoteDatabase(getTargetPrices())
    }

    override suspend fun deleteCoinFromWatchList(fromSymbol: String) {
        watchListCoinInfoDao.deleteCoinFromWatchList(fromSymbol)
    }

    override suspend fun deleteAllFromWatchList() {
        watchListCoinInfoDao.deleteAllFromWatchList()
    }

    override suspend fun deleteTargetPrice(fromSymbol: String, price: Double) {
        withContext(Dispatchers.IO) {
            if (watchListCoinInfoDao.countTargetPrices(fromSymbol) > 1) {
                watchListCoinInfoDao.deleteTargetPrice(fromSymbol, price)
            } else {
                watchListCoinInfoDao.zeroingTargetPrice(fromSymbol, price)
            }
            rewriteWatchListInRemoteDatabase()
        }
    }

    override suspend fun getWatchListCoins(): List<CoinInfo> {
        val result = mutableListOf<CoinInfo>()
        val targetPrices = getTargetPrices()
        val watchListCoins = targetPrices
            .groupBy { it.fromSymbol }
        for (coin in watchListCoins) {
            val coinInfo = coinInfoDao.getInfoAboutCoin(coin.key)
            val coinInfoEntity = CoinInfo(
                id = coinInfo.id,
                fromSymbol = coin.key,
                fullName = coinInfo.fullName,
                toSymbol = coinInfo.toSymbol,
                targetPrice = coin.value,
                price = coinInfo.price,
                imageUrl = coinInfo.imageUrl
            )
            result.add(coinInfoEntity)
        }
        _watchListLiveData.value = result
        return result
    }

    override suspend fun getPopularCoinsList(): LiveData<List<CoinInfo>> {
        return coinInfoDao.getListCoinsInfo(getPopularCoinsListFromApi()).map {
            it.map { coinInfo ->
                mapper.mapDbModelToEntity(coinInfo, null)
            }
        }
    }

    override suspend fun getCoinInfo(fromSymbol: String): LiveData<CoinInfo> {
        val coinInfoLiveData = coinInfoDao.getLiveDataInfoAboutCoin(fromSymbol)
        val targetPricesLiveData = getTargetPrice(fromSymbol)

        return MediatorLiveData<CoinInfo>().apply {
            fun update() {
                val coinInfo = coinInfoLiveData.value
                val targetPrices = targetPricesLiveData.value
                if (coinInfo != null) {
                    value = mapper.mapDbModelToEntity(coinInfo, targetPrices)
                }
            }
            addSource(coinInfoLiveData) { update() }
            addSource(targetPricesLiveData) { update() }
        }
    }

    private suspend fun getTargetPrice(fromSymbol: String): LiveData<List<TargetPrice>> {
        return withContext(Dispatchers.Default) {
            watchListCoinInfoDao.getTargetPrice(fromSymbol)
        }
    }

    override suspend fun getCoinsList(): List<CoinName> {
        return coinInfoDao.getListCoins()
    }

    override suspend fun loadCoinPriceHistory(fromSymbol: String) {
        try {
            withContext(Dispatchers.IO) {
                val dayPriceContainer = apiService.getCoinPriceHistory(fSym = fromSymbol)
                dayPriceContainer.data?.data?.let { dayPriceList ->
                    val dayPriceDbModelList = dayPriceList
                        .map { mapper.mapDayPriceDtoToDbModel(fromSymbol, it) }
                    coinPriceHistoryDao.insertCoinsPriceHistoryList(dayPriceDbModelList)
                }
            } ?: Log.d("loadData", "Price history data is null for $fromSymbol")
            withContext(Dispatchers.IO) {
                val hourPriceContainer = apiService.getCoinPriceHourHistory(fSym = fromSymbol)
                hourPriceContainer.data?.data?.let { hourPriceList ->
                    val hourPriceDbModelList = hourPriceList
                        .map { mapper.mapHourPriceDtoToDbModel(fromSymbol, it) }
                    coinPriceHistoryDao.insertCoinsPriceHourHistoryList(hourPriceDbModelList)
                }
            } ?: Log.d("loadData", "Price history data is null for $fromSymbol")
        } catch (e: Exception) {
            Log.d("loadData", "ERROR LOAD DATA PRICE HISTORY " + e.message)
        }
    }

    override suspend fun getCoinPriceHistory(
        fromSymbol: String,
        period: Int
    ): Map<Float, Float> {
        val dbModelList = when (period) {
            24 -> coinPriceHistoryDao.getPriceHistoryForDay(fromSymbol)
            7 -> coinPriceHistoryDao.getPriceHistoryForWeek(fromSymbol)
            30 -> coinPriceHistoryDao.getPriceHistoryForMonth(fromSymbol)
            365 -> coinPriceHistoryDao.getPriceHistoryForYear(fromSymbol)
            5 -> coinPriceHistoryDao.getPriceHistoryForFiveYears(fromSymbol)
            else -> coinPriceHistoryDao.getAllPriceHistoryCoin(fromSymbol)
        }
        return dbModelList.associate { it.date.toFloat() to it.price.toFloat() }
    }

    override suspend fun getCurrentCoinPrice(fromSymbol: String): Double {
        return apiService.getCoinPrice(fSym = fromSymbol).price
    }

    override fun startWorker() {
        val workManager = WorkManager.getInstance(application)
        workManager.enqueueUniqueWork(
            PriceMonitoringWorker.NAME,
            ExistingWorkPolicy.KEEP,
            PriceMonitoringWorker.WORK_REQUEST
        )
    }

    private fun networkStatusFlow(): Flow<Boolean> = callbackFlow {
        val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                trySend(true).isSuccess
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                trySend(false).isSuccess
            }
        }
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        awaitClose { connectivityManager.unregisterNetworkCallback(networkCallback) }
    }

    override suspend fun loadDataWithNetworkCheck() {
        networkStatusFlow().collect { isConnected ->
            if (isConnected) {
                networkConnected = true
                loadData()
            } else {
                networkConnected = false
            }
        }
    }

    private suspend fun loadData() {
        try {
            withContext(Dispatchers.IO) {
                deleteAllFromWatchList()
                loadingWatchlistAndStartWorker()
            }
            val container = withContext(Dispatchers.IO) {
                apiService.getAllCoinsList()
            }
            val coinList = mapper
                .mapCoinSymbolsContainerDtoToCoinSymbolsList(container)
                .map { it.symbol }

            val listSymbols = withContext(Dispatchers.IO) {
                coinInfoDao.getListSymbols().toSet()
            }
            val filteredList = coinList.filter { !listSymbols.contains(it) }

            val coinInfoList = withContext(Dispatchers.IO) {
                filteredList.map { coin ->
                    async {
                        try {
                            apiService.getCoinInfo(assetSymbol = coin).coinInfo
                        } catch (e: Exception) {
                            null
                        }
                    }
                }.awaitAll()
            }

            val chunkedCoinList = mapper.listChunking(filteredList, 50)
            val coinPrices = mutableMapOf<String, Map<String, Double>>()

            withContext(Dispatchers.IO) {
                val deferredResponses =
                    chunkedCoinList.map { coinSymbolsList ->
                        async {
                            val allCoinSymbols = coinSymbolsList.joinToString(",")
                            apiService.getCoinPrices(fSyms = allCoinSymbols)
                        }
                    }
                deferredResponses.awaitAll().forEach { response ->
                    if (response.isSuccessful) {
                        response.body()?.let { prices ->
                            coinPrices.putAll(prices)
                        }
                    }
                }
            }
            val coinInfoDbModelList = coinInfoList.mapNotNull { coinInfo ->
                coinPrices[coinInfo?.symbol]?.let { pricesMap ->
                    mapper.mapDtoToDbModel(coinInfo, pricesMap["USD"])
                }
            }

            coinInfoDao.insertListCoinsInfo(coinInfoDbModelList)

            while (networkConnected) {
                withContext(Dispatchers.IO) {
                    val popularCoins = apiService.getPopularCoinsList().coinList
                    val popularCoinsList = popularCoins?.coins?.map { it.symbol } ?: listOf()
                    val watchListNames = watchListCoinInfoDao.getWatchListCoins()
                    val allCoinsList = popularCoinsList.toMutableSet()
                    allCoinsList.addAll(watchListNames)
                    val names = allCoinsList.joinToString(",")
                    val response = apiService.getCoinPrices(fSyms = names)
                    if (response.isSuccessful) {
                        val body = response.body()
                        if (body != null) {
                            for (i in body) {
                                i.value["USD"]?.let { coinInfoDao.updatePrice(i.key, it) }
                            }
                        }
                    }
                }
                getWatchListCoins()
                delay(10_000)
            }
        } catch (e: Exception) {
            Log.d("loadData", "ERROR LOAD DATA " + e.message)
        }
    }

    override suspend fun getNewsList(): List<News> {
        return try {
            val cryptoNews = withContext(Dispatchers.IO) {
                apiService.getCryptoNews()
            }
            cryptoNews.newsList.map { mapper.mapNewsDtoToEntity(it) }
        } catch (e: Exception) {
            listOf()
        }
    }

    private suspend fun getPopularCoinsListFromApi(): List<String> {
        return try {
            val popularCoins = apiService.getPopularCoinsList().coinList
            popularCoins?.coins?.map { it.symbol } ?: listOf("BTC")
        } catch (e: Exception) {
            listOf("BTC")
        }
    }

    private suspend fun getTargetPrices(): List<TargetPrice> {
        return withContext(Dispatchers.Default) {
            watchListCoinInfoDao.getTargetPrices()
        }
    }
}