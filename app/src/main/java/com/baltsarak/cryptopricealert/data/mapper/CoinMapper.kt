package com.baltsarak.cryptopricealert.data.mapper

import com.baltsarak.cryptopricealert.data.database.entities.CoinInfoDbModel
import com.baltsarak.cryptopricealert.data.database.entities.DayPriceDbModel
import com.baltsarak.cryptopricealert.data.database.entities.HourPriceDbModel
import com.baltsarak.cryptopricealert.data.network.models.CoinInfoDto
import com.baltsarak.cryptopricealert.data.network.models.CoinSymbolDto
import com.baltsarak.cryptopricealert.data.network.models.CoinSymbolsContainerDto
import com.baltsarak.cryptopricealert.data.network.models.CryptoNewsDto
import com.baltsarak.cryptopricealert.data.network.models.DayPriceDto
import com.baltsarak.cryptopricealert.domain.entities.CoinInfo
import com.baltsarak.cryptopricealert.domain.entities.News
import com.baltsarak.cryptopricealert.domain.entities.TargetPrice
import com.google.gson.Gson
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class CoinMapper {

    fun mapDtoToDbModel(coin: CoinInfoDto?, coinPrice: Double?): CoinInfoDbModel? {
        if (coin == null) {
            return null
        }
        return CoinInfoDbModel(
            id = coin.id,
            fromSymbol = coin.symbol,
            fullName = coin.name,
            toSymbol = "USD",
            price = coinPrice,
            imageUrl = coin.imageUrl
        )
    }

    fun mapDbModelToEntity(dbModel: CoinInfoDbModel, targetPrices: List<TargetPrice>?) =
        CoinInfo(
            id = dbModel.id,
            fromSymbol = dbModel.fromSymbol,
            fullName = dbModel.fullName,
            toSymbol = dbModel.toSymbol,
            targetPrice = targetPrices ?: listOf(),
            price = dbModel.price,
            imageUrl = dbModel.imageUrl
        )

    fun mapDayPriceDtoToDbModel(fSym: String, dto: DayPriceDto): DayPriceDbModel {
        return DayPriceDbModel(
            id = 0,
            fromSymbol = fSym,
            date = dto.date
                ?: throw RuntimeException("DATA LOADING ERROR: price history not received"),
            price = dto.price
                ?: throw RuntimeException("DATA LOADING ERROR: price history not received")
        )
    }

    fun mapHourPriceDtoToDbModel(fSym: String, dto: DayPriceDto): HourPriceDbModel {
        return HourPriceDbModel(
            id = 0,
            fromSymbol = fSym,
            date = dto.date
                ?: throw RuntimeException("DATA LOADING ERROR: price history not received"),
            price = dto.price
                ?: throw RuntimeException("DATA LOADING ERROR: price history not received")
        )
    }

    fun mapCoinSymbolsContainerDtoToCoinSymbolsList(jsonContainer: CoinSymbolsContainerDto): List<CoinSymbolDto> {
        val result = mutableListOf<CoinSymbolDto>()
        val jsonObject = jsonContainer.json ?: return result
        val coinKeySet = jsonObject.keySet()
        for (coinKey in coinKeySet) {
            val coinSymbol = Gson().fromJson(
                jsonObject.getAsJsonObject(coinKey),
                CoinSymbolDto::class.java
            )
            result.add(coinSymbol)
        }
        return result
    }

    fun mapNewsDtoToEntity(newsDto: CryptoNewsDto): News {
        return News(
            publishedOn = convertTimestampToTime(newsDto.publishedOn.toLong()),
            imageUrl = newsDto.imageUrl,
            title = newsDto.title,
            url = newsDto.url,
            body = newsDto.body
        )
    }

    fun listChunking(originalList: List<String>, chunkSize: Int): List<List<String>> {
        return originalList.withIndex()
            .groupBy { it.index / chunkSize }
            .map { it.value.map(IndexedValue<String>::value) }
    }

    private fun convertTimestampToTime(timestamp: Long?): String {
        if (timestamp == null) return ""
        val stamp = Timestamp(timestamp * 1000)
        val date = Date(stamp.time)
        val pattern = "d MMMM yyyy HH:mm:ss"
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.timeZone = TimeZone.getDefault()
        return sdf.format(date)
    }
}

