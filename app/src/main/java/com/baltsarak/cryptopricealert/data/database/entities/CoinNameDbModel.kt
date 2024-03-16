package com.baltsarak.cryptopricealert.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin_names")
data class CoinNameDbModel(
    @PrimaryKey
    val fullName: String,
    val imageUrl: String?
)