package com.baltsarak.cryptopricealert.presentation.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import com.baltsarak.cryptopricealert.R
import com.baltsarak.cryptopricealert.databinding.ItemCoinListBinding
import com.baltsarak.cryptopricealert.domain.CoinInfo
import com.baltsarak.cryptopricealert.domain.TargetPrice
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
            currencyName.text = coin.fullName
            "$${coin.price.toString()}".also { coinPrice.text = it }
            toSymbol.setText(R.string.to_symbol)

            val targetPrices = coin.targetPrice
                .filterNotNull()
                .filter { it.targetPrice != 0.0 }

            setTargetPriceValues(targetPrices, holder)

            Glide.with(holder.itemView.context)
                .load(coin.imageUrl)
                .into(coinLogo)

            root.setOnClickListener {
                onCoinClickListener?.onCoinClick(coin)
            }
        }
    }

    private fun ItemCoinListBinding.setTargetPriceValues(
        targetPrices: List<TargetPrice>,
        holder: CoinInfoViewHolder
    ) {
        when (targetPrices.size) {
            0 -> {
                bellLogo.visibility = View.GONE
                targetPrice1.visibility = View.GONE
                targetPrice2.visibility = View.GONE
                targetPriceMore.visibility = View.GONE
            }

            1 -> {
                targetPrice1.text = targetPrices[0].toPriceString()
                targetPrice1.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        if (targetPrices[0].higherThenCurrent)
                            R.color.colorPriceHigher
                        else R.color.colorPriceLower
                    )
                )
                targetPrice1.visibility = View.VISIBLE
                bellLogo.setImageResource(R.drawable.bell)
                bellLogo.visibility = View.VISIBLE

                targetPrice2.visibility = View.GONE
                targetPriceMore.visibility = View.GONE
            }

            2 -> {
                if (targetPrices[1].higherThenCurrent && !targetPrices[0].higherThenCurrent) {
                    setTargetPriceData(targetPrice1, targetPrices[1])
                    setTargetPriceData(targetPrice2, targetPrices[0])
                } else {
                    setTargetPriceData(targetPrice1, targetPrices[0])
                    setTargetPriceData(targetPrice2, targetPrices[1])
                }
                bellLogo.setImageResource(R.drawable.bell)
                bellLogo.visibility = View.VISIBLE

                targetPriceMore.visibility = View.GONE
            }

            else -> {
                if (targetPrices[1].higherThenCurrent && !targetPrices[0].higherThenCurrent) {
                    setTargetPriceData(targetPrice1, targetPrices[1])
                    setTargetPriceData(targetPrice2, targetPrices[0])
                } else {
                    setTargetPriceData(targetPrice1, targetPrices[0])
                    setTargetPriceData(targetPrice2, targetPrices[1])
                }
                bellLogo.setImageResource(R.drawable.bell)
                bellLogo.visibility = View.VISIBLE
                targetPriceMore.text = ". . ."
                targetPriceMore.visibility = View.VISIBLE
            }
        }
    }

    private fun setTargetPriceData(textView: TextView, targetPrice: TargetPrice) {
        textView.apply {
            text = targetPrice.toPriceString()
            setTextColor(ContextCompat.getColor(context,
                if (targetPrice.higherThenCurrent) R.color.colorPriceHigher else R.color.colorPriceLower))
            visibility = View.VISIBLE
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