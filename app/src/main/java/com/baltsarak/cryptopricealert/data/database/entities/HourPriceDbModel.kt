package com.baltsarak.cryptopricealert.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin_price_hour_history")
data class HourPriceDbModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val fromSymbol: String,
    val date: Int,
    val price: Double
)