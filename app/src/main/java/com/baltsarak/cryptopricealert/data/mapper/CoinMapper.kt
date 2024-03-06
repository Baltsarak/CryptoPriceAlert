package com.baltsarak.cryptopricealert.data.mapper

import com.baltsarak.cryptopricealert.data.database.entities.CoinInfoDbModel
import com.baltsarak.cryptopricealert.data.database.entities.DayPriceDbModel
import com.baltsarak.cryptopricealert.data.network.models.CoinInfoDto
import com.baltsarak.cryptopricealert.data.network.models.CoinInfoJsonContainerDto
import com.baltsarak.cryptopricealert.data.network.models.DayPriceDto
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.google.gson.Gson

class CoinMapper {

    fun mapDtoToDbModel(dto: CoinInfoDto) = CoinInfoDbModel(
        fromSymbol = dto.fromsymbol,
        toSymbol = dto.tosymbol,
        price = dto.price,
        highDay = dto.highday,
        lowDay = dto.lowday,
        imageUrl = dto.imageurl
    )

    fun mapDbModelToEntity(dbModel: CoinInfoDbModel) = CoinInfo(
        fromSymbol = dbModel.fromSymbol,
        toSymbol = dbModel.toSymbol,
        targetPrice = listOf(),
        price = dbModel.price,
        highDay = dbModel.highDay,
        lowDay = dbModel.lowDay,
        imageUrl = BASE_IMAGE_URL + dbModel.imageUrl
    )

    fun mapJsonContainerToListCoinInfo(jsonContainer: CoinInfoJsonContainerDto): List<CoinInfoDto> {
        val result = mutableListOf<CoinInfoDto>()
        val jsonObject = jsonContainer.json ?: return result
        val coinKeySet = jsonObject.keySet()
        for (coinKey in coinKeySet) {
            val currencyJson = jsonObject.getAsJsonObject(coinKey)
            val currencyKeySet = currencyJson.keySet()
            for (currencyKey in currencyKeySet) {
                val priceInfo = Gson().fromJson(
                    currencyJson.getAsJsonObject(currencyKey),
                    CoinInfoDto::class.java
                )
                result.add(priceInfo)
            }
        }
        return result
    }

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

//    private fun convertTimestampToTime(timestamp: Long?): String {
//        if (timestamp == null) return ""
//        val stamp = Timestamp(timestamp * 1000)
//        val date = Date(stamp.time)
//        val pattern = "HH:mm:ss"
//        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
//        sdf.timeZone = TimeZone.getDefault()
//        return sdf.format(date)
//    }

    companion object {
        const val BASE_IMAGE_URL = "https://cryptocompare.com"
    }
}

