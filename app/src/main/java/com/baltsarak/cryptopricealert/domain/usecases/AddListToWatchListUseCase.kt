package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.domain.CoinRepository

class AddListToWatchListUseCase(private val repository: CoinRepository) {

    suspend operator fun invoke(list: List<CoinInfo>) =
        repository.addListToWatchList(list)
}