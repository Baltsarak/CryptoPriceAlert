package com.baltsarak.cryptopricealert.domain.entities

data class News(
    val publishedOn: String,
    val imageUrl: String,
    val title: String,
    val url: String,
    val body: String
)