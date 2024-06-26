package com.baltsarak.cryptopricealert.data.network

import com.baltsarak.cryptopricealert.data.network.models.CoinInfoContainerDto
import com.baltsarak.cryptopricealert.data.network.models.CoinListContainerDto
import com.baltsarak.cryptopricealert.data.network.models.CoinPriceDto
import com.baltsarak.cryptopricealert.data.network.models.CoinSymbolsContainerDto
import com.baltsarak.cryptopricealert.data.network.models.CryptoNewsListDto
import com.baltsarak.cryptopricealert.data.network.models.DayPriceContainerDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url
import javax.inject.Inject

interface ApiService {
    @GET("https://data-api.cryptocompare.com/asset/v1/top/list")
    suspend fun getPopularCoinsList(
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_PAGE) page: Int = 1,
        @Query(QUERY_PARAM_PAGE_SIZE) pageSize: Int = 50,
    ): CoinListContainerDto

    @GET("https://data-api.cryptocompare.com/asset/v1/data/by/symbol")
    suspend fun getCoinInfo(
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_ASSET_SYMBOL) assetSymbol: String
    ): CoinInfoContainerDto

    @GET
    suspend fun getAllCoinsList(
        @Url url: String = "data/blockchain/list",
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY
    ): CoinSymbolsContainerDto

    @GET
    suspend fun getCoinPrice(
        @Url url: String = "data/price",
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_FROM_SYMBOL) fSym: String,
        @Query(QUERY_PARAM_TO_SYMBOLS) tSyms: String = CURRENCY
    ): CoinPriceDto

    @GET
    suspend fun getCoinPrices(
        @Url url: String = "data/pricemulti",
        @Query(QUERY_PARAM_FROM_SYMBOLS) fSyms: String,
        @Query(QUERY_PARAM_TO_SYMBOLS) tSyms: String = CURRENCY
    ): Response<Map<String, Map<String, Double>>>

    @GET
    suspend fun getCoinPriceHistory(
        @Url url: String = "data/v2/histoday",
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_FROM_SYMBOL) fSym: String,
        @Query(QUERY_PARAM_TO_SYMBOL) tSym: String = CURRENCY,
        @Query(QUERY_PARAM_ALL_DATA) allData: Boolean = true
    ): DayPriceContainerDto

    @GET
    suspend fun getCoinPriceHourHistory(
        @Url url: String = "data/v2/histohour",
        @Query(QUERY_PARAM_API_KEY) apiKey: String = API_KEY,
        @Query(QUERY_PARAM_FROM_SYMBOL) fSym: String,
        @Query(QUERY_PARAM_TO_SYMBOL) tSym: String = CURRENCY,
        @Query(QUERY_PARAM_LIMIT) limit: Int = LIMIT,
        @Query(QUERY_PARAM_TO_TS) toTs: Long = WEEK_AGO,
    ): DayPriceContainerDto

    @GET
    suspend fun getCryptoNews(
        @Url url: String = "data/v2/news/",
        @Query(QUERY_PARAM_LANGUAGE) lang: String = LANGUAGE,
        @Query(QUERY_PARAM_SORT_ORDER) sortOrder: String = SORT_ORDER
    ): CryptoNewsListDto

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
        private const val QUERY_PARAM_LIMIT = "limit"
        private const val QUERY_PARAM_TO_TS = "toTs"
        private const val QUERY_PARAM_LANGUAGE = "lang"
        private const val QUERY_PARAM_SORT_ORDER = "sortOrder"

        private const val API_KEY = ""
        private const val CURRENCY = "USD"
        private const val LANGUAGE = "EN"
        private const val LIMIT = 2000
        private const val SORT_ORDER = "top"
        private val WEEK_AGO = System.currentTimeMillis() - 604800
    }
}