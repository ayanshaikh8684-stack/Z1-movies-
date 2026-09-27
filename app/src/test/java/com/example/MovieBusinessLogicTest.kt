package com.example

import com.example.data.model.Movie
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieBusinessLogicTest {

    private fun createTestMovie(
        isPublished: Boolean = true,
        publishAt: Long? = null,
        rightsStartDate: Long? = null,
        rightsExpiryDate: Long? = null
    ): Movie {
        return Movie(
            id = "test-1",
            title = "Test Cinema Feature",
            description = "A test movie for rights and scheduling validation.",
            duration = "2h 10m",
            durationMinutes = 130,
            rating = 8.8f,
            year = 2026,
            ageRating = "16+",
            quality = "4K HDR",
            posterUrl = "",
            backdropUrl = "",
            videoUrl = "https://test.stream/sample.mp4",
            trailerUrl = "",
            genres = listOf("Sci-Fi", "Action"),
            language = "English",
            director = "Elena Vance",
            cast = listOf("Actor A", "Actor B"),
            isFeatured = true,
            isTrending = false,
            isNewRelease = true,
            isPublished = isPublished,
            publishAt = publishAt,
            rightsStartDate = rightsStartDate,
            rightsExpiryDate = rightsExpiryDate
        )
    }

    @Test
    fun movie_publishedLiveWithoutRestrictions_isPlayable() {
        val movie = createTestMovie(isPublished = true)
        assertTrue(movie.isPublished)
        assertFalse(movie.isScheduledRelease)
        assertFalse(movie.isRightsExpired)
        assertTrue(movie.isPlayableForUser)
    }

    @Test
    fun movie_draft_isNotPlayable() {
        val movie = createTestMovie(isPublished = false)
        assertFalse(movie.isPublished)
        assertFalse(movie.isPlayableForUser)
    }

    @Test
    fun movie_scheduledInFuture_isNotPlayable() {
        val futureTime = System.currentTimeMillis() + 86400000L * 7 // 7 days in future
        val movie = createTestMovie(isPublished = true, publishAt = futureTime)
        assertTrue(movie.isPublished)
        assertTrue(movie.isScheduledRelease)
        assertFalse(movie.isPlayableForUser)
    }

    @Test
    fun movie_scheduledInPast_isPlayable() {
        val pastTime = System.currentTimeMillis() - 86400000L * 2 // 2 days ago
        val movie = createTestMovie(isPublished = true, publishAt = pastTime)
        assertTrue(movie.isPublished)
        assertFalse(movie.isScheduledRelease)
        assertTrue(movie.isPlayableForUser)
    }

    @Test
    fun movie_rightsExpired_isNotPlayable() {
        val pastExpiry = System.currentTimeMillis() - 86400000L // expired 1 day ago
        val movie = createTestMovie(isPublished = true, rightsExpiryDate = pastExpiry)
        assertTrue(movie.isPublished)
        assertTrue(movie.isRightsExpired)
        assertFalse(movie.isPlayableForUser)
    }

    @Test
    fun movie_rightsActiveWithFutureExpiry_isPlayable() {
        val futureExpiry = System.currentTimeMillis() + 86400000L * 30 // 30 days valid
        val movie = createTestMovie(isPublished = true, rightsExpiryDate = futureExpiry)
        assertTrue(movie.isPublished)
        assertFalse(movie.isRightsExpired)
        assertTrue(movie.isPlayableForUser)
    }

    @Test
    fun movie_rightsNotYetStarted_isNotPlayable() {
        val futureStart = System.currentTimeMillis() + 86400000L * 5 // Starts in 5 days
        val movie = createTestMovie(isPublished = true, rightsStartDate = futureStart)
        assertTrue(movie.isPublished)
        assertFalse(movie.isPlayableForUser)
    }
}
