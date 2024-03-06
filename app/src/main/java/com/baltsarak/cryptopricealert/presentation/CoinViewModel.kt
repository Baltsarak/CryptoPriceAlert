package com.baltsarak.cryptopricealert.presentation

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.baltsarak.cryptopricealert.data.repository.CoinRepositoryImpl
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.domain.usecases.AddCoinToWatchListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.RewriteWatchListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.DeleteCoinFromWatchListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetCoinInfoUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetCoinPriceHistoryInfoUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetCurrentCoinPriceUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetPopularCoinListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetWatchListCoinsUseCase
import com.baltsarak.cryptopricealert.domain.usecases.LoadCoinPriceHistoryInfoUseCase
import com.baltsarak.cryptopricealert.domain.usecases.LoadDataUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CoinViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CoinRepositoryImpl(application)

    private val addCoinToWatchListUseCase = AddCoinToWatchListUseCase(repository)
    private val rewriteWatchListUseCase = RewriteWatchListUseCase(repository)
    private val deleteCoinFromWatchListUseCase = DeleteCoinFromWatchListUseCase(repository)
    private val loadCoinPriceHistoryInfoUseCase = LoadCoinPriceHistoryInfoUseCase(repository)
    private val getCoinPriceHistoryInfoUseCase = GetCoinPriceHistoryInfoUseCase(repository)
    private val getPopularCoinListUseCase = GetPopularCoinListUseCase(repository)
    private val getCoinInfoUseCase = GetCoinInfoUseCase(repository)
    private val getCurrentCoinPriceUseCase = GetCurrentCoinPriceUseCase(repository)
    private val getWatchListCoinsUseCase = GetWatchListCoinsUseCase(repository)
    private val loadDataUseCase = LoadDataUseCase(repository)

    private val _currentCoinPrice = MutableLiveData<Double>()
    val currentCoinPrice: LiveData<Double>
        get() = _currentCoinPrice

    private val _watchList = MutableLiveData<List<CoinInfo>>()
    val watchList: LiveData<List<CoinInfo>>
        get() = _watchList

    suspend fun popularCoinList() =
        viewModelScope.async { getPopularCoinListUseCase() }.await()

    suspend fun getCoinDetailInfo(fromSymbol: String) =
        viewModelScope.async { getCoinInfoUseCase(fromSymbol) }.await()

    suspend fun getCoinPriceHistory(fromSymbol: String) =
        viewModelScope.async { getCoinPriceHistoryInfoUseCase(fromSymbol) }.await()

    suspend fun addCoinToWatchList(fromSymbol: String, targetPrice: String) {
        val priceAlert = targetPrice.trim().toDoubleOrNull()
        if (priceAlert != null && priceAlert > 0) {
            viewModelScope.launch {
                addCoinToWatchListUseCase(fromSymbol, priceAlert)
            }
        } else {
            Log.d("addCoinToWatchList", "Неверное значение цены")
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
            _watchList.value = getWatchListCoinsUseCase()
        }
    }

    fun getCurrentCoinPrice(fromSymbol: String) {
        viewModelScope.launch {
            _currentCoinPrice.value = getCurrentCoinPriceUseCase(fromSymbol)
        }
    }

    fun loadCoinPriceHistoryInfo(fromSymbol: String) {
        viewModelScope.launch {
            loadCoinPriceHistoryInfoUseCase(fromSymbol)
        }
    }

    fun getWatchListCoins() {
        viewModelScope.launch {
            _watchList.value = getWatchListCoinsUseCase()
        }
    }

    init {
        viewModelScope.launch {
            loadDataUseCase()
        }
    }
}