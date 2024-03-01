package com.baltsarak.cryptopricealert.domain

import androidx.lifecycle.LiveData

interface CoinRepository {
    suspend fun addCoinToWatchList(fromSymbol: String, targetPrice: Double)

    fun deleteCoinFromWatchList(fromSymbol: String)

    suspend fun getWatchListCoins(): List<CoinInfo>

    suspend fun getPopularCoinsList(): LiveData<List<CoinInfo>>

   fun getCoinInfo(fromSymbol: String): LiveData<CoinInfo>

    suspend fun loadCoinPriceHistory(fromSymbol: String)

    suspend fun getCoinPriceHistory(fromSymbol: String): Map<Int, Double>

    suspend fun loadData()
}