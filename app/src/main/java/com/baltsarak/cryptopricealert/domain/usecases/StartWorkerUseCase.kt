package com.baltsarak.cryptopricealert.domain.usecases

import com.baltsarak.cryptopricealert.domain.CoinRepository
import javax.inject.Inject

class StartWorkerUseCase @Inject constructor(
    private val repository: CoinRepository
) {
    operator fun invoke() = repository.startWorker()
}