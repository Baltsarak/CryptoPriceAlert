package com.baltsarak.cryptopricealert.presentation.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baltsarak.cryptopricealert.data.repository.CoinRepositoryImpl
import com.baltsarak.cryptopricealert.domain.entities.CoinInfo
import com.baltsarak.cryptopricealert.domain.usecases.DeleteAllFromWatchListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.DeleteCoinFromWatchListUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetAndAddCoinToLocalDatabaseUseCase
import com.baltsarak.cryptopricealert.domain.usecases.GetWatchListCoinsUseCase
import com.baltsarak.cryptopricealert.domain.usecases.RewriteWatchListInRemoteDatabaseUseCase
import com.baltsarak.cryptopricealert.domain.usecases.RewriteWatchListUseCase
import kotlinx.coroutines.launch

class WatchListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CoinRepositoryImpl(application)

    private val getWatchListCoinsUseCase = GetWatchListCoinsUseCase(repository)
    private val rewriteWatchListUseCase = RewriteWatchListUseCase(repository)
    private val deleteCoinFromWatchListUseCase = DeleteCoinFromWatchListUseCase(repository)
    private val deleteAllFromWatchListUseCase = DeleteAllFromWatchListUseCase(repository)
    private val getAndAddCoinToLocalDatabaseUseCase =
        GetAndAddCoinToLocalDatabaseUseCase(repository)
    private val rewriteWatchListInRemoteDatabaseUseCase =
        RewriteWatchListInRemoteDatabaseUseCase(repository)

    val watchListCoins = repository.watchListLiveData

    suspend fun getWatchListCoins() = getWatchListCoinsUseCase()

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