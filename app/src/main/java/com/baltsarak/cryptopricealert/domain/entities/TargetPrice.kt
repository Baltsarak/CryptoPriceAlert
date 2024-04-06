package com.baltsarak.cryptopricealert.domain.entities

data class TargetPrice(
    val id: Int,
    val fromSymbol: String,
    val targetPrice: Double,
    val higherThenCurrent: Boolean,
    val position: Int
)

fun TargetPrice.toPriceString(): String {
    val arrow = if (this.higherThenCurrent) "↑" else "↓"
    return "$arrow ${this.targetPrice}"
}

