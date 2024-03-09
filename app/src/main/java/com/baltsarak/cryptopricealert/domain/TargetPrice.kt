package com.baltsarak.cryptopricealert.domain

data class TargetPrice(
    val id: Int,
    val fromSymbol: String,
    val targetPrice: Double,
    val higherThenCurrent: Boolean,
    val position: Int
)
