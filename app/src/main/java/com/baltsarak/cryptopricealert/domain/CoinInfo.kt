package com.baltsarak.cryptopricealert.domain

data class CoinInfo(
    val fromSymbol: String,
    val toSymbol: String?,
    val targetPrice: List<Double?>,
    val price: Double?,
    val highDay: Double?,
    val lowDay: Double?,
    val imageUrl: String
)