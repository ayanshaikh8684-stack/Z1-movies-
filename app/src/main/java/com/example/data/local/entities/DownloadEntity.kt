package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey
    val id: String,
    val movieId: String,
    val movieTitle: String,
    val posterUrl: String,
    val progress: Float,
    val status: DownloadStatus,
    val fileSizeFormatted: String,
    val downloadedSizeFormatted: String,
    val quality: String = "1080p FHD",
    val speedFormatted: String = "4.8 MB/s",
    val isLicensed: Boolean = true,
    val downloadedAt: Long = System.currentTimeMillis()
) {
    fun toDownloadItem(): DownloadItem = DownloadItem(
        id = id,
        movieId = movieId,
        movieTitle = movieTitle,
        posterUrl = posterUrl,
        progress = progress,
        status = status,
        fileSizeFormatted = fileSizeFormatted,
        downloadedSizeFormatted = downloadedSizeFormatted,
        quality = quality,
        speedFormatted = speedFormatted,
        isLicensed = isLicensed
    )

    companion object {
        fun fromItem(item: DownloadItem): DownloadEntity = DownloadEntity(
            id = item.id,
            movieId = item.movieId,
            movieTitle = item.movieTitle,
            posterUrl = item.posterUrl,
            progress = item.progress,
            status = item.status,
            fileSizeFormatted = item.fileSizeFormatted,
            downloadedSizeFormatted = item.downloadedSizeFormatted,
            quality = item.quality,
            speedFormatted = item.speedFormatted,
            isLicensed = item.isLicensed
        )
    }
}
