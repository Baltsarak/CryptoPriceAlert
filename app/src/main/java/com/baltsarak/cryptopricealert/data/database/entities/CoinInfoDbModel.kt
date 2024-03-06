package com.baltsarak.cryptopricealert.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "coins_info")
data class CoinInfoDbModel(
    @PrimaryKey
    val fromSymbol: String,
    val toSymbol: String?,
    val price: Double?,
    val highDay: Double?,
    val lowDay: Double?,
    val imageUrl: String?
)