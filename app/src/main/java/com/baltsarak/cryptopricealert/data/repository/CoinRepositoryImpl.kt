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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

class CoinRepositoryImpl(
    private val application: Application
) : CoinRepository {

    private val coinInfoDao = AppDatabase.getInstance(application).coinInfoDao()
    private val watchListCoinInfoDao = AppDatabase.getInstance(application).watchListCoinInfoDao()

    private val apiService = ApiFactory.apiService

    private val mapper = CoinMapper()

    override suspend fun addCoinToWatchList(fromSymbol: String) {
        watchListCoinInfoDao.insertCoinToWatchList(WatchListCoinDbModel(fromSymbol))
    }

    override fun deleteCoinFromWatchList(fromSymbol: String) {
        watchListCoinInfoDao.deleteCoinFromWatchList(fromSymbol)
    }

    override suspend fun getWatchListCoins(): LiveData<List<CoinInfo>> {
        return coinInfoDao.getListCoinsInfo(getWatchList()).map {
            it.map {
                mapper.mapDbModelToEntity(it)
            }
        }
    }

    override suspend fun getPopularCoinsList(): LiveData<List<CoinInfo>> {
        return coinInfoDao.getListCoinsInfo(getPopularCoinsListFromApi()).map {
            it.map {
                mapper.mapDbModelToEntity(it)
            }
        }
    }

    override fun getCoinInfo(fromSymbol: String): LiveData<CoinInfo> {
        return coinInfoDao.getInfoAboutCoin(fromSymbol).map {
            mapper.mapDbModelToEntity(it)
        }
    }

    override suspend fun loadData() {
        while (true) {
            try {
                loadPopularCoins()
                loadWatchListCoins()
            } catch (e: Exception) {
                Log.d("loadData", "ERROR LOAD DATA " + e.message)
            }
            delay(10000)
        }
    }

    private suspend fun loadPopularCoins() {
        val popularCoins = apiService.getPopularCoinsList(limit = 50)
        val fSym = mapper.mapPopularCoinsListToString(popularCoins)
        val jsonContainer = apiService.getFullInfoAboutCoins(fSyms = fSym)
        val coinInfoDtoList = mapper.mapJsonContainerToListCoinInfo(jsonContainer)
        val popularDbModelList = coinInfoDtoList.map { mapper.mapDtoToDbModel(it) }
        coinInfoDao.insertListCoinsInfo(popularDbModelList)
    }

    private suspend fun loadWatchListCoins() {
        val watchList = withContext(Dispatchers.Default) {
            watchListCoinInfoDao.getWatchListCoins().joinToString(",")
        }
        val watchListJsonContainer = apiService.getFullInfoAboutCoins(fSyms = watchList)
        val watchListDto = mapper.mapJsonContainerToListCoinInfo(watchListJsonContainer)
        val watchDbModelList = watchListDto.map { mapper.mapDtoToDbModel(it) }
        coinInfoDao.insertListCoinsInfo(watchDbModelList)
    }

    private suspend fun getPopularCoinsListFromApi(): List<String?> {
        return (apiService.getPopularCoinsList(limit = 50))
            .coins?.map { it.coinName?.name } ?: listOf("BTC")
    }

    private suspend fun getWatchList(): List<String> {
        return withContext(Dispatchers.Default) {
            watchListCoinInfoDao.getWatchListCoins()
        }
    }
}