package com.example.data.model

data class Movie(
    val id: String,
    val title: String,
    val description: String,
    val posterUrl: String,
    val backdropUrl: String,
    val videoUrl: String,
    val trailerUrl: String = "",
    val year: Int,
    val duration: String,
    val durationMinutes: Int = 115,
    val rating: Float,
    val genres: List<String>,
    val language: String,
    val director: String,
    val cast: List<String>,
    val quality: String = "4K Ultra HD",
    val subtitles: List<String> = listOf("English", "Hindi", "Urdu", "Spanish", "French"),
    val audioTracks: List<String> = listOf("English (Dolby Atmos)", "Hindi (Original 5.1)", "Urdu (Stereo)"),
    val ageRating: String = "16+",
    val isFeatured: Boolean = false,
    val isTrending: Boolean = false,
    val isNewRelease: Boolean = false,
    val isRecentlyAdded: Boolean = false,
    val isLicensedDownloadAllowed: Boolean = true,
    val colorGradientStart: Long = 0xFF1E293B,
    val colorGradientEnd: Long = 0xFF0F172A,
    val accentColor: Long = 0xFF00E5FF,
    val trendingRank: Int? = null,
    val tagline: String = "An epic cinematic journey",
    val contentLicenseStatus: String = "Licensed & Public Domain Stream",
    val licenseType: String = "Licensed & Public Domain Stream",
    val isPublished: Boolean = true,
    val digitalReleaseDate: String = "",
    val rightsStartDate: Long? = null,
    val rightsExpiryDate: Long? = null,
    val publishAt: Long? = null,
    val subtitleUrl: String = "",
    val rightsProvider: String = "Authorized Studio Partner",
    val territory: String = "Global / Worldwide",
    val isStreamingPermitted: Boolean = true,
    val isDownloadPermitted: Boolean = true,
    val isAdSupportedPermitted: Boolean = true,
    val hideWhenExpired: Boolean = false,
    val isPremiumEarlyAccess: Boolean = false,
    val premiumReleaseDate: Long? = null,
    val freeReleaseDate: Long? = null,
    val autoFreeAfterDate: Boolean = true,
    val premiumOnlyDurationDays: Int = 14,
    val subtitleTracks: List<SubtitleTrack> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isRightsNotYetStarted: Boolean
        get() = rightsStartDate != null && rightsStartDate > System.currentTimeMillis()

    val isRightsExpired: Boolean
        get() = rightsExpiryDate != null && rightsExpiryDate < System.currentTimeMillis()

    val isScheduledRelease: Boolean
        get() = publishAt != null && publishAt > System.currentTimeMillis()

    // Premium Early Access window state
    // Movie is in Premium Early Access if feature is ON, rights permit streaming,
    // current time >= premiumReleaseDate (or unset), and either freeReleaseDate is not yet reached or not auto-freed
    val isInPremiumEarlyAccess: Boolean
        get() {
            if (!isPremiumEarlyAccess) return false
            if (isRightsExpired || isRightsNotYetStarted) return false
            val now = System.currentTimeMillis()
            val premStart = premiumReleaseDate ?: 0L
            if (now < premStart) return false // Not yet even in premium release
            val freeDate = freeReleaseDate
            return if (freeDate != null && autoFreeAfterDate) {
                now < freeDate
            } else {
                true
            }
        }

    val isFreeReleaseReached: Boolean
        get() {
            val freeDate = freeReleaseDate ?: return !isPremiumEarlyAccess
            return System.currentTimeMillis() >= freeDate
        }

    // Playable check considering user's premium status
    fun isPlayableFor(isUserPremium: Boolean): Boolean {
        if (!isPublished) return false
        if (isRightsExpired || isRightsNotYetStarted || isScheduledRelease) return false
        if (isInPremiumEarlyAccess) {
            return isUserPremium
        }
        return true
    }

    val isPlayableForUser: Boolean
        get() = isPublished && !isRightsExpired && !isRightsNotYetStarted && !isScheduledRelease
}


