package com.baltsarak.cryptopricealert.presentation.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baltsarak.cryptopricealert.domain.entities.CoinName
import com.baltsarak.cryptopricealert.domain.usecases.GetCoinListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetPopularCoinListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.LoadDataUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

class CoinListsViewModel @Inject constructor(
    private val getPopularCoinListUseCase: GetPopularCoinListUseCase,
    private val getCoinListUseCase: GetCoinListUseCase,
    private val loadDataUseCase: LoadDataUseCase
) : ViewModel() {

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