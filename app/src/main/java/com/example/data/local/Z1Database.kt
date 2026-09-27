package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.converters.Z1TypeConverters
import com.example.data.local.dao.BannerDao
import com.example.data.local.dao.CategoryDao
import com.example.data.local.dao.DownloadDao
import com.example.data.local.dao.LibraryDao
import com.example.data.local.dao.MovieDao
import com.example.data.local.dao.NotificationDao
import com.example.data.local.dao.UserDao
import com.example.data.local.entities.BannerEntity
import com.example.data.local.entities.CategoryEntity
import com.example.data.local.entities.ContinueWatchingEntity
import com.example.data.local.entities.DownloadEntity
import com.example.data.local.entities.FavoriteEntity
import com.example.data.local.entities.MovieEntity
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.UserEntity
import com.example.data.local.entities.WatchlistEntity

@Database(
    entities = [
        MovieEntity::class,
        CategoryEntity::class,
        BannerEntity::class,
        UserEntity::class,
        WatchlistEntity::class,
        FavoriteEntity::class,
        ContinueWatchingEntity::class,
        DownloadEntity::class,
        NotificationEntity::class,
        com.example.data.local.entities.LiveChannelEntity::class,
        com.example.data.local.entities.LiveChannelReportEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Z1TypeConverters::class)
abstract class Z1Database : RoomDatabase() {

    abstract fun movieDao(): MovieDao
    abstract fun categoryDao(): CategoryDao
    abstract fun bannerDao(): BannerDao
    abstract fun userDao(): UserDao
    abstract fun libraryDao(): LibraryDao
    abstract fun downloadDao(): DownloadDao
    abstract fun notificationDao(): NotificationDao
    abstract fun liveChannelDao(): com.example.data.local.dao.LiveChannelDao

    companion object {
        @Volatile
        private var INSTANCE: Z1Database? = null

        fun getInstance(context: Context): Z1Database {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    Z1Database::class.java,
                    "z1_movies.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
