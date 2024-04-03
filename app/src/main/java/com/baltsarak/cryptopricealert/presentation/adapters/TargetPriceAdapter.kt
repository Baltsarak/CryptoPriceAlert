package com.baltsarak.cryptopricealert.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.ItemTargetPriceBinding
import com.baltsarak.cryptopricealert.domain.TargetPrice
import com.baltsarak.cryptopricealert.presentation.fragments.CoinDetailInfoFragment

class TargetPriceAdapter :
    ListAdapter<TargetPrice, TargetPriceViewHolder>(TargetPriceDiffCallback),
    CoinDetailInfoFragment.ItemTouchHelperAdapter {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TargetPriceViewHolder {
        val binding = ItemTargetPriceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TargetPriceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TargetPriceViewHolder, position: Int) {
        val coin = getItem(position)
        with(holder.binding) {
            val context = holder.itemView.context
            price.text =
                context.getString(R.string.price_with_currency, coin.targetPrice.toString())
            val colorResId =
                if (coin.higherThenCurrent) R.color.colorPriceHigher else R.color.colorPriceLower
            price.setTextColor(ContextCompat.getColor(context, colorResId))
        }
    }

    override fun onItemDismiss(position: Int) {
        val newList = currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList)
    }
}