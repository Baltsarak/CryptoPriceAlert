package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinRepository

class GetNewsListUseCase(private val repository: CoinRepository) {

    suspend operator fun invoke() = repository.getNewsList()
}