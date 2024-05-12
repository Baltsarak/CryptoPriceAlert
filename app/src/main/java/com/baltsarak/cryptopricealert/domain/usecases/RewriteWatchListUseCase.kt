package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinRepository
import com.baltsarak.cryptopricealert.domain.entities.CoinInfo
import javax.inject.Inject

class RewriteWatchListUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    suspend operator fun invoke(list: List<CoinInfo>) =
        repository.rewriteWatchList(list)
}