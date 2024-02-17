package com.baltsarak.cryptopricealert.domain

class GetWatchListCoinsUseCase(private val repository: CoinRepository) {

    operator fun invoke() = repository.getWatchListCoins()
}