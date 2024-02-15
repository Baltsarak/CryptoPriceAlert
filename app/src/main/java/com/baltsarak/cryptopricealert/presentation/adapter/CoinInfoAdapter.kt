package com.baltsarak.cryptopricealert.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.baltsarak.cryptopricealert.databinding.ItemCoinListBinding
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.squareup.picasso.Picasso

class CoinInfoAdapter :
    ListAdapter<CoinInfo, CoinInfoViewHolder>(CoinInfoDiffCallback) {

    var onCoinClickListener: OnCoinClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinInfoViewHolder {
        val binding = ItemCoinListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CoinInfoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CoinInfoViewHolder, position: Int) {
        val coin = getItem(position)
        with(holder.binding) {
            with(coin) {
                val pairTemplate = "%s / %s"
                currencyPair.text = String.format(pairTemplate, fromSymbol, toSymbol)
                coinPrice.text = price.toString()
                Picasso.get().load(imageUrl).into(coinLogo)
                root.setOnClickListener {
                    onCoinClickListener?.onCoinClick(this)
                }
            }
        }
    }

    interface OnCoinClickListener {
        fun onCoinClick(coinPriceInfo: CoinInfo)
    }
}