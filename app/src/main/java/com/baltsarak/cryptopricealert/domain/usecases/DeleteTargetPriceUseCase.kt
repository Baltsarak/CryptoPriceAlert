package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinRepository

class DeleteTargetPriceUseCase(private val repository: CoinRepository) {

    suspend operator fun invoke(fromSymbol: String, price: Double) =
        repository.deleteTargetPrice(fromSymbol, price)
}