package com.baltsarak.cryptopricealert.domain

data class TargetPrice(
    val fromSymbol: String,
    val targetPrice: Double,
    val higherThenCurrent: Boolean
)
