package com.example.data.model

data class BannerItem(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val imageUrl: String,
    val movieId: String? = null,
    val actionUrl: String? = null,
    val badgeText: String = "FEATURED",
    val displayOrder: Int = 1,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
