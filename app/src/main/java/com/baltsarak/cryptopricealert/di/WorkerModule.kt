package com.baltsarak.cryptopricealert.di

import com.baltsarak.cryptopricealert.data.worker.ChildWorkerFactory
import com.baltsarak.cryptopricealert.data.worker.PriceMonitoringWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(PriceMonitoringWorker::class)
    fun bindPriceMonitoringWorkerFactory(
        worker: PriceMonitoringWorker.Factory
    ): ChildWorkerFactory
}