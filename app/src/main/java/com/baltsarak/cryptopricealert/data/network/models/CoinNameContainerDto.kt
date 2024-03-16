package com.baltsarak.cryptopricealert.data.network.models

import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CoinNameContainerDto (
    @SerializedName("Data")
    @Expose
    val json: JsonObject?
)