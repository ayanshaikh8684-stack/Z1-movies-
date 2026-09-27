package com.example.data.remote

import com.example.data.model.Movie
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class AdminLoginRequest(
    val emailOrUsername: String,
    val secretKeyOrPin: String
)

data class AdminLoginResponse(
    val success: Boolean,
    val token: String,
    val role: String,
    val displayName: String,
    val message: String
)

data class MediaUploadResponse(
    val fileUrl: String,
    val cdnUrl: String,
    val storageBucket: String,
    val sizeBytes: Long
)

interface Z1ApiService {

    @GET("api/v1/movies")
    suspend fun getPublishedMovies(
        @Query("genre") genre: String? = null,
        @Query("page") page: Int = 1
    ): Response<List<Movie>>

    @GET("api/v1/admin/movies")
    suspend fun getAllAdminMovies(
        @Header("Authorization") token: String
    ): Response<List<Movie>>

    @POST("api/v1/admin/movies")
    suspend fun publishMovie(
        @Header("Authorization") token: String,
        @Body movie: Movie
    ): Response<Movie>

    @PUT("api/v1/admin/movies/{id}")
    suspend fun updateMovie(
        @Header("Authorization") token: String,
        @Path("id") movieId: String,
        @Body movie: Movie
    ): Response<Movie>

    @DELETE("api/v1/admin/movies/{id}")
    suspend fun deleteMovie(
        @Header("Authorization") token: String,
        @Path("id") movieId: String
    ): Response<Unit>

    @POST("api/v1/admin/auth/login")
    suspend fun adminLogin(
        @Body request: AdminLoginRequest
    ): Response<AdminLoginResponse>
}
