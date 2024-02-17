package com.baltsarak.cryptopricealert.domain

import androidx.lifecycle.LiveData

interface CoinRepository {

    fun getWatchListCoins(): LiveData<List<CoinInfo>>

    fun getCoinInfoFromWatchList(fromSymbol: String): LiveData<CoinInfo>

    fun addCoinToWatchList(fromSymbol: String)

    fun deleteCoinFromWatchList(fromSymbol: String)

    fun getPopularCoinInfoList(): LiveData<List<CoinInfo>>

    fun getPopularCoinInfo(fromSymbol: String): LiveData<CoinInfo>

    suspend fun loadData()
}