package com.example.data.firestore

import android.content.Context
import android.util.Log
import com.example.data.model.BannerItem
import com.example.data.model.ContinueWatchingItem
import com.example.data.model.DownloadItem
import com.example.data.model.Movie
import com.example.data.model.MovieCategory
import com.example.data.model.NotificationItem
import com.example.data.model.UserProfile
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class FirestoreSyncStatus(
    val isConnected: Boolean = false,
    val isFallbackMode: Boolean = true,
    val statusMessage: String = "Initializing Firestore...",
    val movieCount: Int = 0,
    val categoryCount: Int = 0,
    val bannerCount: Int = 0,
    val lastSyncedAt: Long = 0L
)

class FirestoreService(private val context: Context) {

    private val tag = "FirestoreService"

    private val firestore: FirebaseFirestore? by lazy {
        try {
            val app = if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            } else {
                FirebaseApp.getInstance()
            }
            if (app != null) {
                val db = FirebaseFirestore.getInstance()
                // Enable offline caching and fast client responsiveness
                try {
                    val settings = FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .build()
                    db.firestoreSettings = settings
                } catch (e: Exception) {
                    Log.w(tag, "Firestore settings already applied or adjusted: ${e.message}")
                }
                Log.i(tag, "Firebase Firestore initialized successfully.")
                db
            } else {
                Log.w(tag, "FirebaseApp is null, operating in local fallback mode.")
                null
            }
        } catch (e: Throwable) {
            Log.w(tag, "Firestore not configured or unavailable (${e.message}). Falling back to local Room database.")
            null
        }
    }

    val isConfigured: Boolean
        get() = firestore != null

    // =========================================================================
    // 1. MOVIES
    // =========================================================================

    /**
     * User App: Observes only published movies in real-time from Firestore.
     */
    fun observePublishedMovies(): Flow<List<Movie>?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = db.collection(FirestoreConstants.COLLECTION_MOVIES)
            .whereEqualTo(FirestoreConstants.FIELD_IS_PUBLISHED, true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(tag, "Error listening to published movies from Firestore: ${error.message}")
                    trySend(null)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val movies = snapshot.documents.mapNotNull { FirestoreDto.movieFromDoc(it) }
                    trySend(movies)
                }
            }

        awaitClose { listener.remove() }
    }

    /**
     * Admin Portal: Observes all movies (published and drafts).
     */
    fun observeAllAdminMovies(): Flow<List<Movie>?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = db.collection(FirestoreConstants.COLLECTION_MOVIES)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(tag, "Error listening to admin movies from Firestore: ${error.message}")
                    trySend(null)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val movies = snapshot.documents.mapNotNull { FirestoreDto.movieFromDoc(it) }
                    trySend(movies)
                }
            }

        awaitClose { listener.remove() }
    }

    suspend fun saveMovie(movie: Movie): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val map = FirestoreDto.movieToMap(movie)
            db.collection(FirestoreConstants.COLLECTION_MOVIES)
                .document(movie.id)
                .set(map, SetOptions.merge())
                .await()
            Log.d(tag, "Movie saved to Firestore: ${movie.title} (${movie.id})")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to save movie to Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun deleteMovie(movieId: String): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            db.collection(FirestoreConstants.COLLECTION_MOVIES)
                .document(movieId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to delete movie from Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun setMoviePublishStatus(movieId: String, isPublished: Boolean): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            db.collection(FirestoreConstants.COLLECTION_MOVIES)
                .document(movieId)
                .update(
                    mapOf(
                        FirestoreConstants.FIELD_IS_PUBLISHED to isPublished,
                        FirestoreConstants.FIELD_UPDATED_AT to System.currentTimeMillis()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(tag, "Failed to update movie publish status: ${e.message}", e)
            Result.failure(e)
        }
    }

    // =========================================================================
    // 2. CATEGORIES
    // =========================================================================

    fun observeCategories(): Flow<List<MovieCategory>?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = db.collection(FirestoreConstants.COLLECTION_CATEGORIES)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(tag, "Error listening to categories: ${error.message}")
                    trySend(null)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val categories = snapshot.documents.mapNotNull { FirestoreDto.categoryFromDoc(it) }
                    trySend(categories)
                }
            }

        awaitClose { listener.remove() }
    }

    suspend fun saveCategory(category: MovieCategory, order: Int = 0): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val map = FirestoreDto.categoryToMap(category, order)
            db.collection(FirestoreConstants.COLLECTION_CATEGORIES)
                .document(category.id)
                .set(map, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteCategory(categoryId: String): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            db.collection(FirestoreConstants.COLLECTION_CATEGORIES)
                .document(categoryId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =========================================================================
    // 3. BANNERS
    // =========================================================================

    fun observeActiveBanners(): Flow<List<BannerItem>?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = db.collection(FirestoreConstants.COLLECTION_BANNERS)
            .whereEqualTo(FirestoreConstants.FIELD_IS_ACTIVE, true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val banners = snapshot.documents
                        .mapNotNull { FirestoreDto.bannerFromDoc(it) }
                        .sortedBy { it.displayOrder }
                    trySend(banners)
                }
            }

        awaitClose { listener.remove() }
    }

    fun observeAllBanners(): Flow<List<BannerItem>?> = callbackFlow {
        val db = firestore
        if (db == null) {
            trySend(null)
            close()
            return@callbackFlow
        }

        val listener = db.collection(FirestoreConstants.COLLECTION_BANNERS)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(null)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val banners = snapshot.documents
                        .mapNotNull { FirestoreDto.bannerFromDoc(it) }
                        .sortedBy { it.displayOrder }
                    trySend(banners)
                }
            }

        awaitClose { listener.remove() }
    }

    suspend fun saveBanner(banner: BannerItem): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val map = FirestoreDto.bannerToMap(banner)
            db.collection(FirestoreConstants.COLLECTION_BANNERS)
                .document(banner.id)
                .set(map, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteBanner(bannerId: String): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            db.collection(FirestoreConstants.COLLECTION_BANNERS)
                .document(bannerId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setBannerActiveStatus(bannerId: String, isActive: Boolean): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            db.collection(FirestoreConstants.COLLECTION_BANNERS)
                .document(bannerId)
                .update(
                    mapOf(
                        FirestoreConstants.FIELD_IS_ACTIVE to isActive,
                        FirestoreConstants.FIELD_UPDATED_AT to System.currentTimeMillis()
                    )
                )
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =========================================================================
    // 4. USERS & PREFERENCES
    // =========================================================================

    suspend fun saveUserProfile(profile: UserProfile): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val map = FirestoreDto.userToMap(profile)
            db.collection(FirestoreConstants.COLLECTION_USERS)
                .document(profile.id)
                .set(map, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =========================================================================
    // 5. WATCH HISTORY
    // =========================================================================

    suspend fun recordWatchHistory(userId: String, item: ContinueWatchingItem): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val docId = "${userId}_${item.movieId}"
            val map = FirestoreDto.watchHistoryToMap(userId, item)
            db.collection(FirestoreConstants.COLLECTION_WATCH_HISTORY)
                .document(docId)
                .set(map, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =========================================================================
    // 6. WATCHLIST
    // =========================================================================

    suspend fun updateWatchlist(userId: String, movieId: String, isAdded: Boolean): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val docId = "${userId}_${movieId}"
            val ref = db.collection(FirestoreConstants.COLLECTION_WATCHLIST).document(docId)
            if (isAdded) {
                ref.set(FirestoreDto.watchlistItemToMap(userId, movieId), SetOptions.merge()).await()
            } else {
                ref.delete().await()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =========================================================================
    // 7. NOTIFICATIONS
    // =========================================================================

    suspend fun postNotification(item: NotificationItem, targetUserId: String? = null): Result<Unit> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore unavailable"))
        return try {
            val map = FirestoreDto.notificationToMap(item, targetUserId)
            db.collection(FirestoreConstants.COLLECTION_NOTIFICATIONS)
                .document(item.id)
                .set(map, SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // =========================================================================
    // 8. BATCH MIGRATION & SEEDING (Room -> Firestore)
    // =========================================================================

    suspend fun migrateFromRoom(
        movies: List<Movie>,
        categories: List<MovieCategory>,
        banners: List<BannerItem>
    ): Result<Triple<Int, Int, Int>> {
        val db = firestore ?: return Result.failure(IllegalStateException("Firestore is not initialized or offline"))
        return try {
            var moviesUploaded = 0
            var categoriesUploaded = 0
            var bannersUploaded = 0

            // 1. Upload movies batch
            val movieBatch = db.batch()
            movies.forEach { movie ->
                val ref = db.collection(FirestoreConstants.COLLECTION_MOVIES).document(movie.id)
                movieBatch.set(ref, FirestoreDto.movieToMap(movie), SetOptions.merge())
                moviesUploaded++
            }
            movieBatch.commit().await()

            // 2. Upload categories batch
            val catBatch = db.batch()
            categories.forEachIndexed { index, cat ->
                val ref = db.collection(FirestoreConstants.COLLECTION_CATEGORIES).document(cat.id)
                catBatch.set(ref, FirestoreDto.categoryToMap(cat, index), SetOptions.merge())
                categoriesUploaded++
            }
            catBatch.commit().await()

            // 3. Upload banners batch
            val bannerBatch = db.batch()
            banners.forEach { banner ->
                val ref = db.collection(FirestoreConstants.COLLECTION_BANNERS).document(banner.id)
                bannerBatch.set(ref, FirestoreDto.bannerToMap(banner), SetOptions.merge())
                bannersUploaded++
            }
            bannerBatch.commit().await()

            Log.i(tag, "Batch migration to Firestore completed: $moviesUploaded movies, $categoriesUploaded categories, $bannersUploaded banners.")
            Result.success(Triple(moviesUploaded, categoriesUploaded, bannersUploaded))
        } catch (e: Exception) {
            Log.e(tag, "Batch migration failed: ${e.message}", e)
            Result.failure(e)
        }
    }
}
