package com.baltsarak.cryptopricealert.domain

class AddCoinToWatchListUseCase(private val repository: CoinRepository) {

    suspend operator fun invoke(fromSymbol: String) = repository.addCoinToWatchList(fromSymbol)
}