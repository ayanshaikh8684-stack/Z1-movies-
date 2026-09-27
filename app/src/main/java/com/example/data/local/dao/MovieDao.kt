package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies WHERE isPublished = 1 ORDER BY createdAt DESC")
    fun getAllPublishedMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies ORDER BY createdAt DESC")
    fun getAllMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    fun getMovieById(id: String): Flow<MovieEntity?>

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    suspend fun getMovieByIdSync(id: String): MovieEntity?

    @Query("SELECT * FROM movies WHERE isPublished = 1 AND isFeatured = 1 ORDER BY createdAt DESC")
    fun getFeaturedMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE isPublished = 1 AND isTrending = 1 ORDER BY trendingRank ASC")
    fun getTrendingMovies(): Flow<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<MovieEntity>)

    @Update
    suspend fun updateMovie(movie: MovieEntity)

    @Query("DELETE FROM movies WHERE id = :id")
    suspend fun deleteMovieById(id: String)

    @Query("UPDATE movies SET isPublished = :isPublished, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setPublishStatus(id: String, isPublished: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM movies")
    suspend fun getMovieCount(): Int

    @Query("SELECT COUNT(*) FROM movies WHERE isPublished = 1")
    suspend fun getPublishedCount(): Int

    @Query("SELECT COUNT(*) FROM movies WHERE isPublished = 0")
    suspend fun getDraftCount(): Int
}
