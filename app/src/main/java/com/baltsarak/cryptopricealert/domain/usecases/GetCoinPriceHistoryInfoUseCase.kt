package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinRepository
import javax.inject.Inject

class GetCoinPriceHistoryInfoUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    suspend operator fun invoke(fromSymbol: String, period: Int) =
        repository.getCoinPriceHistory(fromSymbol, period)
}