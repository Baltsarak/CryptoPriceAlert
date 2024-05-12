package com.baltsarak.cryptopricealert.presentation

import android.app.Application
import com.baltsarak.cryptopricealert.di.DaggerApplicationComponent

class CryptoApp: Application() {

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }
}