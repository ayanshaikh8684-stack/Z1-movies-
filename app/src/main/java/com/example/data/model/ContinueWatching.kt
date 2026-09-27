package com.example.data.model

data class ContinueWatchingItem(
    val movieId: String,
    val title: String,
    val thumbnail: String,
    val positionSeconds: Int,
    val durationSeconds: Int,
    val genre: String,
    val lastWatchedText: String = "Yesterday"
) {
    val progress: Float
        get() = if (durationSeconds > 0) positionSeconds.toFloat() / durationSeconds else 0f

    val remainingMinutes: Int
        get() = ((durationSeconds - positionSeconds).coerceAtLeast(0)) / 60
}
