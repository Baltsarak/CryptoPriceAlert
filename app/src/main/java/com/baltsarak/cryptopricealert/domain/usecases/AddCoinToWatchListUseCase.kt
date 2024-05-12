package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinRepository
import javax.inject.Inject

class AddCoinToWatchListUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    suspend operator fun invoke(
        fromSymbol: String,
        targetPrice: Double?,
        higherThenCurrentPrice: Boolean
    ) = repository.addCoinToWatchList(fromSymbol, targetPrice, higherThenCurrentPrice)
}