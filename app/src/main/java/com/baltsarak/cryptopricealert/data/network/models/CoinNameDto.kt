package com.baltsarak.cryptopricealert.data.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CoinNameDto(
    @SerializedName("ID")
    @Expose
    val id: Int,

    @SerializedName("SYMBOL")
    @Expose
    val symbol: String,

    @SerializedName("NAME")
    @Expose
    val name: String?,

    @SerializedName("LOGO_URL")
    @Expose
    val imageUrl: String?
)