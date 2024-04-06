package com.baltsarak.cryptopricealert.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.baltsarak.cryptopricealert.databinding.ItemCoinSearchListBinding
import com.baltsarak.cryptopricealert.domain.entities.CoinName

class CoinNameAdapter :
    ListAdapter<CoinName, CoinNameViewHolder>(CoinNameDiffCallback) {

    var onCoinClickListener: OnCoinClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinNameViewHolder {
        val binding = ItemCoinSearchListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CoinNameViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinNameViewHolder, position: Int) {
        val coin = getItem(position)
        with(holder.binding) {
            with(coin) {
                currencyPair.text = coin.fullName
                root.setOnClickListener {
                    onCoinClickListener?.onCoinClick(this)
                }
            }
        }
    }

    interface OnCoinClickListener {
        fun onCoinClick(name: CoinName)
    }
}