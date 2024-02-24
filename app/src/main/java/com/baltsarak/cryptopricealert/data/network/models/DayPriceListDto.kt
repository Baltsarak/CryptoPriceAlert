package com.baltsarak.cryptopricealert.data.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DayPriceListDto(
    @SerializedName("TimeFrom")
    @Expose
    var timeFrom: Int?,

    @SerializedName("TimeTo")
    @Expose
    var timeTo: Int?,

    @SerializedName("Data")
    @Expose
    var data: List<DayPriceDto>?
)
