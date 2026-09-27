package com.example.data.model

enum class DownloadStatus {
    DOWNLOADING,
    COMPLETED,
    PAUSED,
    FAILED
}

data class DownloadItem(
    val id: String,
    val movieId: String,
    val movieTitle: String,
    val posterUrl: String,
    val progress: Float, // 0.0 to 1.0
    val status: DownloadStatus,
    val fileSizeFormatted: String, // e.g. "1.4 GB"
    val downloadedSizeFormatted: String, // e.g. "940 MB"
    val quality: String = "1080p FHD",
    val speedFormatted: String = "4.8 MB/s",
    val isLicensed: Boolean = true
)
