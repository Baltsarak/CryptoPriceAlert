package com.baltsarak.cryptopricealert.data.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import com.baltsarak.cryptopricealert.data.database.dao.WatchListCoinInfoDao
import com.baltsarak.cryptopricealert.data.network.ApiService
import com.baltsarak.cryptopricealert.domain.CoinRepository
import com.baltsarak.cryptopricealert.domain.entities.TargetPrice
import kotlinx.coroutines.delay
import javax.inject.Inject

class PriceMonitoringWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val repository: CoinRepository,
    private val watchListCoinInfoDao: WatchListCoinInfoDao,
    private val apiService: ApiService
) : CoroutineWorker(context, workerParameters) {

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
            targetPrice
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

    class Factory @Inject constructor(
        private val repository: CoinRepository,
        private val watchListCoinInfoDao: WatchListCoinInfoDao,
        private val apiService: ApiService
    ) : ChildWorkerFactory {
        override fun create(
            context: Context,
            workerParameters: WorkerParameters
        ): ListenableWorker {
            return PriceMonitoringWorker(
                context,
                workerParameters,
                repository,
                watchListCoinInfoDao,
                apiService
            )
        }
    }
}