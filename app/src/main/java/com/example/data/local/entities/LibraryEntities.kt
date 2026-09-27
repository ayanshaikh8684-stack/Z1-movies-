package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ContinueWatchingItem

@Entity(tableName = "watchlist", primaryKeys = ["userId", "movieId"])
data class WatchlistEntity(
    val userId: String,
    val movieId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "favorites", primaryKeys = ["userId", "movieId"])
data class FavoriteEntity(
    val userId: String,
    val movieId: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "continue_watching")
data class ContinueWatchingEntity(
    @PrimaryKey
    val id: String, // userId_movieId
    val userId: String,
    val movieId: String,
    val title: String,
    val thumbnail: String,
    val positionSeconds: Int,
    val durationSeconds: Int,
    val genre: String,
    val lastWatchedText: String = "Recently",
    val lastWatchedTimestamp: Long = System.currentTimeMillis()
) {
    fun toContinueWatchingItem(): ContinueWatchingItem = ContinueWatchingItem(
        movieId = movieId,
        title = title,
        thumbnail = thumbnail,
        positionSeconds = positionSeconds,
        durationSeconds = durationSeconds,
        genre = genre,
        lastWatchedText = lastWatchedText
    )

    companion object {
        fun fromItem(userId: String, item: ContinueWatchingItem): ContinueWatchingEntity = ContinueWatchingEntity(
            id = "${userId}_${item.movieId}",
            userId = userId,
            movieId = item.movieId,
            title = item.title,
            thumbnail = item.thumbnail,
            positionSeconds = item.positionSeconds,
            durationSeconds = item.durationSeconds,
            genre = item.genre,
            lastWatchedText = item.lastWatchedText
        )
    }
}

