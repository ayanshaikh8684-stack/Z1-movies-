package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.UserProfile
import com.example.data.model.UserRole

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole = UserRole.USER,
    val membershipTier: String = "Z1 Cinema Pro",
    val avatarUrl: String = "",
    val wifiOnlyDownloads: Boolean = true,
    val preferredQuality: String = "1080p FHD",
    val autoDownloadNext: Boolean = false,
    val subtitlesEnabled: Boolean = true,
    val subtitleLanguage: String = "English",
    val dataSaver: Boolean = false,
    val darkMode: Boolean = true
) {
    fun toUserProfile(watchlist: Set<String>, favorites: Set<String>): UserProfile = UserProfile(
        id = id,
        name = name,
        email = email,
        membershipTier = membershipTier,
        avatarUrl = avatarUrl,
        role = role,
        watchlistMovieIds = watchlist,
        favoriteMovieIds = favorites,
        wifiOnlyDownloads = wifiOnlyDownloads,
        preferredQuality = preferredQuality,
        autoDownloadNext = autoDownloadNext,
        subtitlesEnabled = subtitlesEnabled,
        subtitleLanguage = subtitleLanguage,
        dataSaver = dataSaver,
        darkMode = darkMode
    )

    companion object {
        fun fromUserProfile(profile: UserProfile): UserEntity = UserEntity(
            id = profile.id,
            name = profile.name,
            email = profile.email,
            role = profile.role,
            membershipTier = profile.membershipTier,
            avatarUrl = profile.avatarUrl,
            wifiOnlyDownloads = profile.wifiOnlyDownloads,
            preferredQuality = profile.preferredQuality,
            autoDownloadNext = profile.autoDownloadNext,
            subtitlesEnabled = profile.subtitlesEnabled,
            subtitleLanguage = profile.subtitleLanguage,
            dataSaver = profile.dataSaver,
            darkMode = profile.darkMode
        )
    }
}
