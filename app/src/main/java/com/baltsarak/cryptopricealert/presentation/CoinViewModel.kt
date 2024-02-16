package com.baltsarak.cryptopricealert.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baltsarak.cryptopricealert.data.repository.CoinRepositoryImpl
import com.baltsarak.cryptopricealert.domain.GetCoinInfoFromWatchListUseCase
import com.baltsarak.cryptopricealert.domain.GetPopularCoinInfoListUseCase
import com.baltsarak.cryptopricealert.domain.GetPopularCoinInfoUseCase
import com.baltsarak.cryptopricealert.domain.LoadDataUseCase
import kotlinx.coroutines.launch

class CoinViewModel(application: Application): AndroidViewModel(application) {

    private val repository = CoinRepositoryImpl(application)

    private val getCoinInfoFromWatchListUseCase = GetCoinInfoFromWatchListUseCase(repository)
    private val getPopularCoinInfoListUseCase = GetPopularCoinInfoListUseCase(repository)
    private val getPopularCoinInfoUseCase = GetPopularCoinInfoUseCase(repository)
    private val loadDataUseCase = LoadDataUseCase(repository)

    val coinInfoList = getPopularCoinInfoListUseCase()

    fun getDetailInfo(fSym:String) = getPopularCoinInfoUseCase(fSym)

    init {
        viewModelScope.launch {
            loadDataUseCase()
        }
    }
}