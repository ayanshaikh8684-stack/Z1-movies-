# ProGuard rules for Z1 Movies production release build

# Keep AndroidX & Jetpack Compose rules
-keep class androidx.compose.** { *; }

# Keep Room Database models and entities
-keep class androidx.room.** { *; }
-dontwarn androidx.room.paging.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# Keep Moshi & Retrofit data models
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}
-keep class com.squareup.moshi.** { *; }
-keep @com.squareup.moshi.JsonClass class * { *; }
-dontwarn com.squareup.moshi.**

# Keep Z1 Movies data models
-keep class com.example.data.model.** { *; }

# Keep Coroutines
-keepclassmembernames class kotlinx.coroutines.internal.MainDispatcherFactory {
    java.lang.String createDispatcher(java.util.List);
}
-keep class kotlinx.coroutines.** { *; }

# Coil Image Loader
-keep class coil.** { *; }
-dontwarn coil.**

# OkHttp & Retrofit
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**
