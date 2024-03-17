package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinRepository

class GetCoinNamesListUseCase(private val repository: CoinRepository) {

    suspend operator fun invoke() = repository.getCoinNamesList()
}