package com.example.data.model

data class Series(
    val id: String,
    val title: String,
    val description: String,
    val posterUrl: String,
    val backdropUrl: String,
    val trailerUrl: String = "",
    val totalSeasons: Int = 1,
    val rating: Float = 4.5f,
    val genres: List<String> = listOf("Drama"),
    val language: String = "English",
    val seasons: List<Season> = emptyList(),
    val isFeatured: Boolean = false,
    val isPublished: Boolean = true,
    val rightsStartDate: Long? = null,
    val rightsExpiryDate: Long? = null,
    val publishAt: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class Season(
    val seasonNumber: Int,
    val title: String,
    val episodeCount: Int,
    val episodes: List<Episode> = emptyList()
)

data class Episode(
    val episodeNumber: Int,
    val title: String,
    val description: String,
    val duration: String,
    val streamUrl: String,
    val subtitleUrl: String = "",
    val thumbnail: String = ""
)

data class Genre(
    val id: String,
    val name: String,
    val icon: String = "Movie",
    val movieCount: Int = 0
)

data class ContentLanguage(
    val code: String,
    val name: String,
    val nativeName: String = "",
    val isAvailable: Boolean = true
)

data class ScheduledRelease(
    val id: String,
    val movieId: String,
    val movieTitle: String,
    val scheduledPublishAt: Long,
    val status: String = "PENDING", // PENDING, PUBLISHED, CANCELLED
    val createdAt: Long = System.currentTimeMillis()
)

data class AuditLog(
    val id: String = "log_${System.currentTimeMillis()}_${(100..999).random()}",
    val adminEmail: String,
    val action: String, // CREATE, UPDATE, DELETE, PUBLISH, UNPUBLISH, RIGHTS_CHANGE, SETTINGS_CHANGE, MODERATION
    val targetType: String, // MOVIE, CATEGORY, BANNER, USER, SETTINGS, SECTION, REPORT
    val targetId: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class AppSettings(
    val appName: String = "Z1 Movies",
    val logoUrl: String = "https://images.unsplash.com/photo-1536440136628-849c177e76a1?w=200&auto=format&fit=crop&q=80",
    val supportEmail: String = "support@z1movies.stream",
    val defaultLanguage: String = "English",
    val defaultTerritory: String = "Global / Worldwide",
    val isUnderMaintenance: Boolean = false,
    val maintenanceMessage: String = "Z1 Movies is currently undergoing scheduled platform upgrades. We will be back shortly with faster streaming!",
    val currentAppVersion: String = "2.4.0",
    val minSupportedVersion: Int = 1,
    val forceUpdate: Boolean = false,
    val optionalUpdate: Boolean = false,
    val updateMessage: String = "A new cinematic release of Z1 Movies is ready! Enjoy 4K HDR playback & enhanced stability.",
    val enableGuestBrowsing: Boolean = true,
    val maxContinueWatchingItems: Int = 20,
    val newReleaseDurationDays: Int = 30,
    val defaultStreamingCdn: String = "Cloudflare Stream / AWS CloudFront"
)

data class MovieReport(
    val id: String = "rep_${System.currentTimeMillis()}",
    val movieId: String,
    val movieTitle: String,
    val userEmail: String,
    val issueType: String, // BROKEN_VIDEO, WRONG_INFO, WRONG_SUBTITLE, COPYRIGHT, OTHER
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "OPEN" // OPEN, RESOLVED, DISMISSED
)

data class ContentCollection(
    val id: String,
    val title: String,
    val description: String,
    val movieIds: List<String> = emptyList(),
    val isEnabled: Boolean = true,
    val displayOrder: Int = 0
)

data class HomePageSection(
    val id: String,
    val title: String,
    val subtitle: String = "",
    val movieIds: List<String> = emptyList(),
    val isEnabled: Boolean = true,
    val displayOrder: Int = 0
)

data class SubtitleTrack(
    val languageCode: String,
    val languageName: String,
    val trackUrl: String
)
