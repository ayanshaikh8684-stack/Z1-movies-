package com.example.data.model

data class EpgProgram(
    val id: String,
    val title: String,
    val description: String = "",
    val startTime: String, // e.g., "19:00"
    val endTime: String,   // e.g., "20:00"
    val isLiveNow: Boolean = false,
    val category: String = "General"
)

data class LiveChannel(
    val id: String,
    val name: String,
    val logoUrl: String,
    val description: String = "",
    val category: String, // News, Sports, Movies, Entertainment, Music, Kids, Regional, International, etc.
    val language: String = "English",
    val country: String = "Global",
    val streamUrl: String,
    val epgSourceUrl: String = "",
    val currentProgram: String = "Live Broadcast",
    val nextProgram: String = "Upcoming Show",
    val currentProgramTime: String = "Live Now",
    val isFeatured: Boolean = false,
    val isPublished: Boolean = true,
    val isEnabled: Boolean = true,
    val isPremiumOnly: Boolean = false,
    val displayOrder: Int = 0,
    val rightsProvider: String = "Authorized Broadcaster",
    val territory: String = "Worldwide / Open Broadcast",
    val rightsStartDate: Long? = null,
    val rightsExpiryDate: Long? = null,
    val isStreamingPermitted: Boolean = true,
    val isAdSupportedPermitted: Boolean = true,
    val isPremiumAccessPermitted: Boolean = true,
    val schedule: List<EpgProgram> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    val isRightsExpired: Boolean
        get() = rightsExpiryDate != null && rightsExpiryDate < System.currentTimeMillis()

    val isRightsNotYetStarted: Boolean
        get() = rightsStartDate != null && rightsStartDate > System.currentTimeMillis()

    // Determine if channel is playable for user
    fun isPlayableFor(isUserPremium: Boolean): Boolean {
        if (!isPublished || !isEnabled || !isStreamingPermitted) return false
        if (isRightsExpired || isRightsNotYetStarted) return false
        if (isPremiumOnly) {
            return isUserPremium
        }
        return true
    }
}

data class LiveChannelReport(
    val id: String,
    val channelId: String,
    val channelName: String,
    val issueType: String,
    val description: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending" // Pending, Investigating, Resolved
)
