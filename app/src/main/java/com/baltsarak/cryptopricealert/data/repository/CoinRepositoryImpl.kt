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

    private val coinInfoDao = AppDatabase.getInstance(application).coinInfoDao()

    private val apiService = ApiFactory.apiService

    private val mapper = CoinMapper()

    override fun getCoinInfoList(): LiveData<List<CoinInfo>> {
        return coinInfoDao.getPriceList().map {
            it.map {
                mapper.mapDbModelToEntity(it)
            }
        }
    }

    override fun getCoinInfo(fromSymbol: String): LiveData<CoinInfo> {
        return coinInfoDao.getPriceInfoAboutCoin(fromSymbol).map {
            mapper.mapDbModelToEntity(it)
        }
    }

    override suspend fun loadData() {
        while (true) {
            try {
                val topCoins = apiService.getTopCoinInfo(limit = 50)
                val fSyms = mapper.mapNamesListToString(topCoins)
                val jsonContainer = apiService.getFullPriceList(fSyms = fSyms)
                val coinInfoDtoList = mapper.mapJsonContainerToListCoinInfo(jsonContainer)
                val dbModelList = coinInfoDtoList.map { mapper.mapDtoToDbModel(it) }
                coinInfoDao.insertPriceList(dbModelList)
            } catch (e: Exception) {
            }
            delay(10000)
        }
    }
}