package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinRepository

class GetCoinInfoUseCase(private val repository: CoinRepository) {

    operator fun invoke(fromSymbol: String) = repository.getCoinInfo(fromSymbol)
}