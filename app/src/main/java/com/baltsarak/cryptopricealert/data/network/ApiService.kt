package com.baltsarak.cryptopricealert.data.network

import com.baltsarak.cryptopricealert.data.network.models.CoinInfoContainerDto
import com.baltsarak.cryptopricealert.data.network.models.CoinListContainerDto
import com.baltsarak.cryptopricealert.data.network.models.CoinPriceDto
import com.baltsarak.cryptopricealert.data.network.models.CoinSymbolsContainerDto
import com.baltsarak.cryptopricealert.data.network.models.DayPriceContainerDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface ApiService {
    @GET("asset/v1/top/list")
    suspend fun getPopularCoinsList(
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_PAGE) page: Int = 1,
        @Query(QUERY_PARAM_PAGE_SIZE) pageSize: Int = 50,
    ): CoinListContainerDto

    @GET
    suspend fun getAllCoinsList(
        @Url url: String = "https://min-api.cryptocompare.com/data/blockchain/list",
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY
    ): CoinSymbolsContainerDto

    @GET("asset/v1/data/by/symbol")
    suspend fun getCoinInfo(
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_ASSET_SYMBOL) assetSymbol: String
    ): CoinInfoContainerDto

    @GET
    suspend fun getCoinPrice(
        @Url url: String = "https://min-api.cryptocompare.com/data/price",
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_FROM_SYMBOL) fSym: String,
        @Query(QUERY_PARAM_TO_SYMBOLS) tSyms: String = CURRENCY
    ): CoinPriceDto

    @GET
    suspend fun getCoinPrices(
        @Url url: String = "https://min-api.cryptocompare.com/data/pricemulti",
        @Query(QUERY_PARAM_FROM_SYMBOLS) fSyms: String,
        @Query(QUERY_PARAM_TO_SYMBOLS) tSyms: String = CURRENCY
    ): Response<Map<String, Map<String, Double>>>

    @GET
    suspend fun getCoinPriceHistory(
        @Url url: String = "https://min-api.cryptocompare.com/data/v2/histoday",
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_FROM_SYMBOL) fSym: String,
        @Query(QUERY_PARAM_TO_SYMBOL) tSym: String = CURRENCY,
        @Query(QUERY_PARAM_ALL_DATA) allData: Boolean = true
    ): DayPriceContainerDto

    companion object {
        private const val QUERY_PARAM_API_KEY = "api_key"
        private const val QUERY_PARAM_TO_SYMBOL = "tsym"
        private const val QUERY_PARAM_TO_SYMBOLS = "tsyms"
        private const val QUERY_PARAM_FROM_SYMBOL = "fsym"
        private const val QUERY_PARAM_FROM_SYMBOLS = "fsyms"
        private const val QUERY_PARAM_ASSET_SYMBOL = "asset_symbol"
        private const val QUERY_PARAM_PAGE = "page"
        private const val QUERY_PARAM_PAGE_SIZE = "page_size"
        private const val QUERY_PARAM_ALL_DATA = "allData"

        private const val API_KEY = "8bb7cb65f4c4164493d302efbf9f49d779ca02b1c51d526674d578f6e2425874"
        private const val CURRENCY = "USD"
    }
}