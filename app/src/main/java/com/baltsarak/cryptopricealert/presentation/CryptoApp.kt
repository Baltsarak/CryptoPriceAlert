package com.baltsarak.cryptopricealert.presentation

import android.app.Application
import androidx.work.Configuration
import com.baltsarak.cryptopricealert.data.worker.CryptoWorkerFactory
import com.baltsarak.cryptopricealert.di.DaggerApplicationComponent
import javax.inject.Inject

class CryptoApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: CryptoWorkerFactory

    val component by lazy {
        DaggerApplicationComponent.factory().create(this)
    }

    override fun onCreate() {
        component.inject(this)
        super.onCreate()
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}