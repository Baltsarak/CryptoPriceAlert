package com.baltsarak.cryptopricealert.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baltsarak.cryptopricealert.data.repository.CoinRepositoryImpl
import com.baltsarak.cryptopricealert.domain.AddCoinToWatchListUseCase
import com.baltsarak.cryptopricealert.domain.DeleteCoinFromWatchListUseCase
import com.baltsarak.cryptopricealert.domain.GetCoinInfoFromWatchListUseCase
import com.baltsarak.cryptopricealert.domain.GetPopularCoinInfoListUseCase
import com.baltsarak.cryptopricealert.domain.GetPopularCoinInfoUseCase
import com.baltsarak.cryptopricealert.domain.GetWatchListCoinsUseCase
import com.baltsarak.cryptopricealert.domain.LoadDataUseCase
import kotlinx.coroutines.launch

class CoinViewModel(application: Application): AndroidViewModel(application) {

    private val repository = CoinRepositoryImpl(application)

    private val addCoinToWatchListUseCase = AddCoinToWatchListUseCase(repository)
    private val deleteCoinFromWatchListUseCase = DeleteCoinFromWatchListUseCase(repository)
    private val getCoinInfoFromWatchListUseCase = GetCoinInfoFromWatchListUseCase(repository)
    private val getPopularCoinInfoListUseCase = GetPopularCoinInfoListUseCase(repository)
    private val getPopularCoinInfoUseCase = GetPopularCoinInfoUseCase(repository)
    private val getWatchListCoinsUseCase = GetWatchListCoinsUseCase(repository)
    private val loadDataUseCase = LoadDataUseCase(repository)

    val popularCoinList = getPopularCoinInfoListUseCase()

    fun getPopularCoinDetailInfo(fSym:String) = getPopularCoinInfoUseCase(fSym)

    fun addCoinToWatchList(fSym: String) = addCoinToWatchListUseCase(fSym)



    init {
        viewModelScope.launch {
            loadDataUseCase()
        }
    }
}