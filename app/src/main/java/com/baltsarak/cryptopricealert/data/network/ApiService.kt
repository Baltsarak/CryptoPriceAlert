package com.baltsarak.cryptopricealert.data.network

import com.baltsarak.cryptopricealert.data.network.models.CoinInfoJsonContainerDto
import com.baltsarak.cryptopricealert.data.network.models.CoinListDto
import com.baltsarak.cryptopricealert.data.network.models.CoinPriceDto
import com.baltsarak.cryptopricealert.data.network.models.DayPriceContainerDto
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("top/totalvolfull")
    suspend fun getPopularCoinsList(
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_LIMIT) limit: Int = 10,
        @Query(QUERY_PARAM_TO_SYMBOL) tSym: String = CURRENCY
    ): CoinListDto

    @GET("pricemultifull")
    suspend fun getFullInfoAboutCoins(
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_FROM_SYMBOLS) fSyms: String,
        @Query(QUERY_PARAM_TO_SYMBOLS) tSyms: String = CURRENCY
    ): CoinInfoJsonContainerDto

    @GET("price")
    suspend fun getCoinPrice(
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_FROM_SYMBOL) fSym: String,
        @Query(QUERY_PARAM_TO_SYMBOLS) tSyms: String = CURRENCY
    ): CoinPriceDto

    @GET("v2/histoday")
    suspend fun getCoinPriceHistory(
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_FROM_SYMBOL) fSym: String,
        @Query(QUERY_PARAM_TO_SYMBOL) tSym: String = CURRENCY,
        @Query(QUERY_PARAM_TO_DATE) toTs: Long = YEAR_AGO
    ): DayPriceContainerDto

    companion object {
        private const val QUERY_PARAM_API_KEY = "api_key"
        private const val QUERY_PARAM_TO_SYMBOL = "tsym"
        private const val QUERY_PARAM_TO_SYMBOLS = "tsyms"
        private const val QUERY_PARAM_FROM_SYMBOL = "fsym"
        private const val QUERY_PARAM_FROM_SYMBOLS = "fsyms"
        private const val QUERY_PARAM_LIMIT = "limit"
        private const val QUERY_PARAM_TO_DATE = "toTs"

        private const val API_KEY = ""
        private const val CURRENCY = "USD"
        private val YEAR_AGO = System.currentTimeMillis() - 31556926
    }
}