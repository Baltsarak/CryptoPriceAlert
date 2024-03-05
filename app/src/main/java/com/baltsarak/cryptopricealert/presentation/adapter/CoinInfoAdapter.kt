package com.baltsarak.cryptopricealert.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.baltsarak.cryptopricealert.databinding.ItemCoinListBinding
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.presentation.WatchListFragment
import com.squareup.picasso.Picasso
import java.util.Collections

class CoinInfoAdapter :
    ListAdapter<CoinInfo, CoinInfoViewHolder>(CoinInfoDiffCallback), WatchListFragment.ItemTouchHelperAdapter {

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

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        val currentList = currentList.toMutableList()
        Collections.swap(currentList, fromPosition, toPosition)
        submitList(currentList)
        return true
    }

    override fun onItemDismiss(position: Int) {
        val currentList = currentList.toMutableList()
        currentList.removeAt(position)
        submitList(currentList)
    }

    interface OnCoinClickListener {
        fun onCoinClick(coinPriceInfo: CoinInfo)
    }
}