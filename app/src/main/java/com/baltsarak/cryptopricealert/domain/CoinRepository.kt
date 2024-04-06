package com.baltsarak.cryptopricealert.domain

import androidx.lifecycle.LiveData
import com.baltsarak.cryptopricealert.domain.entities.CoinInfo
import com.baltsarak.cryptopricealert.domain.entities.CoinName
import com.baltsarak.cryptopricealert.domain.entities.News

interface CoinRepository {
    suspend fun addCoinToWatchList(
        fromSymbol: String,
        targetPrice: Double?,
        higherThenCurrentPrice: Boolean
    ): Long

    suspend fun getWatchListCoins(): List<CoinInfo>

    suspend fun rewriteWatchList(watchList: List<CoinInfo>)

    suspend fun deleteCoinFromWatchList(fromSymbol: String)

    suspend fun deleteTargetPrice(fromSymbol: String, price: Double)

    suspend fun getPopularCoinsList(): LiveData<List<CoinInfo>>

    suspend fun getCoinsList(): List<CoinName>

    suspend fun getCoinInfo(fromSymbol: String): LiveData<CoinInfo>

    suspend fun getCurrentCoinPrice(fromSymbol: String): Double

    suspend fun loadCoinPriceHistory(fromSymbol: String)

    suspend fun getCoinPriceHistory(fromSymbol: String, period: Int): Map<Float, Float>

    fun startWorker()

    suspend fun loadDataWithNetworkCheck()
    suspend fun getNewsList(): List<News>
}