package com.baltsarak.cryptopricealert.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.baltsarak.cryptopricealert.data.repository.CoinRepositoryImpl
import com.baltsarak.cryptopricealert.domain.entities.CoinInfo
import com.baltsarak.cryptopricealert.domain.entities.CoinName
import com.baltsarak.cryptopricealert.domain.usecases.AddCoinToWatchListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.DeleteAllFromWatchListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.DeleteCoinFromWatchListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.DeleteTargetPriceUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetAndAddCoinToLocalDatabaseUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetCoinInfoUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetCoinListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetCoinPriceHistoryInfoUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetCurrentCoinPriceUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetNewsListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetPopularCoinListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetWatchListCoinsUseCase
import com.baltsarak.cryptopricealert.domain.usecases.LoadCoinPriceHistoryInfoUseCase
import com.baltsarak.cryptopricealert.domain.usecases.LoadDataUseCase
import com.baltsarak.cryptopricealert.domain.usecases.RewriteWatchListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.StartWorkerUseCase
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CoinViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CoinRepositoryImpl(application)

    private val addCoinToWatchListUseCase = AddCoinToWatchListUseCase(repository)
    private val rewriteWatchListUseCase = RewriteWatchListUseCase(repository)
    private val deleteCoinFromWatchListUseCase = DeleteCoinFromWatchListUseCase(repository)
    private val deleteAllFromWatchListUseCase = DeleteAllFromWatchListUseCase(repository)
    private val deleteTargetPriceUseCase = DeleteTargetPriceUseCase(repository)
    private val loadCoinPriceHistoryInfoUseCase = LoadCoinPriceHistoryInfoUseCase(repository)
    private val getCoinPriceHistoryInfoUseCase = GetCoinPriceHistoryInfoUseCase(repository)
    private val getAndAddCoinToLocalDatabaseUseCase =
        GetAndAddCoinToLocalDatabaseUseCase(repository)
    private val getPopularCoinListUseCase = GetPopularCoinListUseCase(repository)
    private val getCoinListUseCase = GetCoinListUseCase(repository)
    private val getNewsListUseCase = GetNewsListUseCase(repository)
    private val getCoinInfoUseCase = GetCoinInfoUseCase(repository)
    private val getCurrentCoinPriceUseCase = GetCurrentCoinPriceUseCase(repository)
    private val getWatchListCoinsUseCase = GetWatchListCoinsUseCase(repository)
    private val loadDataUseCase = LoadDataUseCase(repository)
    private val startWorkerUseCase = StartWorkerUseCase(repository)

    private var _coinListLiveData = MutableLiveData<List<CoinName>>()
    val coinListLiveData: LiveData<List<CoinName>>
        get() = _coinListLiveData

    val watchListCoins = repository.watchListLiveData

    fun updateCoinListLiveData(newList: List<CoinName>) {
        _coinListLiveData.value = newList
    }

    suspend fun getNewsList() = viewModelScope.async { getNewsListUseCase() }.await()

    suspend fun popularCoinList() =
        viewModelScope.async { getPopularCoinListUseCase() }.await()

    suspend fun getWatchListCoins() = getWatchListCoinsUseCase()

    fun getAndAddCoinToLocalDatabase() {
        viewModelScope.launch {
            deleteAllFromWatchListUseCase()
            getAndAddCoinToLocalDatabaseUseCase()
        }
    }

    fun deleteAllFromWatchList() {
        viewModelScope.launch { deleteAllFromWatchListUseCase() }
    }

    suspend fun getListCoinNames() =
        viewModelScope.async { getCoinListUseCase() }.await()

    private suspend fun getCurrentCoinPrice(fromSymbol: String) =
        viewModelScope.async { getCurrentCoinPriceUseCase(fromSymbol) }.await()

    suspend fun getCoinDetailInfo(fromSymbol: String) =
        viewModelScope.async { getCoinInfoUseCase(fromSymbol) }.await()

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
                Log.d("addCoinToWatchList", "Неверное значение цены")
                addCoinToWatchListUseCase(fromSymbol, null, false)
            }
        }
    }

    fun rewriteWatchList(newList: List<CoinInfo>) {
        viewModelScope.launch {
            rewriteWatchListUseCase(newList)
        }
    }

    fun deleteCoinFromWatchList(fromSymbol: String) {
        viewModelScope.launch {
            deleteCoinFromWatchListUseCase(fromSymbol)
        }
    }

    fun deleteTargetPrice(fromSymbol: String, price: Double) {
        viewModelScope.launch {
            deleteTargetPriceUseCase(fromSymbol, price)
        }
    }

    suspend fun loadCoinPriceHistoryInfo(fromSymbol: String) {
        return viewModelScope.async {
            loadCoinPriceHistoryInfoUseCase(fromSymbol)
        }.await()
    }

    fun startWorker() {
        startWorkerUseCase()
    }

    fun loadData() {
        viewModelScope.launch {
            loadDataUseCase()
        }
    }
}