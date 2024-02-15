package com.baltsarak.cryptopricealert.domain

data class CoinInfo (
    val fromSymbol: String,
    val toSymbol: String?,
    val lastMarket: String?,
    val price: Double?,
    val lastUpdate: Int?,
    val highDay: Double?,
    val lowDay: Double?,
    val imageUrl: String
)