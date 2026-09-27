package com.example.data.model

data class MovieCategory(
    val id: String,
    val name: String,
    val icon: String,
    val movieCount: Int,
    val gradientStart: Long,
    val gradientEnd: Long,
    val description: String = "Explore curated films"
)
