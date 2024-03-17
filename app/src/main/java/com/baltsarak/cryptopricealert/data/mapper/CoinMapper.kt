package com.baltsarak.cryptopricealert.data.mapper

import com.baltsarak.cryptopricealert.data.database.entities.CoinInfoDbModel
import com.baltsarak.cryptopricealert.data.database.entities.CoinNameDbModel
import com.baltsarak.cryptopricealert.data.database.entities.DayPriceDbModel
import com.baltsarak.cryptopricealert.data.network.models.CoinInfoDto
import com.baltsarak.cryptopricealert.data.network.models.CoinNameContainerDto
import com.baltsarak.cryptopricealert.data.network.models.CoinNameDto
import com.baltsarak.cryptopricealert.data.network.models.DayPriceDto
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.domain.CoinName
import com.google.gson.Gson

class CoinMapper {

    fun mapCoinInfoDtoToDbModel(coin: CoinInfoDto, coinPrice: Double?) = CoinInfoDbModel(
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

    fun mapCoinNameDbModelToEntity(dbModel: CoinNameDbModel) = CoinName(
        fullName = dbModel.fullName,
        symbol = dbModel.symbol,
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

    fun mapCoinNameContainerDtoToCoinNameList(jsonContainer: CoinNameContainerDto): List<CoinNameDto> {
        val result = mutableListOf<CoinNameDto>()
        val jsonObject = jsonContainer.json ?: return result
        val coinKeySet = jsonObject.keySet()
        for (coinKey in coinKeySet) {
            val priceName = Gson().fromJson(
                jsonObject.getAsJsonObject(coinKey),
                CoinNameDto::class.java
            )
            result.add(priceName)
        }
        return result
    }

    fun mapCoinNameDtoToDbModel(coinName: CoinNameDto) = CoinNameDbModel(
        fullName = coinName.fullName,
        symbol = coinName.symbol,
        imageUrl = coinName.imageUrl
    )

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

