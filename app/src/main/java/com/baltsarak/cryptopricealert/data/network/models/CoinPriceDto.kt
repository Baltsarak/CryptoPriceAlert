package com.baltsarak.cryptopricealert.data.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CoinPriceDto(
    @SerializedName("PRICE")
    @Expose
    val price: Double?
)