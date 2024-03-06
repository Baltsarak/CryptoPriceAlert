package com.baltsarak.cryptopricealert.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkerParameters
import com.baltsarak.cryptopricealert.data.database.AppDatabase
import com.baltsarak.cryptopricealert.data.network.ApiFactory
import com.baltsarak.cryptopricealert.domain.TargetPrice
import kotlinx.coroutines.flow.asFlow
import java.util.concurrent.TimeUnit

class PriceMonitoringWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val apiService = ApiFactory.apiService
    private val watchListCoinInfoDao =
        AppDatabase.getInstance(context).watchListCoinInfoDao()

    override suspend fun doWork(): Result {
        val targetPrices = watchListCoinInfoDao.getTargetPrices().asFlow()

        targetPrices
            .collect {
                checkPrice(it)
            }

        return Result.success()
    }

    private suspend fun checkPrice(targetPrice: TargetPrice) {
        val currentPrice = apiService.getCoinPrice(fSym = targetPrice.fromSymbol)

        val isTargetReached = if (targetPrice.higherThenCurrent) {
            currentPrice.price >= targetPrice.targetPrice
        } else {
            currentPrice.price <= targetPrice.targetPrice
        }

        if (isTargetReached) {
            //TODO оповещение что цена достигла целевой
        }
    }

    companion object {
        const val NAME = "PriceMonitoringWorker"

        val WORK_REQUEST = PeriodicWorkRequestBuilder<PriceMonitoringWorker>(30, TimeUnit.SECONDS)
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()
    }
}