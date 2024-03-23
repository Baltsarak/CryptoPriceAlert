package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinRepository

class GetCoinPriceHistoryInfoUseCase(private val repository: CoinRepository) {

    suspend operator fun invoke(fromSymbol: String, period: Int) =
        repository.getCoinPriceHistory(fromSymbol, period)
}