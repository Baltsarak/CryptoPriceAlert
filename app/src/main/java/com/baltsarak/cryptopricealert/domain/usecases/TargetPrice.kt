package com.baltsarak.cryptopricealert.domain.usecases

data class TargetPrice(
    val fromSymbol: String,
    val targetPrice: Double?
)