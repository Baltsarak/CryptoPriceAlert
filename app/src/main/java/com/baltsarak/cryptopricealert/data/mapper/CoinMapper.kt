package com.baltsarak.cryptopricealert.data.mapper

import com.baltsarak.cryptopricealert.data.database.entities.CoinInfoDbModel
import com.baltsarak.cryptopricealert.data.database.entities.DayPriceDbModel
import com.baltsarak.cryptopricealert.data.database.entities.WatchListCoinDbModel
import com.baltsarak.cryptopricealert.data.network.models.CoinNameDto
import com.baltsarak.cryptopricealert.data.network.models.DayPriceDto
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.domain.TargetPrice

class CoinMapper {

    fun mapTargetPriceToDbModel(targetPrice: TargetPrice) = WatchListCoinDbModel(
        id = 0,
        fromSymbol = targetPrice.fromSymbol,
        targetPrice = targetPrice.targetPrice,
        higherThenCurrent = targetPrice.higherThenCurrent,
        position = targetPrice.position
    )

    fun mapDtoToDbModel(coin: CoinNameDto, coinPrice: Double?) = CoinInfoDbModel(
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

