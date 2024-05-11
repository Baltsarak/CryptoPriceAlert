package com.baltsarak.cryptopricealert.presentation.models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.baltsarak.cryptopricealert.data.repository.CoinRepositoryImpl
import com.baltsarak.cryptopricealert.domain.usecases.GetNewsListUseCase
import kotlinx.coroutines.async

class NewsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = CoinRepositoryImpl(application)

    private val getNewsListUseCase = GetNewsListUseCase(repository)

    suspend fun getNewsList() = viewModelScope.async { getNewsListUseCase() }.await()
}