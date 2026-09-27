package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.firestore.FirestoreSyncStatus
import com.example.data.model.BannerItem
import com.example.data.model.ContinueWatchingItem
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.data.model.Movie
import com.example.data.model.MovieCategory
import com.example.data.model.NotificationItem
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.data.remote.AdminSession
import com.example.data.remote.AuthService
import com.example.data.repository.MovieRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class MainTab {
    HOME,
    MOVIES,
    LIVE_TV,
    CATEGORIES,
    DOWNLOADS,
    PROFILE
}

class Z1MoviesViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: MovieRepository = MovieRepository.getInstance(application)
) : AndroidViewModel(application) {

    constructor(repository: MovieRepository) : this(
        Application(),
        repository
    )

    val authService: AuthService = AuthService()

    // Splash screen visible state
    private val _isSplashVisible = MutableStateFlow(true)
    val isSplashVisible: StateFlow<Boolean> = _isSplashVisible.asStateFlow()

    // Primary Bottom Navigation Tab
    private val _selectedTab = MutableStateFlow(MainTab.HOME)
    val selectedTab: StateFlow<MainTab> = _selectedTab.asStateFlow()

    // Dark/Light Theme State
    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode(enabled: Boolean = !_isDarkMode.value) {
        _isDarkMode.value = enabled
        showMessage(if (enabled) "Switched to Dark Mode" else "Switched to Light Mode")
    }

    // Overlays & Secondary Screens
    private val _activeMovieDetail = MutableStateFlow<Movie?>(null)
    val activeMovieDetail: StateFlow<Movie?> = _activeMovieDetail.asStateFlow()

    private val _activePlayerMovie = MutableStateFlow<Movie?>(null)
    val activePlayerMovie: StateFlow<Movie?> = _activePlayerMovie.asStateFlow()

    private val _isSearchOpen = MutableStateFlow(false)
    val isSearchOpen: StateFlow<Boolean> = _isSearchOpen.asStateFlow()

    private val _isNotificationsOpen = MutableStateFlow(false)
    val isNotificationsOpen: StateFlow<Boolean> = _isNotificationsOpen.asStateFlow()

    private val _isMyListOpen = MutableStateFlow(false)
    val isMyListOpen: StateFlow<Boolean> = _isMyListOpen.asStateFlow()

    private val _selectedCategory = MutableStateFlow<MovieCategory?>(null)
    val selectedCategory: StateFlow<MovieCategory?> = _selectedCategory.asStateFlow()

    private val _isAdminOpen = MutableStateFlow(false)
    val isAdminOpen: StateFlow<Boolean> = _isAdminOpen.asStateFlow()

    private val _isAuthOpen = MutableStateFlow(false)
    val isAuthOpen: StateFlow<Boolean> = _isAuthOpen.asStateFlow()

    // Feedback message (e.g., added to watchlist, download started)
    private val _feedbackMessage = MutableStateFlow<String?>(null)
    val feedbackMessage: StateFlow<String?> = _feedbackMessage.asStateFlow()

    // Repository Flows
    val allMovies: StateFlow<List<Movie>> = repository.movies
    val allAdminMovies: StateFlow<List<Movie>> = repository.allAdminMovies
    val banners: StateFlow<List<BannerItem>> = repository.banners
    val allAdminBanners: StateFlow<List<BannerItem>> = repository.allAdminBanners
    val continueWatching: StateFlow<List<ContinueWatchingItem>> = repository.continueWatching
    val downloads: StateFlow<List<DownloadItem>> = repository.downloads
    val categories: List<MovieCategory> get() = repository.categories.value
    val categoriesFlow: StateFlow<List<MovieCategory>> = repository.categories
    val notifications: StateFlow<List<NotificationItem>> = repository.notifications
    val userProfile: StateFlow<UserProfile> = repository.userProfile
    val adminSession: StateFlow<AdminSession> = authService.currentSession
    val firestoreSyncStatus: StateFlow<FirestoreSyncStatus> = repository.firestoreSyncStatus

    // Live Channels Flow
    val allLiveChannels: StateFlow<List<com.example.data.model.LiveChannel>> = repository.liveChannels
    val liveChannelReports: StateFlow<List<com.example.data.model.LiveChannelReport>> = repository.liveChannelReports

    // Selected Live Channel for Detail Sheet
    private val _selectedLiveChannel = MutableStateFlow<com.example.data.model.LiveChannel?>(null)
    val selectedLiveChannel: StateFlow<com.example.data.model.LiveChannel?> = _selectedLiveChannel.asStateFlow()

    // Active Live Channel Player
    private val _activePlayerChannel = MutableStateFlow<com.example.data.model.LiveChannel?>(null)
    val activePlayerChannel: StateFlow<com.example.data.model.LiveChannel?> = _activePlayerChannel.asStateFlow()

    // Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // Live Channel Search Results
    val searchChannelResults: StateFlow<List<com.example.data.model.LiveChannel>> = combine(allLiveChannels, _searchQuery) { channels, q ->
        val query = q.trim()
        if (query.isEmpty()) {
            emptyList()
        } else {
            channels.filter { ch ->
                ch.isPublished && (
                    ch.name.contains(query, ignoreCase = true) ||
                    ch.category.contains(query, ignoreCase = true) ||
                    ch.language.contains(query, ignoreCase = true) ||
                    ch.country.contains(query, ignoreCase = true) ||
                    ch.currentProgram.contains(query, ignoreCase = true) ||
                    ch.description.contains(query, ignoreCase = true)
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentSearches = MutableStateFlow(
        listOf("Sci-Fi 4K", "Midnight Protocol", "Action Thrillers", "Elena Vance", "Al-Andalus")
    )

    val popularSearches = listOf(
        "Midnight Protocol", "Desert Storm", "The Last Horizon", "4K Ultra HD", "Lost Kingdom", "Indian Drama"
    )

    // Filter & Sort State for Movies Screen
    val movieGenreFilter = MutableStateFlow("All")
    val movieLanguageFilter = MutableStateFlow("All")
    val movieYearFilter = MutableStateFlow("All")
    val movieQualityFilter = MutableStateFlow("All")
    val movieSortOption = MutableStateFlow("Popular")

    data class FilterParams(
        val genre: String,
        val language: String,
        val year: String,
        val quality: String
    )

    private val filterParams = combine(
        movieGenreFilter,
        movieLanguageFilter,
        movieYearFilter,
        movieQualityFilter
    ) { genre, lang, yr, qual ->
        FilterParams(genre, lang, yr, qual)
    }

    // Filtered Movies for Movies Screen
    val filteredMovies: StateFlow<List<Movie>> = combine(
        allMovies,
        filterParams,
        movieSortOption
    ) { movies, filters, sort ->
        var list = movies
        if (filters.genre != "All") {
            list = list.filter { it.genres.any { g -> g.equals(filters.genre, ignoreCase = true) } }
        }
        if (filters.language != "All") {
            list = list.filter { it.language.contains(filters.language, ignoreCase = true) }
        }
        if (filters.year != "All") {
            val yrInt = filters.year.toIntOrNull()
            if (yrInt != null) {
                list = list.filter { it.year == yrInt }
            }
        }
        if (filters.quality != "All") {
            list = list.filter { it.quality.contains(filters.quality, ignoreCase = true) }
        }

        when (sort) {
            "Newest" -> list.sortedByDescending { it.year }
            "Highest Rated" -> list.sortedByDescending { it.rating }
            "A-Z" -> list.sortedBy { it.title }
            else -> list.sortedByDescending { if (it.isTrending) 100 - (it.trendingRank ?: 50) else 10 }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // Search Results
    val searchResults: StateFlow<List<Movie>> = combine(allMovies, _searchQuery) { movies, q ->
        val query = q.trim()
        if (query.isEmpty()) {
            emptyList()
        } else {
            movies.filter { movie ->
                movie.title.contains(query, ignoreCase = true) ||
                        movie.director.contains(query, ignoreCase = true) ||
                        movie.cast.any { it.contains(query, ignoreCase = true) } ||
                        movie.genres.any { it.contains(query, ignoreCase = true) } ||
                        movie.description.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Video Player State
    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _playerCurrentTimeMs = MutableStateFlow(0L)
    val playerCurrentTimeMs: StateFlow<Long> = _playerCurrentTimeMs.asStateFlow()

    private val _playerDurationMs = MutableStateFlow(7200000L) // 2 hours default
    val playerDurationMs: StateFlow<Long> = _playerDurationMs.asStateFlow()

    private val _playbackSpeed = MutableStateFlow(1.0f)
    val playbackSpeed: StateFlow<Float> = _playbackSpeed.asStateFlow()

    private val _selectedQuality = MutableStateFlow("1080p FHD")
    val selectedQuality: StateFlow<String> = _selectedQuality.asStateFlow()

    private val _selectedSubtitle = MutableStateFlow("English")
    val selectedSubtitle: StateFlow<String> = _selectedSubtitle.asStateFlow()

    private val _selectedAudioTrack = MutableStateFlow("English (Dolby Atmos)")
    val selectedAudioTrack: StateFlow<String> = _selectedAudioTrack.asStateFlow()

    private val _isControlsLocked = MutableStateFlow(false)
    val isControlsLocked: StateFlow<Boolean> = _isControlsLocked.asStateFlow()

    private val _volume = MutableStateFlow(0.85f)
    val volume: StateFlow<Float> = _volume.asStateFlow()

    private var playerProgressJob: Job? = null

    init {
        // Dismiss splash screen after smooth intro
        viewModelScope.launch {
            delay(1600)
            _isSplashVisible.value = false
        }
    }

    fun selectTab(tab: MainTab) {
        _selectedTab.value = tab
        // Close overlays if switching tabs
        _activeMovieDetail.value = null
        _isSearchOpen.value = false
        _isNotificationsOpen.value = false
        _isMyListOpen.value = false
        _selectedCategory.value = null
        _isAdminOpen.value = false
        _isAuthOpen.value = false
    }

    fun openMovieDetail(movie: Movie) {
        _activeMovieDetail.value = movie
    }

    fun closeMovieDetail() {
        _activeMovieDetail.value = null
    }

    fun playMovie(movie: Movie) {
        val user = userProfile.value
        val isUserPrem = user.isPremiumActive

        if (!movie.isPlayableFor(isUserPrem)) {
            when {
                movie.isRightsExpired -> {
                    showMessage("Playback unavailable: Streaming license window for '${movie.title}' has expired.")
                }
                movie.isScheduledRelease -> {
                    val dateStr = movie.publishAt?.let {
                        java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(it))
                    } ?: "a later date"
                    showMessage("'${movie.title}' is a scheduled release. Available on $dateStr.")
                }
                movie.isInPremiumEarlyAccess && !isUserPrem -> {
                    val freeDateStr = movie.freeReleaseDate?.let {
                        java.text.SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", java.util.Locale.US).format(java.util.Date(it))
                    } ?: "the scheduled date"
                    showMessage("'${movie.title}' is in Premium Early Access. Upgrade to Z1 Cinema Pro or wait until $freeDateStr.")
                }
                else -> {
                    showMessage("Playback unavailable: This title is not currently published.")
                }
            }
            return
        }

        _activePlayerMovie.value = movie
        _isPlaying.value = true
        _playerCurrentTimeMs.value = 45000L // 45 seconds in for demo
        _playerDurationMs.value = (movie.durationMinutes * 60 * 1000L).coerceAtLeast(3600000L)
        startPlaybackTicker()
    }

    fun closePlayer() {
        _activePlayerMovie.value = null
        playerProgressJob?.cancel()
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        if (_isPlaying.value) {
            startPlaybackTicker()
        } else {
            playerProgressJob?.cancel()
        }
    }

    fun seekRelative(deltaSeconds: Int) {
        val newMs = (_playerCurrentTimeMs.value + deltaSeconds * 1000L).coerceIn(0L, _playerDurationMs.value)
        _playerCurrentTimeMs.value = newMs
    }

    fun seekToPosition(positionFraction: Float) {
        val newMs = (positionFraction * _playerDurationMs.value).toLong().coerceIn(0L, _playerDurationMs.value)
        _playerCurrentTimeMs.value = newMs
    }

    fun setPlaybackSpeed(speed: Float) {
        _playbackSpeed.value = speed
        showMessage("Playback speed: ${speed}x")
    }

    fun setQuality(quality: String) {
        _selectedQuality.value = quality
        showMessage("Video quality changed to $quality")
    }

    fun setSubtitle(subtitle: String) {
        _selectedSubtitle.value = subtitle
        showMessage("Subtitles: $subtitle")
    }

    fun setAudioTrack(track: String) {
        _selectedAudioTrack.value = track
        showMessage("Audio: $track")
    }

    fun toggleLockControls() {
        _isControlsLocked.value = !_isControlsLocked.value
        showMessage(if (_isControlsLocked.value) "Controls Locked" else "Controls Unlocked")
    }

    fun setVolume(vol: Float) {
        _volume.value = vol.coerceIn(0f, 1f)
    }

    private fun startPlaybackTicker() {
        playerProgressJob?.cancel()
        playerProgressJob = viewModelScope.launch {
            while (isActive && _isPlaying.value) {
                delay(1000)
                val step = (1000 * _playbackSpeed.value).toLong()
                val next = _playerCurrentTimeMs.value + step
                if (next >= _playerDurationMs.value) {
                    _playerCurrentTimeMs.value = _playerDurationMs.value
                    _isPlaying.value = false
                    break
                } else {
                    _playerCurrentTimeMs.value = next
                }
            }
        }
    }

    // Watchlist & Favorites
    fun toggleWatchlist(movie: Movie) {
        repository.toggleWatchlist(movie.id)
        val isIn = repository.userProfile.value.watchlistMovieIds.contains(movie.id)
        showMessage(if (isIn) "Added to My List" else "Removed from My List")
    }

    fun toggleFavorite(movie: Movie) {
        repository.toggleFavorite(movie.id)
        val isFav = repository.userProfile.value.favoriteMovieIds.contains(movie.id)
        showMessage(if (isFav) "Added to Favorites" else "Removed from Favorites")
    }

    // Downloads
    fun downloadMovie(movie: Movie) {
        repository.startDownload(movie)
        showMessage("Download started: ${movie.title} (Licensed)")
    }

    fun pauseDownload(id: String) {
        repository.pauseDownload(id)
        showMessage("Download paused")
    }

    fun resumeDownload(id: String) {
        repository.resumeDownload(id)
        showMessage("Download resumed")
    }

    fun deleteDownload(id: String) {
        repository.cancelOrDeleteDownload(id)
        showMessage("Download removed")
    }

    // Search Controls
    fun openSearch() {
        _isSearchOpen.value = true
    }

    fun closeSearch() {
        _isSearchOpen.value = false
        _searchQuery.value = ""
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        if (query.length >= 3) {
            val list = recentSearches.value.toMutableList()
            if (!list.contains(query)) {
                list.add(0, query)
                recentSearches.value = list.take(8)
            }
        }
    }

    fun clearSearchHistory() {
        recentSearches.value = emptyList()
        showMessage("Search history cleared")
    }

    // Category Screen
    fun openCategory(category: MovieCategory) {
        _selectedCategory.value = category
    }

    fun closeCategory() {
        _selectedCategory.value = null
    }

    // Notifications
    fun openNotifications() {
        _isNotificationsOpen.value = true
    }

    fun closeNotifications() {
        _isNotificationsOpen.value = false
    }

    fun dismissNotification(id: String) {
        repository.dismissNotification(id)
    }

    // My List overlay
    fun openMyList() {
        _isMyListOpen.value = true
    }

    fun closeMyList() {
        _isMyListOpen.value = false
    }

    fun clearWatchHistory() {
        repository.clearWatchHistory()
        showMessage("Watch history cleared")
    }

    fun sendAnnouncement(title: String, message: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val res = repository.addAnnouncement(title, message)
            if (res.isSuccess) {
                showMessage("Announcement published to all users!")
                onComplete(true)
            } else {
                showMessage("Failed to publish announcement: ${res.exceptionOrNull()?.message}")
                onComplete(false)
            }
        }
    }

    fun deleteNotification(id: String) {
        viewModelScope.launch {
            val res = repository.deleteNotification(id)
            if (res.isSuccess) {
                showMessage("Announcement deleted")
            }
        }
    }

    // Admin Panel Actions & Dynamic Movie Management
    fun openAdmin() {
        _isAdminOpen.value = true
    }

    fun closeAdmin() {
        _isAdminOpen.value = false
    }

    fun loginAdmin(identifier: String, secret: String, onResult: (Boolean, String) -> Unit) {
        val result = authService.login(identifier, secret)
        result.fold(
            onSuccess = { session ->
                showMessage("Signed in as ${session.user.role.name}")
                onResult(true, "Authentication successful")
            },
            onFailure = { error ->
                onResult(false, error.message ?: "Authentication failed")
            }
        )
    }

    fun logoutAdmin() {
        authService.logout()
        showMessage("Admin session ended")
    }

    fun setAdminRole(role: UserRole) {
        authService.switchRole(role)
        showMessage("Switched role to ${role.name}")
    }

    fun saveMovie(movie: Movie, isEditing: Boolean, onComplete: (Boolean, String) -> Unit) {
        val currentRole = adminSession.value.user.role
        if (isEditing && !currentRole.canEditMovie()) {
            showMessage("Access Denied: You do not have permission to edit movies.")
            onComplete(false, "Permission denied")
            return
        }
        if (!isEditing && !currentRole.canAddMovie()) {
            showMessage("Access Denied: You do not have permission to add movies.")
            onComplete(false, "Permission denied")
            return
        }
        if (movie.isPublished && !currentRole.canPublishMovie()) {
            showMessage("Access Denied: Only Admins can publish live movies to the catalog.")
            onComplete(false, "Permission denied: Publishing requires Admin or Super Admin role.")
            return
        }

        viewModelScope.launch {
            val result = if (isEditing) {
                repository.updateMovie(movie)
            } else {
                repository.addMovie(movie)
            }
            result.fold(
                onSuccess = {
                    val statusText = if (movie.isPublished) "and published live to Z1 Movies app" else "saved as draft"
                    showMessage("Movie '${movie.title}' $statusText!")
                    onComplete(true, "Movie saved successfully")
                },
                onFailure = { err ->
                    showMessage("Error: ${err.message}")
                    onComplete(false, err.message ?: "Failed to save movie")
                }
            )
        }
    }

    fun togglePublishStatus(movieId: String, isPublished: Boolean) {
        val currentRole = adminSession.value.user.role
        if (!currentRole.canPublishMovie()) {
            showMessage("Access Denied: Only Admins can publish or unpublish movies.")
            return
        }
        viewModelScope.launch {
            repository.setMoviePublishStatus(movieId, isPublished)
            if (isPublished) {
                showMessage("Movie published live to Z1 Movies app!")
            } else {
                showMessage("Movie unpublished (moved to draft)")
            }
        }
    }

    fun deleteMovie(movieId: String) {
        val currentRole = adminSession.value.user.role
        if (!currentRole.canDeleteMovie()) {
            showMessage("Access Denied: Only Admins can delete movies from the catalog.")
            return
        }
        viewModelScope.launch {
            repository.deleteMovie(movieId)
            showMessage("Movie removed from catalog")
        }
    }

    // Category Management (Admin)
    fun saveCategory(category: MovieCategory, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val res = repository.saveCategory(category)
            if (res.isSuccess) {
                showMessage("Category '${category.name}' saved to catalog")
                onComplete?.invoke(true)
            } else {
                showMessage("Error saving category: ${res.exceptionOrNull()?.message}")
                onComplete?.invoke(false)
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            repository.deleteCategory(categoryId)
            showMessage("Category removed")
        }
    }

    // Banner Management (Admin)
    fun saveBanner(banner: BannerItem, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val res = repository.saveBanner(banner)
            if (res.isSuccess) {
                showMessage("Banner '${banner.title}' saved")
                onComplete?.invoke(true)
            } else {
                showMessage("Error saving banner: ${res.exceptionOrNull()?.message}")
                onComplete?.invoke(false)
            }
        }
    }

    fun deleteBanner(bannerId: String) {
        viewModelScope.launch {
            repository.deleteBanner(bannerId)
            showMessage("Banner deleted")
        }
    }

    fun setBannerActiveStatus(bannerId: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.setBannerActiveStatus(bannerId, isActive)
            showMessage(if (isActive) "Banner activated on Hero feed" else "Banner hidden from Hero feed")
        }
    }

    // Coming Soon & Notification System
    private val _notifiedComingSoonIds = MutableStateFlow<Set<String>>(emptySet())
    val notifiedComingSoonIds: StateFlow<Set<String>> = _notifiedComingSoonIds.asStateFlow()

    fun toggleComingSoonNotification(movie: Movie) {
        viewModelScope.launch {
            val current = _notifiedComingSoonIds.value
            val isNowNotified = if (current.contains(movie.id)) {
                _notifiedComingSoonIds.value = current - movie.id
                showMessage("Alert cancelled for ${movie.title}")
                false
            } else {
                _notifiedComingSoonIds.value = current + movie.id
                showMessage("Reminder set! We will notify you when ${movie.title} premieres.")
                true
            }
            if (isNowNotified) {
                // Post confirmation notification item
                repository.createNotification(
                    title = "Reminder Scheduled: ${movie.title}",
                    message = "You will receive an instant notification when ${movie.title} becomes available for streaming.",
                    type = com.example.data.model.NotificationType.NEW_RELEASE,
                    targetMovieId = movie.id
                )
            }
        }
    }

    // Movie Issue Reporting
    fun submitMovieReport(movieId: String, movieTitle: String, issueType: String, description: String) {
        viewModelScope.launch {
            repository.createNotification(
                title = "Report Submitted: $movieTitle",
                message = "Thank you! Our engineering & moderation team is investigating: $issueType ($description)",
                type = com.example.data.model.NotificationType.ANNOUNCEMENT,
                targetMovieId = movieId
            )
            showMessage("Issue reported successfully. Thank you for keeping Z1 Movies high quality!")
        }
    }

    // Room-to-Firestore Migration (Admin)
    fun migrateRoomToFirestore(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val res = repository.migrateRoomToFirestore()
            res.fold(
                onSuccess = { triple ->
                    val msg = "Migrated ${triple.first} movies, ${triple.second} categories, ${triple.third} banners to Firestore!"
                    showMessage(msg)
                    onResult(msg)
                },
                onFailure = { err ->
                    val msg = "Migration failed: ${err.message ?: "Check internet/credentials"}"
                    showMessage(msg)
                    onResult(msg)
                }
            )
        }
    }

    fun resetCatalogToDefault() {
        viewModelScope.launch {
            repository.resetToDefaultSeed()
            showMessage("Catalog reset to default curated titles")
        }
    }

    fun syncWithCloud(backendUrl: String, onResult: (String) -> Unit) {
        viewModelScope.launch {
            val res = repository.syncWithCloudBackend(backendUrl)
            res.fold(
                onSuccess = { count ->
                    val msg = "Synchronized $count movie titles from $backendUrl"
                    showMessage(msg)
                    onResult(msg)
                },
                onFailure = { err ->
                    val msg = "Sync error: ${err.message}"
                    showMessage(msg)
                    onResult(msg)
                }
            )
        }
    }

    // Auth Overlay
    fun openAuth() {
        _isAuthOpen.value = true
    }

    fun closeAuth() {
        _isAuthOpen.value = false
    }

    // Toggle user premium tier for testing / demoing Premium Early Access
    fun toggleUserPremiumTier() {
        val current = userProfile.value
        val newIsPremium = !current.isPremiumActive
        repository.updateUserProfile {
            it.copy(
                isPremium = newIsPremium,
                membershipTier = if (newIsPremium) "Z1 Cinema Pro" else "Free Member",
                premiumExpiryDate = if (newIsPremium) System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000L) else null
            )
        }
        showMessage(if (newIsPremium) "Upgraded to Z1 Cinema Pro (Premium Active)!" else "Switched to Free Tier (Standard Access)")
    }

    fun upgradeToPremium() {
        repository.updateUserProfile {
            it.copy(
                isPremium = true,
                membershipTier = "Z1 Cinema Pro",
                premiumExpiryDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000L)
            )
        }
        showMessage("Welcome to Z1 Cinema Pro! Enjoy Premium Early Access to all exclusive titles.")
    }

    // Live Channel Methods
    fun openLiveChannelDetail(channel: com.example.data.model.LiveChannel) {
        _selectedLiveChannel.value = channel
    }

    fun closeLiveChannelDetail() {
        _selectedLiveChannel.value = null
    }

    fun playLiveChannel(channel: com.example.data.model.LiveChannel) {
        val user = userProfile.value
        val isPrem = user.isPremiumActive

        if (!channel.isPlayableFor(isPrem)) {
            when {
                channel.isRightsExpired -> {
                    showMessage("Channel unavailable: Streaming rights license for '${channel.name}' has expired.")
                }
                channel.isRightsNotYetStarted -> {
                    showMessage("Channel unavailable: Streaming rights for '${channel.name}' have not started yet.")
                }
                channel.isPremiumOnly && !isPrem -> {
                    showMessage("'${channel.name}' is a Premium Channel. Upgrade to Z1 Cinema Pro to stream.")
                }
                !channel.isPublished || !channel.isEnabled -> {
                    showMessage("Channel unavailable: '${channel.name}' is currently offline.")
                }
                else -> {
                    showMessage("Playback unavailable for '${channel.name}'.")
                }
            }
            return
        }

        _activePlayerChannel.value = channel
        _isPlaying.value = true
    }

    fun closeLiveChannelPlayer() {
        _activePlayerChannel.value = null
    }

    fun toggleFavoriteChannel(channel: com.example.data.model.LiveChannel) {
        repository.toggleFavoriteChannel(channel.id)
        val isFav = userProfile.value.favoriteChannelIds.contains(channel.id)
        showMessage(if (!isFav) "Added '${channel.name}' to Favorites" else "Removed '${channel.name}' from Favorites")
    }

    fun submitLiveChannelReport(channelId: String, channelName: String, issueType: String, description: String) {
        viewModelScope.launch {
            repository.submitLiveChannelReport(channelId, channelName, issueType, description)
            showMessage("Report submitted for '$channelName'. Our operations team is investigating.")
        }
    }

    fun saveLiveChannel(channel: com.example.data.model.LiveChannel) {
        viewModelScope.launch {
            repository.saveLiveChannel(channel)
            showMessage("Live Channel '${channel.name}' saved successfully.")
        }
    }

    fun deleteLiveChannel(channelId: String) {
        viewModelScope.launch {
            repository.deleteLiveChannel(channelId)
            showMessage("Live Channel deleted.")
        }
    }

    fun toggleChannelPublish(channelId: String, isPublished: Boolean) {
        viewModelScope.launch {
            repository.toggleChannelPublish(channelId, isPublished)
            showMessage(if (isPublished) "Channel published to Live TV." else "Channel unpublished (hidden from users).")
        }
    }

    fun toggleLiveChannelPublish(channelId: String, isPublished: Boolean) = toggleChannelPublish(channelId, isPublished)

    fun toggleChannelEnabled(channelId: String, isEnabled: Boolean) {
        viewModelScope.launch {
            repository.toggleChannelEnabled(channelId, isEnabled)
            showMessage(if (isEnabled) "Channel enabled." else "Channel disabled.")
        }
    }

    fun toggleLiveChannelEnabled(channelId: String, isEnabled: Boolean) = toggleChannelEnabled(channelId, isEnabled)

    fun resolveLiveChannelReport(reportId: String) {
        viewModelScope.launch {
            repository.resolveLiveChannelReport(reportId)
            showMessage("Report marked as resolved.")
        }
    }

    fun showMessage(msg: String) {
        viewModelScope.launch {
            _feedbackMessage.value = msg
            delay(2800)
            if (_feedbackMessage.value == msg) {
                _feedbackMessage.value = null
            }
        }
    }

    fun clearFeedback() {
        _feedbackMessage.value = null
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return Z1MoviesViewModel(application, MovieRepository.getInstance(application)) as T
            }
        }
    }
}
