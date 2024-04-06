package com.baltsarak.cryptopricealert.domain.usecases

import androidx.lifecycle.LiveData
import com.baltsarak.cryptopricealert.domain.entities.CoinInfo
import com.baltsarak.cryptopricealert.domain.CoinRepository

class GetCoinInfoUseCase(private val repository: CoinRepository) {

    suspend operator fun invoke(fromSymbol: String): LiveData<CoinInfo> = repository.getCoinInfo(fromSymbol)
}