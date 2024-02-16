package com.baltsarak.cryptopricealert.data.repository

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.baltsarak.cryptopricealert.data.database.AppDatabase
import com.baltsarak.cryptopricealert.data.mapper.CoinMapper
import com.baltsarak.cryptopricealert.data.network.ApiFactory
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.domain.CoinRepository
import kotlinx.coroutines.delay

class CoinRepositoryImpl(
    private val application: Application
) : CoinRepository {

    private val popularCoinInfoDao = AppDatabase.getInstance(application).popularCoinInfoDao()
    private val watchListCoinInfoDao = AppDatabase.getInstance(application).watchListCoinInfoDao()

    private val apiService = ApiFactory.apiService

    private val mapper = CoinMapper()

    override fun getWatchListCoins(): LiveData<List<CoinInfo>> {
        return watchListCoinInfoDao.getWatchListCoins().map {
            it.map {
                mapper.mapDbModelToEntity(it)
            }
        }
    }

    override fun getCoinInfoFromWatchList(fromSymbol: String): LiveData<CoinInfo> {
        return watchListCoinInfoDao.getInfoAboutCoin(fromSymbol).map {
            mapper.mapDbModelToEntity(it)
        }
    }

    override fun getPopularCoinInfoList(): LiveData<List<CoinInfo>> {
        return popularCoinInfoDao.getListPopularCoins().map {
            it.map {
                mapper.mapDbModelToEntity(it)
            }
        }
    }

    override fun getPopularCoinInfo(fromSymbol: String): LiveData<CoinInfo> {
        return popularCoinInfoDao.getInfoAboutPopularCoin(fromSymbol).map {
            mapper.mapDbModelToEntity(it)
        }
    }

    override suspend fun loadData() {
        while (true) {
            try {
                val popularCoins = apiService.getPopularCoinsList(limit = 50)
                val fSyms = mapper.mapNamesListToString(popularCoins)
                val jsonContainer = apiService.getFullInfoAboutCoins(fSyms = fSyms)
                val coinInfoDtoList = mapper.mapJsonContainerToListCoinInfo(jsonContainer)
                val dbModelList = coinInfoDtoList.map { mapper.mapDtoToDbModel(it) }
                popularCoinInfoDao.insertListPopularCoins(dbModelList)
            } catch (e: Exception) {
            }
            delay(10000)
        }
    }
}