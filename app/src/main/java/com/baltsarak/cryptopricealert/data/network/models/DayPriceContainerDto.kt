package com.baltsarak.cryptopricealert.data.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class DayPriceContainerDto(
    @SerializedName("Response")
    @Expose
    var response: String?,

    @SerializedName("Message")
    @Expose
    var message: String?,

    @SerializedName("HasWarning")
    @Expose
    var hasWarning: Boolean?,

    @SerializedName("Type")
    @Expose
    var type: Int?,

    @SerializedName("Data")
    @Expose
    var data: DayPriceListDto?
)

