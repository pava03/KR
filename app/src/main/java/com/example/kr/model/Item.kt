package com.example.kr.model

data class Item(
    val id: Int = 0,
    var name: String,
    var description: String,
    var storeName: String,
    var originalPrice: Double,
    var discountedPrice: Double,
    var validUntil: String
)
