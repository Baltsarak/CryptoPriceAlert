package com.baltsarak.cryptopricealert.presentation.models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baltsarak.cryptopricealert.domain.usecases.GetNewsListUseCase
import kotlinx.coroutines.async
import javax.inject.Inject

class NewsViewModel @Inject constructor(
    private val getNewsListUseCase: GetNewsListUseCase
) : ViewModel() {

    suspend fun getNewsList() = viewModelScope.async { getNewsListUseCase() }.await()
}