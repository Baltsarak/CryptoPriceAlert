package com.baltsarak.cryptopricealert.domain

import androidx.lifecycle.LiveData

interface CoinRepository {
    suspend fun addCoinToWatchList(fromSymbol: String)

    fun deleteCoinFromWatchList(fromSymbol: String)

    suspend fun getWatchListCoins(): LiveData<List<CoinInfo>>

    suspend fun getPopularCoinsList(): LiveData<List<CoinInfo>>

   fun getCoinInfo(fromSymbol: String): LiveData<CoinInfo>

    suspend fun loadCoinPriceHistory(fromSymbol: String)

    suspend fun loadData()
}