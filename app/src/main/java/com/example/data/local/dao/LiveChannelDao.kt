package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entities.LiveChannelEntity
import com.example.data.local.entities.LiveChannelReportEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LiveChannelDao {

    @Query("SELECT * FROM live_channels ORDER BY displayOrder ASC, createdAt DESC")
    fun getAllChannels(): Flow<List<LiveChannelEntity>>

    @Query("SELECT * FROM live_channels WHERE id = :id")
    suspend fun getChannelById(id: String): LiveChannelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannel(channel: LiveChannelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChannels(channels: List<LiveChannelEntity>)

    @Update
    suspend fun updateChannel(channel: LiveChannelEntity)

    @Query("DELETE FROM live_channels WHERE id = :id")
    suspend fun deleteChannel(id: String)

    @Query("UPDATE live_channels SET isPublished = :isPublished WHERE id = :id")
    suspend fun updatePublishStatus(id: String, isPublished: Boolean)

    @Query("UPDATE live_channels SET isEnabled = :isEnabled WHERE id = :id")
    suspend fun updateEnabledStatus(id: String, isEnabled: Boolean)

    // Reports
    @Query("SELECT * FROM live_channel_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<LiveChannelReportEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: LiveChannelReportEntity)

    @Query("UPDATE live_channel_reports SET status = :status WHERE id = :id")
    suspend fun updateReportStatus(id: String, status: String)

    @Query("DELETE FROM live_channel_reports WHERE id = :id")
    suspend fun deleteReport(id: String)
}
