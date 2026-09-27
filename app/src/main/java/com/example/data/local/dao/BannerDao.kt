package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.BannerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BannerDao {

    @Query("SELECT * FROM banners WHERE isActive = 1 ORDER BY displayOrder ASC, createdAt DESC")
    fun getActiveBanners(): Flow<List<BannerEntity>>

    @Query("SELECT * FROM banners ORDER BY displayOrder ASC, createdAt DESC")
    fun getAllBanners(): Flow<List<BannerEntity>>

    @Query("SELECT * FROM banners WHERE id = :id")
    suspend fun getBannerById(id: String): BannerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: BannerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanners(banners: List<BannerEntity>)

    @Update
    suspend fun updateBanner(banner: BannerEntity)

    @Query("DELETE FROM banners WHERE id = :id")
    suspend fun deleteBannerById(id: String)

    @Query("UPDATE banners SET isActive = :isActive WHERE id = :id")
    suspend fun setBannerActiveStatus(id: String, isActive: Boolean)

    @Query("SELECT COUNT(*) FROM banners")
    suspend fun getBannerCount(): Int
}
