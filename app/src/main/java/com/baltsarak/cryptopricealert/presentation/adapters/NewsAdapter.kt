package com.baltsarak.cryptopricealert.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.baltsarak.cryptopricealert.databinding.ItemNewsBinding
import com.baltsarak.cryptopricealert.domain.entities.News
import com.bumptech.glide.Glide

class NewsAdapter :
    ListAdapter<News, NewsViewHolder>(NewsDiffCallback) {

    var onNewsClickListener: OnNewsClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = ItemNewsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = getItem(position)
        with(holder.binding) {
            val titleText = news.title
            if (titleText.length > 80) title.textSize = 24F
            if (titleText.length < 55) title.textSize = 28F
            else title.textSize = 26F
            title.text = titleText
            body.text = news.body
            Glide.with(holder.itemView.context)
                .load(news.imageUrl)
                .into(image)

            root.setOnClickListener {
                onNewsClickListener?.onNewsClick(news)
            }
        }
    }

    interface OnNewsClickListener {
        fun onNewsClick(news: News)
    }
}