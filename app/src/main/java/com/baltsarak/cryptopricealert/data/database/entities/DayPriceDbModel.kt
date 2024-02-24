package com.baltsarak.cryptopricealert.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin_price_history")
data class DayPriceDbModel(
    @PrimaryKey
    val fromSymbol: String,
    var time: Int?,
    var high: Double?,
    var low: Double?,
    var open: Double?,
    var close: Double
)