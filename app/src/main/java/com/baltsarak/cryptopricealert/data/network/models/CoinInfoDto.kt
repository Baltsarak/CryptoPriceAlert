package com.baltsarak.cryptopricealert.data.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

<<<<<<<< HEAD:app/src/main/java/com/baltsarak/cryptopricealert/data/network/models/CoinInfoDto.kt
data class CoinInfoDto(
    @SerializedName("ID")
    @Expose
    val id: Int,
========
data class CoinNameDto(
>>>>>>>> 37606a5 (adding functionality for downloading a list of names all coins):app/src/main/java/com/baltsarak/cryptopricealert/data/network/models/CoinNameDto.kt

    @SerializedName("FullName")
    @Expose
    val fullName: String,

    @SerializedName("ImageUrl")
    @Expose
    val imageUrl: String?
)