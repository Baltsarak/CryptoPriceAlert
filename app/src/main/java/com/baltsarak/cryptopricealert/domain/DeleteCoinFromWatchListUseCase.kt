package com.baltsarak.cryptopricealert.domain

class DeleteCoinFromWatchListUseCase(private val repository: CoinRepository) {

    operator fun invoke(fromSymbol: String) = repository.deleteCoinFromWatchList(fromSymbol)
}