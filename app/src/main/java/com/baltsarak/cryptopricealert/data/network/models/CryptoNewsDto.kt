package com.baltsarak.cryptopricealert.data.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class CryptoNewsDto(
    @SerializedName("published_on")
    @Expose
    val publishedOn: Int,
    @SerializedName("imageurl")
    @Expose
    val imageUrl: String,
    @SerializedName("title")
    @Expose
    val title: String,
    @SerializedName("url")
    @Expose
    val url: String,
    @SerializedName("body")
    @Expose
    val body: String
)