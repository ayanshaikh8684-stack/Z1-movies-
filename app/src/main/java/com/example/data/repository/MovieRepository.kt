package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.firestore.FirestoreService
import com.example.data.firestore.FirestoreSyncStatus
import com.example.data.local.Z1Database
import com.example.data.local.entities.BannerEntity
import com.example.data.local.entities.CategoryEntity
import com.example.data.local.entities.ContinueWatchingEntity
import com.example.data.local.entities.DownloadEntity
import com.example.data.local.entities.FavoriteEntity
import com.example.data.local.entities.MovieEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.WatchlistEntity
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieRepository(
    private val database: Z1Database? = null,
    private val firestoreService: FirestoreService? = null
) {
    private val tag = "MovieRepository"
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    // User app: only sees published movies
    private val _movies = MutableStateFlow<List<Movie>>(SeedData.movies.filter { it.isPublished })
    val movies: StateFlow<List<Movie>> = _movies.asStateFlow()

    // Admin portal: sees all movies including drafts and un-published
    private val _allAdminMovies = MutableStateFlow<List<Movie>>(SeedData.movies)
    val allAdminMovies: StateFlow<List<Movie>> = _allAdminMovies.asStateFlow()

    private val _categories = MutableStateFlow<List<MovieCategory>>(SeedData.categories)
    val categories: StateFlow<List<MovieCategory>> = _categories.asStateFlow()

    private val _banners = MutableStateFlow<List<BannerItem>>(SeedData.banners.filter { it.isActive })
    val banners: StateFlow<List<BannerItem>> = _banners.asStateFlow()

    private val _allAdminBanners = MutableStateFlow<List<BannerItem>>(SeedData.banners)
    val allAdminBanners: StateFlow<List<BannerItem>> = _allAdminBanners.asStateFlow()

    private val _continueWatching = MutableStateFlow<List<ContinueWatchingItem>>(SeedData.continueWatching)
    val continueWatching: StateFlow<List<ContinueWatchingItem>> = _continueWatching.asStateFlow()

    private val _downloads = MutableStateFlow<List<DownloadItem>>(SeedData.downloads)
    val downloads: StateFlow<List<DownloadItem>> = _downloads.asStateFlow()

    private val _notifications = MutableStateFlow<List<NotificationItem>>(SeedData.notifications)
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Live Channels
    private val _liveChannels = MutableStateFlow<List<com.example.data.model.LiveChannel>>(SeedData.liveChannels)
    val liveChannels: StateFlow<List<com.example.data.model.LiveChannel>> = _liveChannels.asStateFlow()

    private val _liveChannelReports = MutableStateFlow<List<com.example.data.model.LiveChannelReport>>(emptyList())
    val liveChannelReports: StateFlow<List<com.example.data.model.LiveChannelReport>> = _liveChannelReports.asStateFlow()

    private val _userProfile = MutableStateFlow(
        UserProfile(
            watchlistMovieIds = setOf("m1", "m2", "m8"),
            favoriteMovieIds = setOf("m1", "m5", "m9"),
            favoriteChannelIds = setOf("c1", "c3"),
            role = UserRole.USER
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Firestore Synchronization Status
    private val _firestoreSyncStatus = MutableStateFlow(
        FirestoreSyncStatus(
            isConnected = firestoreService?.isConfigured == true,
            isFallbackMode = firestoreService?.isConfigured != true,
            statusMessage = if (firestoreService?.isConfigured == true) {
                "Connected to Cloud Firestore"
            } else {
                "Operating in Local SQLite (Room) Mode (Fallback Active)"
            },
            movieCount = SeedData.movies.size,
            categoryCount = SeedData.categories.size,
            bannerCount = SeedData.banners.size,
            lastSyncedAt = System.currentTimeMillis()
        )
    )
    val firestoreSyncStatus: StateFlow<FirestoreSyncStatus> = _firestoreSyncStatus.asStateFlow()

    init {
        if (database != null) {
            initDatabase(database)
        }
        if (firestoreService != null && firestoreService.isConfigured) {
            initFirestore(firestoreService)
        }
    }

    private fun initDatabase(db: Z1Database) {
        repositoryScope.launch {
            // Seed Room database if empty
            if (db.movieDao().getMovieCount() == 0) {
                val movieEntities = SeedData.movies.map { MovieEntity.fromMovie(it) }
                db.movieDao().insertMovies(movieEntities)
            }
            if (db.categoryDao().getCategoryCount() == 0) {
                val categoryEntities = SeedData.categories.map { CategoryEntity.fromCategory(it) }
                db.categoryDao().insertCategories(categoryEntities)
            }
            if (db.bannerDao().getBannerCount() == 0) {
                val bannerEntities = SeedData.banners.map { BannerEntity.fromBannerItem(it) }
                db.bannerDao().insertBanners(bannerEntities)
            }
            if (db.liveChannelDao().getAllChannels().let { true }) {
                // Seed initial channels if needed
                val channelEntities = SeedData.liveChannels.map { com.example.data.local.entities.LiveChannelEntity.fromDomain(it) }
                db.liveChannelDao().insertChannels(channelEntities)
            }

            // Observe live channels
            launch {
                db.liveChannelDao().getAllChannels().collectLatest { entities ->
                    if (entities.isNotEmpty()) {
                        _liveChannels.value = entities.map { it.toDomain() }
                    }
                }
            }

            // Observe reports
            launch {
                db.liveChannelDao().getAllReports().collectLatest { entities ->
                    _liveChannelReports.value = entities.map { it.toDomain() }
                }
            }

            // Observe published movies for user app
            launch {
                db.movieDao().getAllPublishedMovies().collectLatest { entities ->
                    if (firestoreService == null || !firestoreService.isConfigured) {
                        _movies.value = entities.map { it.toMovie() }
                    }
                }
            }

            // Observe all movies for admin portal
            launch {
                db.movieDao().getAllMovies().collectLatest { entities ->
                    if (firestoreService == null || !firestoreService.isConfigured) {
                        _allAdminMovies.value = entities.map { it.toMovie() }
                    }
                }
            }

            // Observe categories
            launch {
                db.categoryDao().getAllCategories().collectLatest { entities ->
                    if (entities.isNotEmpty() && (firestoreService == null || !firestoreService.isConfigured)) {
                        _categories.value = entities.map { it.toMovieCategory() }
                    }
                }
            }

            // Observe banners
            launch {
                db.bannerDao().getActiveBanners().collectLatest { entities ->
                    if (entities.isNotEmpty() && (firestoreService == null || !firestoreService.isConfigured)) {
                        _banners.value = entities.map { it.toBannerItem() }
                    }
                }
            }
            launch {
                db.bannerDao().getAllBanners().collectLatest { entities ->
                    if (entities.isNotEmpty() && (firestoreService == null || !firestoreService.isConfigured)) {
                        _allAdminBanners.value = entities.map { it.toBannerItem() }
                    }
                }
            }

            // Observe continue watching
            launch {
                db.libraryDao().getContinueWatching(_userProfile.value.id).collectLatest { entities ->
                    if (entities.isNotEmpty()) {
                        _continueWatching.value = entities.map { it.toContinueWatchingItem() }
                    }
                }
            }

            // Observe downloads
            launch {
                db.downloadDao().getAllDownloads().collectLatest { entities ->
                    if (entities.isNotEmpty()) {
                        _downloads.value = entities.map { it.toDownloadItem() }
                    }
                }
            }

            // Observe notifications
            launch {
                db.notificationDao().getAllNotifications().collectLatest { entities ->
                    if (entities.isNotEmpty()) {
                        _notifications.value = entities.map { it.toNotificationItem() }
                    }
                }
            }

            // Observe watchlist
            launch {
                db.libraryDao().getWatchlistMovieIds(_userProfile.value.id).collectLatest { ids ->
                    _userProfile.update { it.copy(watchlistMovieIds = ids.toSet()) }
                }
            }

            // Observe favorites
            launch {
                db.libraryDao().getFavoriteMovieIds(_userProfile.value.id).collectLatest { ids ->
                    _userProfile.update { it.copy(favoriteMovieIds = ids.toSet()) }
                }
            }
        }
    }

    private fun initFirestore(fs: FirestoreService) {
        repositoryScope.launch {
            Log.i(tag, "Connecting app to live Firestore collections...")
            _firestoreSyncStatus.update {
                it.copy(
                    isConnected = true,
                    isFallbackMode = false,
                    statusMessage = "Live Realtime Sync Active with Firestore"
                )
            }

            // 1. Observe published movies in real-time from Firestore
            launch {
                fs.observePublishedMovies().collectLatest { firestoreMovies ->
                    if (firestoreMovies != null) {
                        Log.d(tag, "Firestore published movies update: ${firestoreMovies.size} items")
                        if (firestoreMovies.isNotEmpty()) {
                            _movies.value = firestoreMovies
                            // Cache to Room SQLite for instant offline access
                            database?.movieDao()?.insertMovies(firestoreMovies.map { MovieEntity.fromMovie(it) })
                            _firestoreSyncStatus.update {
                                it.copy(
                                    movieCount = firestoreMovies.size,
                                    lastSyncedAt = System.currentTimeMillis()
                                )
                            }
                        } else {
                            // If Firestore has no movies yet, initiate automatic migration from Room
                            Log.i(tag, "Firestore movies collection is empty. Auto-migrating Room data...")
                            migrateRoomToFirestore()
                        }
                    }
                }
            }

            // 2. Observe all admin movies (including drafts)
            launch {
                fs.observeAllAdminMovies().collectLatest { adminMovies ->
                    if (adminMovies != null && adminMovies.isNotEmpty()) {
                        _allAdminMovies.value = adminMovies
                        database?.movieDao()?.insertMovies(adminMovies.map { MovieEntity.fromMovie(it) })
                    }
                }
            }

            // 3. Observe categories from Firestore
            launch {
                fs.observeCategories().collectLatest { cats ->
                    if (cats != null && cats.isNotEmpty()) {
                        _categories.value = cats
                        database?.categoryDao()?.insertCategories(cats.map { CategoryEntity.fromCategory(it) })
                        _firestoreSyncStatus.update { it.copy(categoryCount = cats.size) }
                    }
                }
            }

            // 4. Observe active banners for user app
            launch {
                fs.observeActiveBanners().collectLatest { activeBanners ->
                    if (activeBanners != null && activeBanners.isNotEmpty()) {
                        _banners.value = activeBanners
                        database?.bannerDao()?.insertBanners(activeBanners.map { BannerEntity.fromBannerItem(it) })
                        _firestoreSyncStatus.update { it.copy(bannerCount = activeBanners.size) }
                    }
                }
            }

            // 5. Observe all banners for admin panel
            launch {
                fs.observeAllBanners().collectLatest { allBanners ->
                    if (allBanners != null && allBanners.isNotEmpty()) {
                        _allAdminBanners.value = allBanners
                        database?.bannerDao()?.insertBanners(allBanners.map { BannerEntity.fromBannerItem(it) })
                    }
                }
            }
        }
    }

    // =========================================================================
    // MOVIE MUTATIONS (Admin & User Sync)
    // =========================================================================

    suspend fun addMovie(movie: Movie): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Immediate in-memory reactive update for UI responsiveness
            _allAdminMovies.update { listOf(movie) + it.filterNot { m -> m.id == movie.id } }
            if (movie.isPublished) {
                _movies.update { listOf(movie) + it.filterNot { m -> m.id == movie.id } }
            } else {
                _movies.update { it.filterNot { m -> m.id == movie.id } }
            }

            // Persist to local Room cache
            if (database != null) {
                database.movieDao().insertMovie(MovieEntity.fromMovie(movie))
                if (movie.isPublished) {
                    val notif = NotificationEntity(
                        id = "notif_${System.currentTimeMillis()}",
                        title = "New Movie Available",
                        message = "'${movie.title}' is now streaming in ${movie.quality}!",
                        timestampText = "Just now",
                        type = NotificationType.NEW_RELEASE,
                        movieId = movie.id,
                        isRead = false
                    )
                    database.notificationDao().insertNotification(notif)
                }
            }

            // Push to Firestore Production Database
            if (firestoreService != null && firestoreService.isConfigured) {
                firestoreService.saveMovie(movie)
                if (movie.isPublished) {
                    val notifItem = NotificationItem(
                        id = "notif_${System.currentTimeMillis()}",
                        title = "New Movie Available",
                        message = "'${movie.title}' is now streaming in ${movie.quality}!",
                        timestampText = "Just now",
                        type = NotificationType.NEW_RELEASE,
                        movieId = movie.id,
                        isRead = false
                    )
                    firestoreService.postNotification(notifItem)
                }
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Error adding movie: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun updateMovie(movie: Movie): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _allAdminMovies.update { list -> list.map { if (it.id == movie.id) movie else it } }
            if (movie.isPublished) {
                _movies.update { list ->
                    if (list.any { it.id == movie.id }) {
                        list.map { if (it.id == movie.id) movie else it }
                    } else {
                        listOf(movie) + list
                    }
                }
            } else {
                _movies.update { list -> list.filterNot { it.id == movie.id } }
            }

            if (database != null) {
                database.movieDao().updateMovie(MovieEntity.fromMovie(movie))
            }

            if (firestoreService != null && firestoreService.isConfigured) {
                firestoreService.saveMovie(movie)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteMovie(movieId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _allAdminMovies.update { list -> list.filterNot { it.id == movieId } }
            _movies.update { list -> list.filterNot { it.id == movieId } }

            if (database != null) {
                database.movieDao().deleteMovieById(movieId)
            }

            if (firestoreService != null && firestoreService.isConfigured) {
                firestoreService.deleteMovie(movieId)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setMoviePublishStatus(movieId: String, isPublished: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _allAdminMovies.update { list ->
                list.map { if (it.id == movieId) it.copy(isPublished = isPublished) else it }
            }
            if (isPublished) {
                val movieToPublish = _allAdminMovies.value.find { it.id == movieId }
                if (movieToPublish != null) {
                    _movies.update { list ->
                        listOf(movieToPublish.copy(isPublished = true)) + list.filterNot { it.id == movieId }
                    }
                }
            } else {
                _movies.update { list -> list.filterNot { it.id == movieId } }
            }

            if (database != null) {
                database.movieDao().setPublishStatus(movieId, isPublished)
            }

            if (firestoreService != null && firestoreService.isConfigured) {
                firestoreService.setMoviePublishStatus(movieId, isPublished)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =========================================================================
    // CATEGORY MUTATIONS (Admin)
    // =========================================================================

    suspend fun saveCategory(category: MovieCategory): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _categories.update { current ->
                if (current.any { it.id == category.id }) {
                    current.map { if (it.id == category.id) category else it }
                } else {
                    current + category
                }
            }

            if (database != null) {
                database.categoryDao().insertCategory(CategoryEntity.fromCategory(category))
            }

            if (firestoreService != null && firestoreService.isConfigured) {
                firestoreService.saveCategory(category)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _categories.update { list -> list.filterNot { it.id == categoryId } }

            if (database != null) {
                database.categoryDao().deleteCategoryById(categoryId)
            }

            if (firestoreService != null && firestoreService.isConfigured) {
                firestoreService.deleteCategory(categoryId)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =========================================================================
    // BANNER MUTATIONS (Admin)
    // =========================================================================

    suspend fun saveBanner(banner: BannerItem): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _allAdminBanners.update { current ->
                if (current.any { it.id == banner.id }) {
                    current.map { if (it.id == banner.id) banner else it }
                } else {
                    current + banner
                }
            }
            if (banner.isActive) {
                _banners.update { current ->
                    if (current.any { it.id == banner.id }) {
                        current.map { if (it.id == banner.id) banner else it }
                    } else {
                        current + banner
                    }
                }
            } else {
                _banners.update { it.filterNot { b -> b.id == banner.id } }
            }

            if (database != null) {
                database.bannerDao().insertBanner(BannerEntity.fromBannerItem(banner))
            }

            if (firestoreService != null && firestoreService.isConfigured) {
                firestoreService.saveBanner(banner)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBanner(bannerId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _allAdminBanners.update { it.filterNot { b -> b.id == bannerId } }
            _banners.update { it.filterNot { b -> b.id == bannerId } }

            if (database != null) {
                database.bannerDao().deleteBannerById(bannerId)
            }

            if (firestoreService != null && firestoreService.isConfigured) {
                firestoreService.deleteBanner(bannerId)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setBannerActiveStatus(bannerId: String, isActive: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            _allAdminBanners.update { list ->
                list.map { if (it.id == bannerId) it.copy(isActive = isActive) else it }
            }
            val target = _allAdminBanners.value.find { it.id == bannerId }
            if (target != null) {
                if (isActive) {
                    _banners.update { list -> (list + target.copy(isActive = true)).distinctBy { it.id } }
                } else {
                    _banners.update { list -> list.filterNot { it.id == bannerId } }
                }
            }

            if (database != null) {
                database.bannerDao().setBannerActiveStatus(bannerId, isActive)
            }

            if (firestoreService != null && firestoreService.isConfigured) {
                firestoreService.setBannerActiveStatus(bannerId, isActive)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =========================================================================
    // ROOM -> FIRESTORE MIGRATION LAYER
    // =========================================================================

    suspend fun migrateRoomToFirestore(): Result<Triple<Int, Int, Int>> = withContext(Dispatchers.IO) {
        val fs = firestoreService
        if (fs == null || !fs.isConfigured) {
            return@withContext Result.failure(
                IllegalStateException("Firestore is not yet configured. Please add google-services.json to app/")
            )
        }

        try {
            _firestoreSyncStatus.update { it.copy(statusMessage = "Migrating catalog from Room to Firestore...") }
            val movies = _allAdminMovies.value
            val categories = _categories.value
            val banners = _allAdminBanners.value

            val result = fs.migrateFromRoom(movies, categories, banners)
            result.onSuccess { triple ->
                _firestoreSyncStatus.update {
                    it.copy(
                        isConnected = true,
                        isFallbackMode = false,
                        statusMessage = "Migration Successful! ${triple.first} movies, ${triple.second} categories, ${triple.third} banners live in Firestore.",
                        movieCount = triple.first,
                        categoryCount = triple.second,
                        bannerCount = triple.third,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                }
            }.onFailure { err ->
                _firestoreSyncStatus.update {
                    it.copy(statusMessage = "Migration error: ${err.message}. Local Room fallback remains active.")
                }
            }
            result
        } catch (e: Exception) {
            Log.e(tag, "Migration error: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun resetToDefaultSeed(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (database != null) {
                val entities = SeedData.movies.map { MovieEntity.fromMovie(it) }
                database.movieDao().insertMovies(entities)
                database.categoryDao().insertCategories(SeedData.categories.map { CategoryEntity.fromCategory(it) })
                database.bannerDao().insertBanners(SeedData.banners.map { BannerEntity.fromBannerItem(it) })
            } else {
                _allAdminMovies.value = SeedData.movies
                _movies.value = SeedData.movies.filter { it.isPublished }
                _categories.value = SeedData.categories
                _banners.value = SeedData.banners.filter { it.isActive }
                _allAdminBanners.value = SeedData.banners
            }

            if (firestoreService != null && firestoreService.isConfigured) {
                migrateRoomToFirestore()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun syncWithCloudBackend(backendUrl: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            if (firestoreService != null && firestoreService.isConfigured) {
                val res = migrateRoomToFirestore()
                if (res.isSuccess) {
                    return@withContext Result.success(res.getOrNull()?.first ?: _allAdminMovies.value.size)
                }
            }
            val totalMovies = if (database != null) database.movieDao().getMovieCount() else _allAdminMovies.value.size
            Result.success(totalMovies)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =========================================================================
    // USER LIBRARY & PLAYBACK SYNC
    // =========================================================================

    fun toggleWatchlist(movieId: String) {
        val userId = _userProfile.value.id
        val isCurrent = _userProfile.value.watchlistMovieIds.contains(movieId)
        val shouldAdd = !isCurrent

        // Immediate reactive update for instant UI feedback
        _userProfile.update { current ->
            val set = current.watchlistMovieIds.toMutableSet()
            if (set.contains(movieId)) set.remove(movieId) else set.add(movieId)
            current.copy(watchlistMovieIds = set)
        }

        repositoryScope.launch {
            if (database != null) {
                if (isCurrent) {
                    database.libraryDao().removeFromWatchlist(userId, movieId)
                } else {
                    database.libraryDao().addToWatchlist(WatchlistEntity(userId, movieId))
                }
            }

            if (firestoreService != null && firestoreService.isConfigured) {
                firestoreService.updateWatchlist(userId, movieId, shouldAdd)
            }
        }
    }

    fun toggleFavorite(movieId: String) {
        val userId = _userProfile.value.id
        val isCurrent = _userProfile.value.favoriteMovieIds.contains(movieId)

        // Immediate reactive update for instant UI feedback
        _userProfile.update { current ->
            val set = current.favoriteMovieIds.toMutableSet()
            if (set.contains(movieId)) set.remove(movieId) else set.add(movieId)
            current.copy(favoriteMovieIds = set)
        }

        repositoryScope.launch {
            if (database != null) {
                if (isCurrent) {
                    database.libraryDao().removeFromFavorites(userId, movieId)
                } else {
                    database.libraryDao().addToFavorites(FavoriteEntity(userId, movieId))
                }
            }
        }
    }

    fun recordContinueWatching(movie: Movie, progressSeconds: Int, durationSeconds: Int) {
        repositoryScope.launch {
            val userId = _userProfile.value.id
            val item = ContinueWatchingItem(
                movieId = movie.id,
                title = movie.title,
                thumbnail = movie.backdropUrl.ifBlank { movie.posterUrl },
                positionSeconds = progressSeconds,
                durationSeconds = durationSeconds,
                genre = movie.genres.firstOrNull() ?: "Cinema",
                lastWatchedText = "Just now"
            )
            if (database != null) {
                database.libraryDao().saveContinueWatching(
                    ContinueWatchingEntity.fromItem(userId, item)
                )
            } else {
                _continueWatching.update { list ->
                    listOf(item) + list.filterNot { it.movieId == movie.id }
                }
            }

            if (firestoreService != null && firestoreService.isConfigured) {
                firestoreService.recordWatchHistory(userId, item)
            }
        }
    }

    fun startDownload(movie: Movie) {
        repositoryScope.launch {
            val existing = _downloads.value.find { it.movieId == movie.id }
            if (existing != null) return@launch

            val newDownload = DownloadItem(
                id = "d_${System.currentTimeMillis()}",
                movieId = movie.id,
                movieTitle = movie.title,
                posterUrl = movie.posterUrl,
                progress = 0.05f,
                status = DownloadStatus.DOWNLOADING,
                fileSizeFormatted = "2.1 GB",
                downloadedSizeFormatted = "105 MB",
                quality = movie.quality
            )
            if (database != null) {
                database.downloadDao().insertDownload(DownloadEntity.fromItem(newDownload))
            } else {
                _downloads.update { listOf(newDownload) + it }
            }
        }
    }

    fun pauseDownload(id: String) {
        repositoryScope.launch {
            if (database != null) {
                database.downloadDao().updateDownloadStatus(id, DownloadStatus.PAUSED)
            } else {
                _downloads.update { list ->
                    list.map { if (it.id == id) it.copy(status = DownloadStatus.PAUSED, speedFormatted = "Paused") else it }
                }
            }
        }
    }

    fun resumeDownload(id: String) {
        repositoryScope.launch {
            if (database != null) {
                database.downloadDao().updateDownloadStatus(id, DownloadStatus.DOWNLOADING)
            } else {
                _downloads.update { list ->
                    list.map { if (it.id == id) it.copy(status = DownloadStatus.DOWNLOADING, speedFormatted = "4.9 MB/s") else it }
                }
            }
        }
    }

    fun cancelOrDeleteDownload(id: String) {
        repositoryScope.launch {
            if (database != null) {
                database.downloadDao().deleteDownloadById(id)
            } else {
                _downloads.update { list -> list.filterNot { it.id == id } }
            }
        }
    }

    fun dismissNotification(id: String) {
        repositoryScope.launch {
            if (database != null) {
                database.notificationDao().deleteNotificationById(id)
            } else {
                _notifications.update { list -> list.filterNot { it.id == id } }
            }
        }
    }

    fun clearWatchHistory() {
        val userId = _userProfile.value.id
        _continueWatching.value = emptyList()
        repositoryScope.launch {
            if (database != null) {
                database.libraryDao().clearContinueWatching(userId)
            }
        }
    }

    suspend fun addAnnouncement(title: String, message: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val announcement = NotificationItem(
                id = "notif_ann_${System.currentTimeMillis()}",
                title = title,
                message = message,
                timestampText = "Just now",
                type = com.example.data.model.NotificationType.ANNOUNCEMENT,
                movieId = null,
                isRead = false
            )
            if (database != null) {
                database.notificationDao().insertNotification(
                    com.example.data.local.entities.NotificationEntity.fromItem(announcement)
                )
            } else {
                _notifications.update { listOf(announcement) + it }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createNotification(
        title: String,
        message: String,
        type: com.example.data.model.NotificationType = com.example.data.model.NotificationType.ANNOUNCEMENT,
        targetMovieId: String? = null
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val notif = NotificationItem(
                id = "notif_${System.currentTimeMillis()}",
                title = title,
                message = message,
                timestampText = "Just now",
                type = type,
                movieId = targetMovieId,
                isRead = false
            )
            if (database != null) {
                database.notificationDao().insertNotification(
                    com.example.data.local.entities.NotificationEntity.fromItem(notif)
                )
            } else {
                _notifications.update { listOf(notif) + it }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNotification(id: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (database != null) {
                database.notificationDao().deleteNotificationById(id)
            } else {
                _notifications.update { list -> list.filterNot { it.id == id } }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun updateUserProfile(transform: (UserProfile) -> UserProfile) {
        _userProfile.update(transform)
    }

    // Live Channels Operations
    suspend fun saveLiveChannel(channel: com.example.data.model.LiveChannel): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (database != null) {
                database.liveChannelDao().insertChannel(com.example.data.local.entities.LiveChannelEntity.fromDomain(channel))
            } else {
                _liveChannels.update { list ->
                    val index = list.indexOfFirst { it.id == channel.id }
                    if (index >= 0) list.toMutableList().apply { set(index, channel) }
                    else listOf(channel) + list
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteLiveChannel(channelId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (database != null) {
                database.liveChannelDao().deleteChannel(channelId)
            } else {
                _liveChannels.update { it.filterNot { ch -> ch.id == channelId } }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleChannelPublish(channelId: String, isPublished: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (database != null) {
                database.liveChannelDao().updatePublishStatus(channelId, isPublished)
            } else {
                _liveChannels.update { list ->
                    list.map { if (it.id == channelId) it.copy(isPublished = isPublished) else it }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleChannelEnabled(channelId: String, isEnabled: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (database != null) {
                database.liveChannelDao().updateEnabledStatus(channelId, isEnabled)
            } else {
                _liveChannels.update { list ->
                    list.map { if (it.id == channelId) it.copy(isEnabled = isEnabled) else it }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun toggleFavoriteChannel(channelId: String) {
        _userProfile.update { user ->
            val set = user.favoriteChannelIds.toMutableSet()
            if (set.contains(channelId)) set.remove(channelId) else set.add(channelId)
            user.copy(favoriteChannelIds = set)
        }
    }

    suspend fun submitLiveChannelReport(channelId: String, channelName: String, issueType: String, description: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val report = com.example.data.model.LiveChannelReport(
                id = "rep_${System.currentTimeMillis()}",
                channelId = channelId,
                channelName = channelName,
                issueType = issueType,
                description = description
            )
            if (database != null) {
                database.liveChannelDao().insertReport(com.example.data.local.entities.LiveChannelReportEntity.fromDomain(report))
            } else {
                _liveChannelReports.update { listOf(report) + it }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resolveLiveChannelReport(reportId: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (database != null) {
                database.liveChannelDao().updateReportStatus(reportId, "Resolved")
            } else {
                _liveChannelReports.update { list ->
                    list.map { if (it.id == reportId) it.copy(status = "Resolved") else it }
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MovieRepository? = null

        fun getInstance(context: Context): MovieRepository {
            return INSTANCE ?: synchronized(this) {
                val appCtx = context.applicationContext
                val db = Z1Database.getInstance(appCtx)
                val firestoreService = FirestoreService(appCtx)
                val repo = MovieRepository(db, firestoreService)
                INSTANCE = repo
                repo
            }
        }
    }
}
