package com.example.data.firestore

import com.example.data.model.BannerItem
import com.example.data.model.ContinueWatchingItem
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.Movie
import com.example.data.model.MovieCategory
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.google.firebase.firestore.DocumentSnapshot

object FirestoreDto {

    // 1. Movie Firestore DTO mapping
    fun movieToMap(movie: Movie): Map<String, Any?> = mapOf(
        "id" to movie.id,
        "title" to movie.title,
        "tagline" to movie.tagline,
        "description" to movie.description,
        "posterUrl" to movie.posterUrl,
        "backdropUrl" to movie.backdropUrl,
        // Large media stored in external CDN/Cloud Object Storage; only authorized stream URL stored in Firestore
        "videoUrl" to movie.videoUrl,
        "trailerUrl" to movie.trailerUrl,
        "year" to movie.year,
        "duration" to movie.duration,
        "durationMinutes" to movie.durationMinutes,
        "rating" to movie.rating.toDouble(),
        "genres" to movie.genres,
        "language" to movie.language,
        "director" to movie.director,
        "cast" to movie.cast,
        "quality" to movie.quality,
        "subtitles" to movie.subtitles,
        "audioTracks" to movie.audioTracks,
        "ageRating" to movie.ageRating,
        "isFeatured" to movie.isFeatured,
        "isTrending" to movie.isTrending,
        "isNewRelease" to movie.isNewRelease,
        "isRecentlyAdded" to movie.isRecentlyAdded,
        "isLicensedDownloadAllowed" to movie.isLicensedDownloadAllowed,
        "isPublished" to movie.isPublished,
        "colorGradientStart" to movie.colorGradientStart,
        "colorGradientEnd" to movie.colorGradientEnd,
        "accentColor" to movie.accentColor,
        "trendingRank" to movie.trendingRank,
        "contentLicenseStatus" to movie.contentLicenseStatus,
        "licenseType" to movie.licenseType,
        "digitalReleaseDate" to movie.digitalReleaseDate,
        "rightsStartDate" to movie.rightsStartDate,
        "rightsExpiryDate" to movie.rightsExpiryDate,
        "publishAt" to movie.publishAt,
        "subtitleUrl" to movie.subtitleUrl,
        "createdAt" to movie.createdAt,
        "updatedAt" to System.currentTimeMillis()
    )

    fun movieFromDoc(doc: DocumentSnapshot): Movie? {
        val id = doc.getString("id") ?: doc.id
        val title = doc.getString("title") ?: return null
        val genresRaw = doc.get("genres") as? List<*>
        val genres = genresRaw?.mapNotNull { it?.toString() } ?: listOf("Drama")
        val castRaw = doc.get("cast") as? List<*>
        val cast = castRaw?.mapNotNull { it?.toString() } ?: emptyList()
        val subtitlesRaw = doc.get("subtitles") as? List<*>
        val subtitles = subtitlesRaw?.mapNotNull { it?.toString() } ?: listOf("English")
        val audioTracksRaw = doc.get("audioTracks") as? List<*>
        val audioTracks = audioTracksRaw?.mapNotNull { it?.toString() } ?: listOf("English (Stereo)")

        return Movie(
            id = id,
            title = title,
            tagline = doc.getString("tagline") ?: "An epic cinematic journey",
            description = doc.getString("description") ?: "",
            posterUrl = doc.getString("posterUrl") ?: "",
            backdropUrl = doc.getString("backdropUrl") ?: "",
            videoUrl = doc.getString("videoUrl") ?: "",
            trailerUrl = doc.getString("trailerUrl") ?: "",
            year = (doc.getLong("year") ?: 2024).toInt(),
            duration = doc.getString("duration") ?: "2h 00m",
            durationMinutes = (doc.getLong("durationMinutes") ?: 120).toInt(),
            rating = (doc.getDouble("rating") ?: 4.5).toFloat(),
            genres = genres,
            language = doc.getString("language") ?: "English",
            director = doc.getString("director") ?: "Z1 Studios",
            cast = cast,
            quality = doc.getString("quality") ?: "1080p FHD",
            subtitles = subtitles,
            audioTracks = audioTracks,
            ageRating = doc.getString("ageRating") ?: "13+",
            isFeatured = doc.getBoolean("isFeatured") ?: false,
            isTrending = doc.getBoolean("isTrending") ?: false,
            isNewRelease = doc.getBoolean("isNewRelease") ?: false,
            isRecentlyAdded = doc.getBoolean("isRecentlyAdded") ?: false,
            isLicensedDownloadAllowed = doc.getBoolean("isLicensedDownloadAllowed") ?: true,
            isPublished = doc.getBoolean("isPublished") ?: true,
            colorGradientStart = doc.getLong("colorGradientStart") ?: 0xFF1E293BL,
            colorGradientEnd = doc.getLong("colorGradientEnd") ?: 0xFF0F172AL,
            accentColor = doc.getLong("accentColor") ?: 0xFF00E5FFL,
            trendingRank = doc.getLong("trendingRank")?.toInt(),
            contentLicenseStatus = doc.getString("contentLicenseStatus") ?: "Licensed & Public Domain Stream",
            licenseType = doc.getString("licenseType") ?: "Licensed & Public Domain Stream",
            digitalReleaseDate = doc.getString("digitalReleaseDate") ?: "",
            rightsStartDate = doc.getLong("rightsStartDate"),
            rightsExpiryDate = doc.getLong("rightsExpiryDate"),
            publishAt = doc.getLong("publishAt"),
            subtitleUrl = doc.getString("subtitleUrl") ?: "",
            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
            updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
        )
    }

    // 2. Category Firestore DTO mapping
    fun categoryToMap(category: MovieCategory, displayOrder: Int = 0): Map<String, Any?> = mapOf(
        "id" to category.id,
        "name" to category.name,
        "icon" to category.icon,
        "movieCount" to category.movieCount,
        "gradientStart" to category.gradientStart,
        "gradientEnd" to category.gradientEnd,
        "description" to category.description,
        "displayOrder" to displayOrder,
        "updatedAt" to System.currentTimeMillis()
    )

    fun categoryFromDoc(doc: DocumentSnapshot): MovieCategory? {
        val id = doc.getString("id") ?: doc.id
        val name = doc.getString("name") ?: return null
        return MovieCategory(
            id = id,
            name = name,
            icon = doc.getString("icon") ?: "LocalMovies",
            movieCount = (doc.getLong("movieCount") ?: 0).toInt(),
            gradientStart = doc.getLong("gradientStart") ?: 0xFF1E293BL,
            gradientEnd = doc.getLong("gradientEnd") ?: 0xFF0F172AL,
            description = doc.getString("description") ?: "Curated cinema collection"
        )
    }

    // 3. Banner Firestore DTO mapping
    fun bannerToMap(banner: BannerItem): Map<String, Any?> = mapOf(
        "id" to banner.id,
        "title" to banner.title,
        "subtitle" to banner.subtitle,
        "imageUrl" to banner.imageUrl,
        "movieId" to banner.movieId,
        "actionUrl" to banner.actionUrl,
        "badgeText" to banner.badgeText,
        "displayOrder" to banner.displayOrder,
        "isActive" to banner.isActive,
        "createdAt" to banner.createdAt,
        "updatedAt" to System.currentTimeMillis()
    )

    fun bannerFromDoc(doc: DocumentSnapshot): BannerItem? {
        val id = doc.getString("id") ?: doc.id
        val title = doc.getString("title") ?: return null
        return BannerItem(
            id = id,
            title = title,
            subtitle = doc.getString("subtitle") ?: "",
            imageUrl = doc.getString("imageUrl") ?: "",
            movieId = doc.getString("movieId"),
            actionUrl = doc.getString("actionUrl"),
            badgeText = doc.getString("badgeText") ?: "FEATURED",
            displayOrder = (doc.getLong("displayOrder") ?: 1).toInt(),
            isActive = doc.getBoolean("isActive") ?: true,
            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
        )
    }

    // 4. User Firestore DTO mapping
    fun userToMap(profile: UserProfile): Map<String, Any?> = mapOf(
        "id" to profile.id,
        "name" to profile.name,
        "email" to profile.email,
        "membershipTier" to profile.membershipTier,
        "avatarUrl" to profile.avatarUrl,
        "role" to profile.role.name,
        "watchlistMovieIds" to profile.watchlistMovieIds.toList(),
        "favoriteMovieIds" to profile.favoriteMovieIds.toList(),
        "wifiOnlyDownloads" to profile.wifiOnlyDownloads,
        "preferredQuality" to profile.preferredQuality,
        "autoDownloadNext" to profile.autoDownloadNext,
        "subtitlesEnabled" to profile.subtitlesEnabled,
        "subtitleLanguage" to profile.subtitleLanguage,
        "dataSaver" to profile.dataSaver,
        "darkMode" to profile.darkMode,
        "updatedAt" to System.currentTimeMillis()
    )

    fun userFromDoc(doc: DocumentSnapshot, defaultProfile: UserProfile): UserProfile {
        val roleStr = doc.getString("role") ?: defaultProfile.role.name
        val role = try { UserRole.valueOf(roleStr) } catch (e: Exception) { UserRole.USER }
        val watchlist = (doc.get("watchlistMovieIds") as? List<*>)?.mapNotNull { it?.toString() }?.toSet()
            ?: defaultProfile.watchlistMovieIds
        val favorites = (doc.get("favoriteMovieIds") as? List<*>)?.mapNotNull { it?.toString() }?.toSet()
            ?: defaultProfile.favoriteMovieIds

        return defaultProfile.copy(
            id = doc.getString("id") ?: doc.id,
            name = doc.getString("name") ?: defaultProfile.name,
            email = doc.getString("email") ?: defaultProfile.email,
            membershipTier = doc.getString("membershipTier") ?: defaultProfile.membershipTier,
            avatarUrl = doc.getString("avatarUrl") ?: defaultProfile.avatarUrl,
            role = role,
            watchlistMovieIds = watchlist,
            favoriteMovieIds = favorites,
            wifiOnlyDownloads = doc.getBoolean("wifiOnlyDownloads") ?: defaultProfile.wifiOnlyDownloads,
            preferredQuality = doc.getString("preferredQuality") ?: defaultProfile.preferredQuality,
            subtitlesEnabled = doc.getBoolean("subtitlesEnabled") ?: defaultProfile.subtitlesEnabled,
            subtitleLanguage = doc.getString("subtitleLanguage") ?: defaultProfile.subtitleLanguage,
            darkMode = doc.getBoolean("darkMode") ?: defaultProfile.darkMode
        )
    }

    // 5. Watch History Firestore DTO mapping
    fun watchHistoryToMap(userId: String, item: ContinueWatchingItem): Map<String, Any?> = mapOf(
        "id" to "${userId}_${item.movieId}",
        "userId" to userId,
        "movieId" to item.movieId,
        "title" to item.title,
        "thumbnail" to item.thumbnail,
        "positionSeconds" to item.positionSeconds,
        "durationSeconds" to item.durationSeconds,
        "genre" to item.genre,
        "lastWatchedText" to item.lastWatchedText,
        "updatedAt" to System.currentTimeMillis()
    )

    fun watchHistoryFromDoc(doc: DocumentSnapshot): ContinueWatchingItem? {
        val movieId = doc.getString("movieId") ?: return null
        val title = doc.getString("title") ?: return null
        return ContinueWatchingItem(
            movieId = movieId,
            title = title,
            thumbnail = doc.getString("thumbnail") ?: "",
            positionSeconds = (doc.getLong("positionSeconds") ?: 0).toInt(),
            durationSeconds = (doc.getLong("durationSeconds") ?: 0).toInt(),
            genre = doc.getString("genre") ?: "Cinema",
            lastWatchedText = doc.getString("lastWatchedText") ?: "Recently"
        )
    }

    // 6. Watchlist Firestore DTO mapping
    fun watchlistItemToMap(userId: String, movieId: String): Map<String, Any?> = mapOf(
        "id" to "${userId}_${movieId}",
        "userId" to userId,
        "movieId" to movieId,
        "addedAt" to System.currentTimeMillis()
    )

    // 7. Download Firestore DTO mapping
    fun downloadToMap(userId: String, item: DownloadItem): Map<String, Any?> = mapOf(
        "id" to item.id,
        "userId" to userId,
        "movieId" to item.movieId,
        "movieTitle" to item.movieTitle,
        "posterUrl" to item.posterUrl,
        "progress" to item.progress.toDouble(),
        "status" to item.status.name,
        "fileSizeFormatted" to item.fileSizeFormatted,
        "downloadedSizeFormatted" to item.downloadedSizeFormatted,
        "quality" to item.quality,
        "createdAt" to System.currentTimeMillis()
    )

    fun downloadFromDoc(doc: DocumentSnapshot): DownloadItem? {
        val id = doc.getString("id") ?: doc.id
        val movieId = doc.getString("movieId") ?: return null
        val movieTitle = doc.getString("movieTitle") ?: return null
        val statusStr = doc.getString("status") ?: DownloadStatus.COMPLETED.name
        val status = try { DownloadStatus.valueOf(statusStr) } catch (e: Exception) { DownloadStatus.COMPLETED }

        return DownloadItem(
            id = id,
            movieId = movieId,
            movieTitle = movieTitle,
            posterUrl = doc.getString("posterUrl") ?: "",
            progress = (doc.getDouble("progress") ?: 1.0).toFloat(),
            status = status,
            fileSizeFormatted = doc.getString("fileSizeFormatted") ?: "1.5 GB",
            downloadedSizeFormatted = doc.getString("downloadedSizeFormatted") ?: "1.5 GB",
            quality = doc.getString("quality") ?: "1080p FHD"
        )
    }

    // 8. Notification Firestore DTO mapping
    fun notificationToMap(item: NotificationItem, targetUserId: String? = null): Map<String, Any?> = mapOf(
        "id" to item.id,
        "userId" to targetUserId,
        "title" to item.title,
        "message" to item.message,
        "timestampText" to item.timestampText,
        "type" to item.type.name,
        "movieId" to item.movieId,
        "isRead" to item.isRead,
        "createdAt" to System.currentTimeMillis()
    )

    fun notificationFromDoc(doc: DocumentSnapshot): NotificationItem? {
        val id = doc.getString("id") ?: doc.id
        val title = doc.getString("title") ?: return null
        val typeStr = doc.getString("type") ?: NotificationType.NEW_RELEASE.name
        val type = try { NotificationType.valueOf(typeStr) } catch (e: Exception) { NotificationType.NEW_RELEASE }

        return NotificationItem(
            id = id,
            title = title,
            message = doc.getString("message") ?: "",
            timestampText = doc.getString("timestampText") ?: "Recently",
            type = type,
            movieId = doc.getString("movieId"),
            isRead = doc.getBoolean("isRead") ?: false
        )
    }
}
