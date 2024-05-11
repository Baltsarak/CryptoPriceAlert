package com.baltsarak.cryptopricealert.presentation.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.baltsarak.cryptopricealert.data.repository.CoinRepositoryImpl
import com.baltsarak.cryptopricealert.domain.entities.CoinName
import com.baltsarak.cryptopricealert.domain.usecases.GetCoinListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetPopularCoinListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.LoadDataUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class CoinListsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CoinRepositoryImpl(application)

    private val getPopularCoinListUseCase = GetPopularCoinListUseCase(repository)
    private val getCoinListUseCase = GetCoinListUseCase(repository)
    private val loadDataUseCase = LoadDataUseCase(repository)

    private var _coinListLiveData = MutableLiveData<List<CoinName>>()
    val coinListLiveData: LiveData<List<CoinName>>
        get() = _coinListLiveData

    fun updateCoinListLiveData(newList: List<CoinName>) {
        _coinListLiveData.value = newList
    }

    suspend fun popularCoinList() = viewModelScope.async { getPopularCoinListUseCase() }.await()

    suspend fun getListCoinNames() = viewModelScope.async { getCoinListUseCase() }.await()

    fun loadData() {
        viewModelScope.launch {
            loadDataUseCase()
        }
    }
}