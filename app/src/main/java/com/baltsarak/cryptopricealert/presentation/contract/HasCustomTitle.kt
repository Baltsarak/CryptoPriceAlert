package com.baltsarak.cryptopricealert.presentation.contract

import androidx.annotation.StringRes

interface HasCustomTitle {
    @StringRes
    fun getTitleRes(): Int
}