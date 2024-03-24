package com.baltsarak.cryptopricealert.domain

data class TargetPrice(
    val id: Int,
    val fromSymbol: String,
    val targetPrice: Double,
    val higherThenCurrent: Boolean,
    val position: Int
)

fun List<TargetPrice?>.toPriceString(): String {
    return this.filterNotNull()
        .filter { it.targetPrice != 0.0 }
        .joinToString(separator = "\n") { targetPrice ->
            val arrow = if (targetPrice.higherThenCurrent) "↑" else "↓"
            "$arrow ${targetPrice.targetPrice}"
        }
}

