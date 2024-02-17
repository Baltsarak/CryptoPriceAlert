package com.baltsarak.cryptopricealert.domain

class AddCoinToWatchListUseCase(private val repository: CoinRepository) {

    operator fun invoke(fromSymbol: String) = repository.addCoinToWatchList(fromSymbol)
}