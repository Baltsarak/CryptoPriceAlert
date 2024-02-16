package com.baltsarak.cryptopricealert.domain

class GetPopularCoinInfoListUseCase(private val repository: CoinRepository) {

    operator fun invoke() = repository.getPopularCoinInfoList()
}