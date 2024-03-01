package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinRepository

class AddCoinToWatchListUseCase(private val repository: CoinRepository) {

    suspend operator fun invoke(fromSymbol: String, targetPrice: Double) =
        repository.addCoinToWatchList(fromSymbol, targetPrice)
}