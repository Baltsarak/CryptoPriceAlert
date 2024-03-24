package com.baltsarak.cryptopricealert.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.ItemCoinListBinding
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.domain.toPriceString
import com.baltsarak.cryptopricealert.presentation.fragments.WatchListFragment
import com.bumptech.glide.Glide
import java.util.Collections

class CoinInfoAdapter :
    ListAdapter<CoinInfo, CoinInfoViewHolder>(CoinInfoDiffCallback),
    WatchListFragment.ItemTouchHelperAdapter {

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
            currencySymbol.text = coin.fromSymbol
            coinPrice.text = coin.price.toString()
            toSymbol.setText(R.string.to_symbol)

            val targetPriceString = coin.targetPrice.toPriceString()
            if (targetPriceString.isNotEmpty()) {
                targetPrices.text = targetPriceString
                targetPrices.isVisible = true
                bellLogo.isVisible = true
                bellLogo.setImageResource(R.drawable.bell)
            } else {
                targetPrices.isVisible = false
                bellLogo.isVisible = false
            }

            Glide.with(holder.itemView.context)
                .load(coin.imageUrl)
                .into(coinLogo)

            root.setOnClickListener {
                onCoinClickListener?.onCoinClick(coin)
            }
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): List<CoinInfo> {
        val newList = currentList.toMutableList()
        Collections.swap(newList, fromPosition, toPosition)
        submitList(newList)
        return newList
    }

    override fun onItemDismiss(position: Int) {
        val newList = currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList)
    }

    interface OnCoinClickListener {
        fun onCoinClick(coinPriceInfo: CoinInfo)
    }
}