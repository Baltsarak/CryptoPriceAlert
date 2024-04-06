package com.baltsarak.cryptopricealert.presentation.adapters

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.baltsarak.cryptopricealert.domain.entities.CoinInfo

object CoinInfoDiffCallback : ItemCallback<CoinInfo>() {
    override fun areItemsTheSame(oldItem: CoinInfo, newItem: CoinInfo): Boolean {
        return oldItem.fromSymbol == newItem.fromSymbol
    }

    override fun areContentsTheSame(oldItem: CoinInfo, newItem: CoinInfo): Boolean {
        return oldItem == newItem
    }
}