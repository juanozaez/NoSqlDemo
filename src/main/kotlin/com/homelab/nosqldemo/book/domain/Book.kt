package com.homelab.nosqldemo.book.domain

import java.util.UUID

data class Book(
    val id: UUID,
    val title: String,
    val author: String,
    val year: Int,
    val genre: String,
    val price: Double
)