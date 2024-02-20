package com.baltsarak.cryptopricealert.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "coins_info")
data class CoinInfoDbModel(
    @PrimaryKey
    val fromSymbol: String,
    val toSymbol: String?,
    val lastMarket: String?,
    val price: Double?,
    val lastUpdate: Int?,
    val highDay: Double?,
    val lowDay: Double?,
    val imageUrl: String?
)