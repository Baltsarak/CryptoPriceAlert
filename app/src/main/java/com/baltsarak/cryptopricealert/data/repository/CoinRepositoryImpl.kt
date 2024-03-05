package com.baltsarak.cryptopricealert.data.repository

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.baltsarak.cryptopricealert.data.database.AppDatabase
import com.baltsarak.cryptopricealert.data.database.entities.WatchListCoinDbModel
import com.baltsarak.cryptopricealert.data.mapper.CoinMapper
import com.baltsarak.cryptopricealert.data.network.ApiFactory
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.domain.CoinRepository
import com.baltsarak.cryptopricealert.domain.TargetPrice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.util.TreeSet

class CoinRepositoryImpl(
    application: Application
) : CoinRepository {

    private val coinInfoDao = AppDatabase.getInstance(application).coinInfoDao()
    private val watchListCoinInfoDao = AppDatabase.getInstance(application).watchListCoinInfoDao()
    private val coinPriceHistoryDao = AppDatabase.getInstance(application).coinPriceHistoryDao()

    private val apiService = ApiFactory.apiService

    private val mapper = CoinMapper()

    override suspend fun addCoinToWatchList(fromSymbol: String, targetPrice: Double) {
        watchListCoinInfoDao.insertCoinToWatchList(WatchListCoinDbModel(0, fromSymbol, targetPrice))
    }

    override suspend fun addListToWatchList(watchList: List<CoinInfo>) {
        val listCoinDbModel = watchList.flatMap { coinInfo ->
            coinInfo.targetPrice.map { targetPrice ->
                WatchListCoinDbModel(0, coinInfo.fromSymbol, targetPrice)
            }
        }
        listCoinDbModel.forEach { watchListCoinInfoDao.insertCoinToWatchList(it) }
    }

    override suspend fun deleteCoinFromWatchList(fromSymbol: String) {
        watchListCoinInfoDao.deleteCoinFromWatchList(fromSymbol)
    }

    override suspend fun getWatchListCoins(): List<CoinInfo> {
        val result = mutableListOf<CoinInfo>()
        val targetPrices = getTargetPrices()
        val watchListCoins = targetPrices
            .groupBy { it.fromSymbol }
            .mapValues { entry ->
                entry.value.map { it.targetPrice }
            }
        for (coin in watchListCoins) {
            val coinInfoFromDb = coinInfoDao.getInfoAboutCoin(coin.key)
            val coinInfoEntity = CoinInfo(
                fromSymbol = coin.key,
                toSymbol = coinInfoFromDb.toSymbol,
                targetPrice = coin.value,
                price = coinInfoFromDb.price,
                lastMarket = coinInfoFromDb.lastMarket,
                lastUpdate = coinInfoFromDb.lastUpdate,
                highDay = coinInfoFromDb.highDay,
                lowDay = coinInfoFromDb.lowDay,
                imageUrl = CoinMapper.BASE_IMAGE_URL + coinInfoFromDb.imageUrl
            )
            result.add(coinInfoEntity)
        }
        return result
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

    override suspend fun loadData() {
        while (true) {
            try {
                val popularCoins = getPopularCoinsListFromApi()
                val watchList = withContext(Dispatchers.Default) {
                    watchListCoinInfoDao.getWatchListCoins()
                }
                val setAllCoins = TreeSet<String>(popularCoins).apply {
                    addAll(watchList)
                }
                val fromSymbols = setAllCoins.joinToString(",")
                val jsonContainer = apiService.getFullInfoAboutCoins(fSyms = fromSymbols)
                val coinInfoDtoList = mapper.mapJsonContainerToListCoinInfo(jsonContainer)
                val coinInfoDbModelList = coinInfoDtoList.map { mapper.mapDtoToDbModel(it) }
                coinInfoDao.insertListCoinsInfo(coinInfoDbModelList)
            } catch (e: Exception) {
                Log.d("loadData", "ERROR LOAD DATA " + e.message)
            }
            delay(10000)
        }
    }

    private suspend fun getPopularCoinsListFromApi(): List<String?> {
        return (apiService.getPopularCoinsList(limit = 50))
            .coins?.map { it.coinName?.name } ?: listOf("BTC")
    }

    private suspend fun getTargetPrices(): List<TargetPrice> {
        return withContext(Dispatchers.Default) {
            watchListCoinInfoDao.getTargetPrices()
        }
    }
}