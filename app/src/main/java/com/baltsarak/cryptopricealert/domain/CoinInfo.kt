package com.baltsarak.cryptopricealert.domain

data class CoinInfo(
    val id: Int,
    val fromSymbol: String,
    val fullName:String?,
    val toSymbol: String?,
    val targetPrice: List<TargetPrice?>,
    val price: Double?,
    val imageUrl: String?
)