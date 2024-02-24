package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinRepository

class LoadCoinPriceHistoryInfoUseCase(private val repository: CoinRepository) {

    suspend operator fun invoke(fromSymbol: String) = repository.loadCoinPriceHistory(fromSymbol)
}