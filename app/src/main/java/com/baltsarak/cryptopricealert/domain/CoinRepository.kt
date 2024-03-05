package com.baltsarak.cryptopricealert.domain

import androidx.lifecycle.LiveData

interface CoinRepository {
    suspend fun addCoinToWatchList(fromSymbol: String, targetPrice: Double)

    suspend fun addListToWatchList(watchList: List<CoinInfo>)

    suspend fun deleteCoinFromWatchList(fromSymbol: String)

    suspend fun getWatchListCoins(): List<CoinInfo>

    suspend fun getPopularCoinsList(): LiveData<List<CoinInfo>>

    suspend fun getCoinInfo(fromSymbol: String): LiveData<CoinInfo>

    suspend fun getCurrentCoinPrice(fromSymbol: String): Double

    suspend fun loadCoinPriceHistory(fromSymbol: String)

    suspend fun getCoinPriceHistory(fromSymbol: String): Map<Float, Float>

    suspend fun loadData()
}