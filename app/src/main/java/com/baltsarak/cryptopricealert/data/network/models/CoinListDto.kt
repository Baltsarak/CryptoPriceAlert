package com.baltsarak.cryptopricealert.data.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CoinListDto (
    @SerializedName("Data")
    @Expose
    val coins : List<CoinContainerDto>? = null
)