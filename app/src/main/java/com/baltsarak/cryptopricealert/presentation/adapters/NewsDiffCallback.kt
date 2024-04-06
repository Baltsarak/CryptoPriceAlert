package com.baltsarak.cryptopricealert.presentation.adapters

import androidx.recyclerview.widget.DiffUtil.ItemCallback
import com.baltsarak.cryptopricealert.domain.entities.News

object NewsDiffCallback : ItemCallback<News>() {
    override fun areItemsTheSame(oldItem: News, newItem: News): Boolean {
        return oldItem.url == newItem.url
    }

    override fun areContentsTheSame(oldItem: News, newItem: News): Boolean {
        return oldItem == newItem
    }
}