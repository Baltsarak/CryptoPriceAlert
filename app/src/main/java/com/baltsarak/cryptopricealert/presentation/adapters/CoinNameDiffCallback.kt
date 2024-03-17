package com.baltsarak.cryptopricealert.presentation.adapters

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.baltsarak.cryptopricealert.domain.CoinName

object CoinNameDiffCallback : ItemCallback<CoinName>() {
    override fun areItemsTheSame(oldItem: CoinName, newItem: CoinName): Boolean {
        return oldItem.fullName == newItem.fullName
    }

    override fun areContentsTheSame(oldItem: CoinName, newItem: CoinName): Boolean {
        return oldItem == newItem
    }
}