package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.domain.CoinRepository

class RewriteWatchListUseCase(private val repository: CoinRepository) {

    suspend operator fun invoke(list: List<CoinInfo>) =
        repository.rewriteWatchList(list)
}