package com.baltsarak.cryptopricealert.presentation.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baltsarak.cryptopricealert.domain.entities.CoinInfo
import com.baltsarak.cryptopricealert.domain.usecases.DeleteAllFromWatchListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.DeleteCoinFromWatchListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetAndAddCoinToLocalDatabaseUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetWatchListCoinsUseCase
import com.baltsarak.cryptopricealert.domain.usecases.RewriteWatchListInRemoteDatabaseUseCase
import com.baltsarak.cryptopricealert.domain.usecases.RewriteWatchListUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

class WatchListViewModel @Inject constructor(
    private val getWatchListCoinsUseCase: GetWatchListCoinsUseCase,
    private val rewriteWatchListUseCase: RewriteWatchListUseCase,
    private val deleteCoinFromWatchListUseCase: DeleteCoinFromWatchListUseCase,
    private val deleteAllFromWatchListUseCase: DeleteAllFromWatchListUseCase,
    private val getAndAddCoinToLocalDatabaseUseCase: GetAndAddCoinToLocalDatabaseUseCase,
    private val rewriteWatchListInRemoteDatabaseUseCase: RewriteWatchListInRemoteDatabaseUseCase,
) : ViewModel() {

    private var _watchListLiveData = MutableLiveData<List<CoinInfo>>()
    val watchListLiveData: LiveData<List<CoinInfo>> = _watchListLiveData

    suspend fun getWatchListCoins():List<CoinInfo> {
       val watchListCoins = getWatchListCoinsUseCase()
        _watchListLiveData.value = watchListCoins
        return watchListCoins
    }

    fun rewriteWatchList(newList: List<CoinInfo>) {
        viewModelScope.launch { rewriteWatchListUseCase(newList) }
    }

    fun deleteCoinFromWatchList(fromSymbol: String) {
        viewModelScope.launch { deleteCoinFromWatchListUseCase(fromSymbol) }
    }

    fun deleteAllFromWatchList() {
        viewModelScope.launch { deleteAllFromWatchListUseCase() }
    }

    fun getAndAddCoinToLocalDatabase() {
        viewModelScope.launch {
            deleteAllFromWatchListUseCase()
            getAndAddCoinToLocalDatabaseUseCase()
        }
    }

    fun rewriteWatchListInRemoteDatabase() {
        viewModelScope.launch { rewriteWatchListInRemoteDatabaseUseCase() }
    }
}