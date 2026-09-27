package com.example.data.remote

import kotlinx.coroutines.delay
import java.util.UUID

enum class CloudStorageProvider(val title: String, val defaultEndpoint: String) {
    CLOUDFLARE_R2("Cloudflare R2 CDN", "https://pub-z1movies.r2.dev"),
    AWS_S3("Amazon AWS S3 / CloudFront", "https://s3.amazonaws.com/z1movies-cdn"),
    BUNNY_STREAM("BunnyCDN Video Stream", "https://video.bunnycdn.com/play"),
    GOOGLE_CLOUD("Google Cloud Storage", "https://storage.googleapis.com/z1movies-media"),
    SUPABASE("Supabase Storage", "https://z1movies.supabase.co/storage/v1/object/public");
}

class CloudStorageService {

    /**
     * Simulates uploading a local image (poster or backdrop) to cloud storage and returns
     * a secure, public HTTPS CDN URL suitable for streaming across all client devices.
     */
    suspend fun uploadImage(
        localUriOrPath: String,
        imageType: String = "poster",
        provider: CloudStorageProvider = CloudStorageProvider.CLOUDFLARE_R2
    ): Result<String> {
        delay(600) // Realistic cloud upload latency simulation

        if (localUriOrPath.startsWith("http://") || localUriOrPath.startsWith("https://")) {
            return Result.success(localUriOrPath)
        }

        val filename = "${imageType}_${UUID.randomUUID().toString().take(8)}.webp"
        val cdnUrl = "${provider.defaultEndpoint}/posters/$filename"
        return Result.success(cdnUrl)
    }

    /**
     * Simulates uploading a trailer video clip or movie asset to cloud storage.
     */
    suspend fun uploadVideo(
        localUriOrPath: String,
        provider: CloudStorageProvider = CloudStorageProvider.BUNNY_STREAM
    ): Result<String> {
        delay(900)
        if (localUriOrPath.startsWith("http://") || localUriOrPath.startsWith("https://")) {
            return Result.success(localUriOrPath)
        }
        val filename = "video_${UUID.randomUUID().toString().take(8)}.mp4"
        val cdnUrl = "${provider.defaultEndpoint}/streams/$filename"
        return Result.success(cdnUrl)
    }

    /**
     * Validates whether a given video URL is suitable for streaming (HLS, DASH, MP4).
     */
    fun validateStreamUrl(url: String): Boolean {
        val trimmed = url.trim()
        if (!trimmed.startsWith("https://") && !trimmed.startsWith("http://")) return false
        return trimmed.endsWith(".mp4", ignoreCase = true) ||
                trimmed.endsWith(".m3u8", ignoreCase = true) ||
                trimmed.endsWith(".mpd", ignoreCase = true) ||
                trimmed.contains("commondatastorage.googleapis.com") ||
                trimmed.contains("sample") ||
                trimmed.contains("stream")
    }
}
