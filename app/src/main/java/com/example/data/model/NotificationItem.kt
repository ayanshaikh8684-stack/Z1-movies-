package com.example.data.model

enum class NotificationType {
    NEW_RELEASE,
    RECOMMENDED,
    DOWNLOAD_COMPLETED,
    ACCOUNT,
    ANNOUNCEMENT
}

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestampText: String,
    val type: NotificationType,
    val movieId: String? = null,
    val isRead: Boolean = false,
    val timestamp: String = timestampText,
    val relatedMovieId: String? = movieId
)

