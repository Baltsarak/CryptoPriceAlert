package com.baltsarak.cryptopricealert.data.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CoinInfoContainerDto (
    @SerializedName("Data")
    @Expose
    val coinInfo: CoinNameDto
)