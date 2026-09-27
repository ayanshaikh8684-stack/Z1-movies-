package com.example.data.model

enum class UserRole(val displayName: String, val level: Int) {
    USER("Standard Viewer", 1),
    EDITOR("Content Editor", 2),
    ADMIN("Administrator", 3),
    SUPER_ADMIN("Super Administrator", 4);

    fun canAddMovie(): Boolean = this != USER
    fun canEditMovie(): Boolean = this != USER
    fun canPublishMovie(): Boolean = this == ADMIN || this == SUPER_ADMIN
    fun canDeleteMovie(): Boolean = this == ADMIN || this == SUPER_ADMIN
    fun canManageUsers(): Boolean = this == SUPER_ADMIN
}

data class UserProfile(
    val id: String = "user_z1_001",
    val name: String = "Ayan Shaikh",
    val email: String = "ayan@z1movies.stream",
    val membershipTier: String = "Z1 Cinema Pro",
    val isPremium: Boolean = true,
    val premiumExpiryDate: Long? = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000L), // 30 days active by default
    val avatarUrl: String = "",
    val role: UserRole = UserRole.USER,
    val watchlistMovieIds: Set<String> = emptySet(),
    val favoriteMovieIds: Set<String> = emptySet(),
    val favoriteChannelIds: Set<String> = emptySet(),
    val wifiOnlyDownloads: Boolean = true,
    val preferredQuality: String = "1080p FHD",
    val autoDownloadNext: Boolean = false,
    val subtitlesEnabled: Boolean = true,
    val subtitleLanguage: String = "English",
    val dataSaver: Boolean = false,
    val darkMode: Boolean = true
) {
    // Dynamically check if Premium access is active (both isPremium flag AND unexpired)
    val isPremiumActive: Boolean
        get() = isPremium && (premiumExpiryDate == null || premiumExpiryDate > System.currentTimeMillis())
}

