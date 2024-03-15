package com.baltsarak.cryptopricealert.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.baltsarak.cryptopricealert.data.database.AppDatabase
import com.baltsarak.cryptopricealert.data.database.entities.CoinInfoDbModel
import com.baltsarak.cryptopricealert.data.database.entities.WatchListCoinDbModel
import com.baltsarak.cryptopricealert.data.mapper.CoinMapper
import com.baltsarak.cryptopricealert.data.network.ApiFactory
import com.baltsarak.cryptopricealert.data.worker.PriceMonitoringWorker
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.domain.CoinRepository
import com.baltsarak.cryptopricealert.domain.TargetPrice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class CoinRepositoryImpl(
    private val application: Application
) : CoinRepository {

    private val coinInfoDao = AppDatabase.getInstance(application).coinInfoDao()
    private val watchListCoinInfoDao = AppDatabase.getInstance(application).watchListCoinInfoDao()
    private val coinPriceHistoryDao = AppDatabase.getInstance(application).coinPriceHistoryDao()

    private val apiService = ApiFactory.apiService

    private val mapper = CoinMapper()

    private val _watchListLiveData = MutableLiveData<List<CoinInfo>>()
    override fun getWatchListLiveData(): LiveData<List<CoinInfo>> {
        return _watchListLiveData
    }


    override suspend fun addCoinToWatchList(
        fromSymbol: String,
        targetPrice: Double,
        higherThenCurrentPrice: Boolean
    ) {
        val size = watchListCoinInfoDao.getWatchListSize()
        watchListCoinInfoDao.insertCoinToWatchList(
            WatchListCoinDbModel(
                0,
                fromSymbol,
                targetPrice,
                higherThenCurrentPrice,
                size + 1
            )
        )
    }

    override suspend fun rewriteWatchList(watchList: List<CoinInfo>) {
        val listCoinDbModel = watchList.flatMapIndexed() { index, coinInfo ->
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

    override suspend fun deleteCoinFromWatchList(fromSymbol: String) {
        watchListCoinInfoDao.deleteCoinFromWatchList(fromSymbol)
    }

    private suspend fun getWatchListCoins() {
        val result = mutableListOf<CoinInfo>()
        val targetPrices = getTargetPrices()
        val watchListCoins = targetPrices
            .groupBy { it.fromSymbol }

        for (coin in watchListCoins) {
            val coinInfoFromDb = coinInfoDao.getInfoAboutCoin(coin.key)
            val coinInfoEntity = CoinInfo(
                id = coinInfoFromDb.id,
                fromSymbol = coin.key,
                fullName = coinInfoFromDb.fullName,
                toSymbol = coinInfoFromDb.toSymbol,
                targetPrice = coin.value,
                price = coinInfoFromDb.price,
                imageUrl = coinInfoFromDb.imageUrl
            )
            result.add(coinInfoEntity)
        }
        _watchListLiveData.value = result
    }

    override suspend fun getPopularCoinsList(): LiveData<List<CoinInfo>> {
        return coinInfoDao.getListCoinsInfo(getPopularCoinsListFromApi()).map {
            it.map {
                mapper.mapDbModelToEntity(it)
            }
        }
    }

    override suspend fun getCoinInfo(fromSymbol: String): LiveData<CoinInfo> {
        return coinInfoDao.getLiveDataInfoAboutCoin(fromSymbol)
            .map { mapper.mapDbModelToEntity(it) }
    }

    override suspend fun loadCoinPriceHistory(fromSymbol: String) {
        try {
            val dayPriceContainer =
                apiService.getCoinPriceHistory(fSym = fromSymbol)
            val dayPriceList = dayPriceContainer.data?.data
            val dayPriceDbModelList = dayPriceList
                ?.map { mapper.mapDayPriceDtoToDbModel(fromSymbol, it) } ?: listOf()

            coinPriceHistoryDao.insertCoinsPriceHistoryList(dayPriceDbModelList)
        } catch (e: Exception) {
            Log.d("loadData", "ERROR LOAD DATA PRICE HISTORY " + e.message)
        }
    }

    override suspend fun getCoinPriceHistory(fromSymbol: String): Map<Float, Float> {
        val dbModelList = coinPriceHistoryDao.getCoinsPriceHistoryList(fromSymbol)
        return dbModelList.associate { it.time.toFloat() to it.close.toFloat() }
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

    override suspend fun loadData() {
        try {
            val popularCoins = apiService.getPopularCoinsList().coinList
            val allCoinsList = popularCoins?.coins?.toMutableSet() ?: mutableSetOf()

            while (true) {
                val watchListNames = withContext(Dispatchers.IO) {
                    watchListCoinInfoDao.getWatchListCoins()
                }
                val watchList = watchListNames
                    .map { apiService.getCoinInfo(assetSymbol = it) }.map { it.coinInfo }

                allCoinsList.addAll(watchList)
                val allCoinSymbols = allCoinsList.joinToString(",") { it.symbol }
                val response = apiService.getCoinPrices(fSyms = allCoinSymbols)
                if (response.isSuccessful) {
                    val prices = response.body()
                    val coinInfoDbModelList = allCoinsList.map { coinInfo ->
                        prices?.get(coinInfo.symbol)?.let { pricesMap ->
                            mapper.mapDtoToDbModel(coinInfo, pricesMap["USD"])
                        } ?: CoinInfoDbModel(0, "", "", "", 0.0, "")
                    }
                    coinInfoDao.insertListCoinsInfo(coinInfoDbModelList)
                    getWatchListCoins()
                    delay(10_000)
                }
            }
        } catch (e: Exception) {
            Log.d("loadData", "ERROR LOAD DATA " + e.message)
        }
    }

    private suspend fun getPopularCoinsListFromApi(): List<String> {
        val popularCoins = apiService.getPopularCoinsList().coinList
        return popularCoins?.coins?.map { it.symbol } ?: listOf("BTC")
    }

    private suspend fun getTargetPrices(): List<TargetPrice> {
        return withContext(Dispatchers.Default) {
            watchListCoinInfoDao.getTargetPrices()
        }
    }
}