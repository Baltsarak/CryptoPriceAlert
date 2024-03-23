package com.baltsarak.cryptopricealert.data.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DayPriceDto(
    @SerializedName("time")
    @Expose
    var date: Int?,

    @SerializedName("close")
    @Expose
    var price: Double?
)
