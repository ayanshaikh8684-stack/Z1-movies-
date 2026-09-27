package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val message: String,
    val timestampText: String,
    val type: NotificationType,
    val movieId: String? = null,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
) {
    fun toNotificationItem(): NotificationItem = NotificationItem(
        id = id,
        title = title,
        message = message,
        timestampText = timestampText,
        type = type,
        movieId = movieId,
        isRead = isRead
    )

    companion object {
        fun fromItem(item: NotificationItem): NotificationEntity = NotificationEntity(
            id = item.id,
            title = item.title,
            message = item.message,
            timestampText = item.timestampText,
            type = item.type,
            movieId = item.movieId,
            isRead = item.isRead
        )
    }
}
