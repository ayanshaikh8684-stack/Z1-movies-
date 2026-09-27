package com.example.data.firestore

object FirestoreConstants {
    const val COLLECTION_MOVIES = "movies"
    const val COLLECTION_CATEGORIES = "categories"
    const val COLLECTION_USERS = "users"
    const val COLLECTION_WATCH_HISTORY = "watchHistory"
    const val COLLECTION_WATCHLIST = "watchlist"
    const val COLLECTION_DOWNLOADS = "downloads"
    const val COLLECTION_NOTIFICATIONS = "notifications"
    const val COLLECTION_BANNERS = "banners"

    // Metadata flags
    const val FIELD_IS_PUBLISHED = "isPublished"
    const val FIELD_IS_ACTIVE = "isActive"
    const val FIELD_USER_ID = "userId"
    const val FIELD_CREATED_AT = "createdAt"
    const val FIELD_UPDATED_AT = "updatedAt"
    const val FIELD_DISPLAY_ORDER = "displayOrder"
}
