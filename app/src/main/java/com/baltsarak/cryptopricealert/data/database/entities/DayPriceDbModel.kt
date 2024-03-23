package com.baltsarak.cryptopricealert.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin_price_history")
data class DayPriceDbModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val fromSymbol: String,
    val date: Int,
    val price: Double
)