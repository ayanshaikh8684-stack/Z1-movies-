package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entities.ContinueWatchingEntity
import com.example.data.local.entities.FavoriteEntity
import com.example.data.local.entities.WatchlistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryDao {

    // Watchlist
    @Query("SELECT movieId FROM watchlist WHERE userId = :userId ORDER BY addedAt DESC")
    fun getWatchlistMovieIds(userId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToWatchlist(item: WatchlistEntity)

    @Query("DELETE FROM watchlist WHERE userId = :userId AND movieId = :movieId")
    suspend fun removeFromWatchlist(userId: String, movieId: String)

    // Favorites
    @Query("SELECT movieId FROM favorites WHERE userId = :userId ORDER BY addedAt DESC")
    fun getFavoriteMovieIds(userId: String): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addToFavorites(item: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE userId = :userId AND movieId = :movieId")
    suspend fun removeFromFavorites(userId: String, movieId: String)

    // Continue Watching
    @Query("SELECT * FROM continue_watching WHERE userId = :userId ORDER BY lastWatchedTimestamp DESC")
    fun getContinueWatching(userId: String): Flow<List<ContinueWatchingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveContinueWatching(item: ContinueWatchingEntity)

    @Query("DELETE FROM continue_watching WHERE userId = :userId AND movieId = :movieId")
    suspend fun deleteContinueWatching(userId: String, movieId: String)

    @Query("DELETE FROM continue_watching WHERE userId = :userId")
    suspend fun clearContinueWatching(userId: String)
}
