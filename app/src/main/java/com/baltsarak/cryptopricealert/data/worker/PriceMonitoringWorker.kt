package com.baltsarak.cryptopricealert.data.worker

import android.app.Application
import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.baltsarak.cryptopricealert.data.database.AppDatabase
import com.baltsarak.cryptopricealert.data.network.ApiFactory
import com.baltsarak.cryptopricealert.data.repository.CoinRepositoryImpl
import com.baltsarak.cryptopricealert.domain.entities.TargetPrice
import kotlinx.coroutines.delay

class PriceMonitoringWorker(
    context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    private val repository = CoinRepositoryImpl(context as Application)
    private val apiService = ApiFactory.apiService
    private val watchListCoinInfoDao =
        AppDatabase.getInstance(context).watchListCoinInfoDao()

    override suspend fun doWork(): Result {
        while (true) {
            val targetPrices = watchListCoinInfoDao.getTargetPrices()
            val listIsEmpty = targetPrices.all { it.targetPrice == 0.0 }
            if (!listIsEmpty) {
                for (targetPrice in targetPrices) {
                    if (targetPrice.targetPrice > 0) {
                        checkPrice(targetPrice)
                    }
                }
            } else {
                return Result.success()
            }
            delay(30_000)
        }
    }

    private suspend fun checkPrice(targetPrice: TargetPrice) {
        val currentPrice = apiService.getCoinPrice(fSym = targetPrice.fromSymbol)

        val isTargetReached = if (targetPrice.higherThenCurrent) {
            currentPrice.price >= targetPrice.targetPrice
        } else {
            currentPrice.price <= targetPrice.targetPrice
        }

        if (isTargetReached) {
            showNotification(targetPrice)
            repository.deleteTargetPrice(
                targetPrice.fromSymbol,
                targetPrice.targetPrice
            )
        }
    }

    private fun showNotification(targetPrice: TargetPrice) {
        val notificationHelper = NotificationHelper(applicationContext)
        notificationHelper.createNotificationChannel()
        notificationHelper.sendNotification(
            targetPrice.id,
            "Price Alert",
            "Цена ${targetPrice.fromSymbol} достигла: ${targetPrice.targetPrice}"
        )
    }

    companion object {
        const val NAME = "PriceMonitoringWorker"

        val WORK_REQUEST = OneTimeWorkRequestBuilder<PriceMonitoringWorker>()
            .setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            )
            .build()
    }
}