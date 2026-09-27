package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.BannerItem

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val movieId: String?,
    val actionUrl: String?,
    val badgeText: String,
    val displayOrder: Int,
    val isActive: Boolean,
    val createdAt: Long
) {
    fun toBannerItem(): BannerItem = BannerItem(
        id = id,
        title = title,
        subtitle = subtitle,
        imageUrl = imageUrl,
        movieId = movieId,
        actionUrl = actionUrl,
        badgeText = badgeText,
        displayOrder = displayOrder,
        isActive = isActive,
        createdAt = createdAt
    )

    companion object {
        fun fromBannerItem(item: BannerItem): BannerEntity = BannerEntity(
            id = item.id,
            title = item.title,
            subtitle = item.subtitle,
            imageUrl = item.imageUrl,
            movieId = item.movieId,
            actionUrl = item.actionUrl,
            badgeText = item.badgeText,
            displayOrder = item.displayOrder,
            isActive = item.isActive,
            createdAt = item.createdAt
        )
    }
}
