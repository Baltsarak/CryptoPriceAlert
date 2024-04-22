package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinRepository

class RewriteWatchListInRemoteDatabaseUseCase(private val repository: CoinRepository) {

    suspend operator fun invoke() = repository.rewriteWatchListInRemoteDatabase()
}