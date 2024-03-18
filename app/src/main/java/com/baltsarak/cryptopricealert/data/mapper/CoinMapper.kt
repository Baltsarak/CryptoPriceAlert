package com.baltsarak.cryptopricealert.data.mapper

import com.baltsarak.cryptopricealert.data.database.entities.CoinInfoDbModel
import com.baltsarak.cryptopricealert.data.database.entities.DayPriceDbModel
import com.baltsarak.cryptopricealert.data.network.models.CoinInfoDto
import com.baltsarak.cryptopricealert.data.network.models.CoinSymbolDto
import com.baltsarak.cryptopricealert.data.network.models.CoinSymbolsContainerDto
import com.baltsarak.cryptopricealert.data.network.models.DayPriceDto
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.google.gson.Gson

class CoinMapper {

    fun mapDtoToDbModel(coin: CoinInfoDto, coinPrice: Double?) = CoinInfoDbModel(
        id = coin.id,
        fromSymbol = coin.symbol,
        fullName = coin.name,
        toSymbol = "USD",
        price = coinPrice,
        imageUrl = coin.imageUrl
    )

    fun mapDbModelToEntity(dbModel: CoinInfoDbModel) = CoinInfo(
        id = dbModel.id,
        fromSymbol = dbModel.fromSymbol,
        fullName = dbModel.fullName,
        toSymbol = dbModel.toSymbol,
        targetPrice = listOf(),
        price = dbModel.price,
        imageUrl = dbModel.imageUrl
    )

    fun mapDayPriceDtoToDbModel(fSym: String, dto: DayPriceDto): DayPriceDbModel {
        return DayPriceDbModel(
            id = 0,
            fromSymbol = fSym,
            time = dto.time
                ?: throw RuntimeException("DATA LOADING ERROR: price history not received"),
            high = dto.high,
            low = dto.low,
            open = dto.open,
            close = dto.close
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

    fun listChunking(originalList: List<String>, chunkSize: Int): List<List<String>> {
        return originalList.withIndex()
            .groupBy { it.index / chunkSize }
            .map { it.value.map(IndexedValue<String>::value) }
    }

//    private fun convertTimestampToTime(timestamp: Long?): String {
//        if (timestamp == null) return ""
//        val stamp = Timestamp(timestamp * 1000)
//        val date = Date(stamp.time)
//        val pattern = "HH:mm:ss"
//        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
//        sdf.timeZone = TimeZone.getDefault()
//        return sdf.format(date)
//    }

}

