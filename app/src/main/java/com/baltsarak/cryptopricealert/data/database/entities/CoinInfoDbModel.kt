package com.baltsarak.cryptopricealert.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coins_info")
data class CoinInfoDbModel(
    @PrimaryKey
    val id: Int,
    val fromSymbol: String,
    val fullName: String?,
    val toSymbol: String?,
    val price: Double?,
    val imageUrl: String?
)