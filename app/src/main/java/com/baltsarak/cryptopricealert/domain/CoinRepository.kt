package com.baltsarak.cryptopricealert.domain

import androidx.lifecycle.LiveData

interface CoinRepository {
    suspend fun addCoinToWatchList(
        fromSymbol: String,
        targetPrice: Double,
        higherThenCurrentPrice: Boolean
    )

    fun getWatchListLiveData(): LiveData<List<CoinInfo>>

    suspend fun rewriteWatchList(watchList: List<CoinInfo>)

    suspend fun deleteCoinFromWatchList(fromSymbol: String)

    suspend fun getPopularCoinsList(): LiveData<List<CoinInfo>>

    suspend fun getCoinNamesList(): LiveData<List<CoinName>>

    suspend fun getCoinInfo(fromSymbol: String): LiveData<CoinInfo>

    suspend fun getCurrentCoinPrice(fromSymbol: String): Double

    suspend fun loadCoinPriceHistory(fromSymbol: String)

    suspend fun loadCoinInfo(fromSymbol: String): Long

    suspend fun getCoinPriceHistory(fromSymbol: String): Map<Float, Float>

    fun startWorker()

    suspend fun loadData()
}