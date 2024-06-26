package com.baltsarak.cryptopricealert.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_list_coins")
data class WatchListCoinDbModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val fromSymbol: String,
    val targetPrice: Double?,
    val higherThenCurrent: Boolean?,
    val position: Int
)
