package com.baltsarak.cryptopricealert.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baltsarak.cryptopricealert.data.repository.CoinRepositoryImpl
import com.baltsarak.cryptopricealert.domain.usecases.AddCoinToWatchListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.DeleteCoinFromWatchListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetCoinInfoUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetCoinPriceHistoryInfoUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetPopularCoinListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetWatchListCoinsUseCase
import com.baltsarak.cryptopricealert.domain.usecases.LoadCoinPriceHistoryInfoUseCase
import com.baltsarak.cryptopricealert.domain.usecases.LoadDataUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CoinViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CoinRepositoryImpl(application)

    private val addCoinToWatchListUseCase = AddCoinToWatchListUseCase(repository)
    private val deleteCoinFromWatchListUseCase = DeleteCoinFromWatchListUseCase(repository)
    private val loadCoinPriceHistoryInfoUseCase = LoadCoinPriceHistoryInfoUseCase(repository)
    private val getCoinPriceHistoryInfoUseCase = GetCoinPriceHistoryInfoUseCase(repository)
    private val getPopularCoinListUseCase = GetPopularCoinListUseCase(repository)
    private val getCoinInfoUseCase = GetCoinInfoUseCase(repository)
    private val getWatchListCoinsUseCase = GetWatchListCoinsUseCase(repository)
    private val loadDataUseCase = LoadDataUseCase(repository)

    suspend fun popularCoinList() = viewModelScope.async { getPopularCoinListUseCase() }.await()
    suspend fun watchList() = viewModelScope.async { getWatchListCoinsUseCase() }.await()
    suspend fun getCoinDetailInfo(fSym: String) = viewModelScope.async { getCoinInfoUseCase(fSym) }.await()

    fun addCoinToWatchList(fSym: String) {
        viewModelScope.launch {
            addCoinToWatchListUseCase(fSym)
        }
    }

    init {
        viewModelScope.launch {
            loadDataUseCase()
        }
    }
}