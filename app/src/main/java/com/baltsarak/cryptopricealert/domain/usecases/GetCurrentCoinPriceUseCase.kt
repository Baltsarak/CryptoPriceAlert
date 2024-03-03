package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinRepository

class GetCurrentCoinPriceUseCase(private val repository: CoinRepository) {

    suspend operator fun invoke(fromSymbol: String): Double =
        repository.getCurrentCoinPrice(fromSymbol)
}