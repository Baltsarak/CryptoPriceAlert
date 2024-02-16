package com.baltsarak.cryptopricealert.data.mapper

import com.baltsarak.cryptopricealert.data.database.entities.PopularCoinDbModel
import com.baltsarak.cryptopricealert.data.database.entities.WatchListCoinDbModel
import com.baltsarak.cryptopricealert.data.network.models.CoinInfoDto
import com.baltsarak.cryptopricealert.data.network.models.CoinInfoJsonContainerDto
import com.baltsarak.cryptopricealert.data.network.models.CoinListDto
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.google.gson.Gson

class CoinMapper {

    fun mapDtoToDbModel(dto: CoinInfoDto) = PopularCoinDbModel(
        fromSymbol = dto.fromsymbol,
        toSymbol = dto.tosymbol,
        price = dto.price,
        lastMarket = dto.lastmarket,
        lastUpdate = dto.lastupdate,
        highDay = dto.highday,
        lowDay = dto.lowday,
        imageUrl = dto.imageurl
    )

    fun mapDbModelToEntity(dbModel: PopularCoinDbModel) = CoinInfo(
        fromSymbol = dbModel.fromSymbol,
        toSymbol = dbModel.toSymbol,
        price = dbModel.price,
        lastMarket = dbModel.lastMarket,
        lastUpdate = dbModel.lastUpdate,
        highDay = dbModel.highDay,
        lowDay = dbModel.lowDay,
        imageUrl = BASE_IMAGE_URL + dbModel.imageUrl
    )

    fun mapDbModelToEntity(dbModel: WatchListCoinDbModel) = CoinInfo(
        fromSymbol = dbModel.fromSymbol,
        toSymbol = dbModel.toSymbol,
        price = dbModel.price,
        lastMarket = dbModel.lastMarket,
        lastUpdate = dbModel.lastUpdate,
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

    fun mapNamesListToString(coinsListDto: CoinListDto): String {
        return coinsListDto.coins?.map { it.coinName?.name }?.joinToString(",") ?: ""
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

