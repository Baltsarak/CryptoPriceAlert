package com.baltsarak.cryptopricealert.domain

class GetPopularCoinInfoUseCase(private val repository: CoinRepository) {

    operator fun invoke(fromSymbol: String) = repository.getPopularCoinInfo(fromSymbol)
}