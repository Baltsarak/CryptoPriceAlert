package com.baltsarak.cryptopricealert.presentation.adapters

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.baltsarak.cryptopricealert.domain.TargetPrice

object TargetPriceDiffCallback : ItemCallback<TargetPrice>() {
    override fun areItemsTheSame(oldItem: TargetPrice, newItem: TargetPrice): Boolean {
        return oldItem.fromSymbol == newItem.fromSymbol
    }

    override fun areContentsTheSame(oldItem: TargetPrice, newItem: TargetPrice): Boolean {
        return oldItem == newItem
    }
}