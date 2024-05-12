package com.baltsarak.cryptopricealert.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baltsarak.cryptopricealert.domain.usecases.AddCoinToWatchListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.DeleteTargetPriceUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetCoinInfoUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetCoinPriceHistoryInfoUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetCurrentCoinPriceUseCase
import com.baltsarak.cryptopricealert.domain.usecases.LoadCoinPriceHistoryInfoUseCase
import com.baltsarak.cryptopricealert.domain.usecases.StartWorkerUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class CoinDetailsViewModel @Inject constructor(
    private val loadCoinPriceHistoryInfoUseCase: LoadCoinPriceHistoryInfoUseCase,
    private val getCoinInfoUseCase: GetCoinInfoUseCase,
    private val getCoinPriceHistoryInfoUseCase: GetCoinPriceHistoryInfoUseCase,
    private val addCoinToWatchListUseCase: AddCoinToWatchListUseCase,
    private val getCurrentCoinPriceUseCase: GetCurrentCoinPriceUseCase,
    private val startWorkerUseCase: StartWorkerUseCase,
    private val deleteTargetPriceUseCase: DeleteTargetPriceUseCase
) : ViewModel() {

    suspend fun getCoinDetailInfo(fromSymbol: String) =
        viewModelScope.async { getCoinInfoUseCase(fromSymbol) }.await()

    suspend fun loadCoinPriceHistoryInfo(fromSymbol: String) {
        return viewModelScope.async {
            loadCoinPriceHistoryInfoUseCase(fromSymbol)
        }.await()
    }

    suspend fun getCoinPriceHistory(fromSymbol: String, period: Int) =
        viewModelScope.async { getCoinPriceHistoryInfoUseCase(fromSymbol, period) }.await()

    suspend fun addCoinToWatchList(fromSymbol: String, targetPrice: String): Deferred<Unit> {
        return viewModelScope.async {
            val currentPrice = getCurrentCoinPrice(fromSymbol)
            val priceAlert = targetPrice.trim().toDoubleOrNull()
            if (priceAlert != null && priceAlert > 0) {
                val higherThenCurrentPrice = priceAlert > currentPrice
                addCoinToWatchListUseCase(fromSymbol, priceAlert, higherThenCurrentPrice)
            } else {
                addCoinToWatchListUseCase(fromSymbol, null, false)
            }
        }
    }

    private suspend fun getCurrentCoinPrice(fromSymbol: String) =
        viewModelScope.async { getCurrentCoinPriceUseCase(fromSymbol) }.await()

    fun deleteTargetPrice(fromSymbol: String, price: Double) {
        viewModelScope.launch {
            deleteTargetPriceUseCase(fromSymbol, price)
        }
    }

    fun startWorker() {
        startWorkerUseCase()
    }
}