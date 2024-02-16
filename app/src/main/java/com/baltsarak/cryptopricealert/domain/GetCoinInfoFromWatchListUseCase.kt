package com.baltsarak.cryptopricealert.domain

class GetCoinInfoFromWatchListUseCase(private val repository: CoinRepository) {

    operator fun invoke(fromSymbol: String) = repository.getCoinInfoFromWatchList(fromSymbol)
}