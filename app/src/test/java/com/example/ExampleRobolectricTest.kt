package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.Movie
import com.example.data.repository.MovieRepository
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.Z1MoviesViewModel
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Z1 Movies", appName)
  }

  @Test
  fun `test viewModel factory instantiation with application context`() {
    val application = ApplicationProvider.getApplicationContext<android.app.Application>()
    val vmFromConstructor = Z1MoviesViewModel(application)
    assertNotNull(vmFromConstructor)

    val factory = Z1MoviesViewModel.provideFactory(application)
    val vmFromFactory = factory.create(Z1MoviesViewModel::class.java)
    assertNotNull(vmFromFactory)
  }

  @Test
  fun `test movie repository and viewmodel lifecycle`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = MovieRepository.getInstance(context)
    val viewModel = Z1MoviesViewModel(repository)

    assertEquals(MainTab.HOME, viewModel.selectedTab.value)
    assertTrue(viewModel.allMovies.value.isNotEmpty())

    val firstMovie = viewModel.allMovies.value.first()
    assertNotNull(firstMovie.title)

    // Test watchlist toggle
    viewModel.toggleWatchlist(firstMovie)
    assertTrue(repository.userProfile.value.watchlistMovieIds.contains(firstMovie.id))

    // Test player
    viewModel.playMovie(firstMovie)
    assertEquals(firstMovie, viewModel.activePlayerMovie.value)
    assertTrue(viewModel.isPlaying.value)

    viewModel.closePlayer()
    assertEquals(null, viewModel.activePlayerMovie.value)
  }

  @Test
  fun `test admin dynamic movie publishing without app rebuild`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = MovieRepository.getInstance(context)
    val viewModel = Z1MoviesViewModel(repository)

    val customDraftMovie = Movie(
      id = "test_draft_${System.currentTimeMillis()}",
      title = "Quantum Frontier",
      tagline = "Into the unknown",
      description = "An intrepid explorer discovers a new celestial gateway.",
      posterUrl = "https://images.unsplash.com/photo-1509198397868-475647b2a1e5",
      backdropUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23",
      videoUrl = "https://cdn.z1movies.com/stream/quantum.m3u8",
      trailerUrl = "https://cdn.z1movies.com/trailers/quantum.mp4",
      year = 2026,
      duration = "2h 10m",
      durationMinutes = 130,
      rating = 4.9f,
      genres = listOf("Sci-Fi", "Adventure"),
      director = "Z1 Studios",
      cast = listOf("Dr. Vance", "Commander Ray"),
      language = "English",
      quality = "4K Ultra HD",
      isPublished = false // Draft initially!
    )

    // 1. Save as Draft
    repository.addMovie(customDraftMovie)

    // Must exist in allAdminMovies (for the Admin)
    assertTrue(viewModel.allAdminMovies.value.any { it.id == customDraftMovie.id })
    // But MUST NOT be visible to standard app users yet because isPublished=false
    assertFalse(viewModel.allMovies.value.any { it.id == customDraftMovie.id })

    // 2. Admin publishes the movie live
    repository.setMoviePublishStatus(customDraftMovie.id, isPublished = true)

    // Now it MUST appear in the user's movie stream dynamically!
    assertTrue(viewModel.allMovies.value.any { it.id == customDraftMovie.id })
  }

  @Test
  fun `test admin category and banner management with firestore dual layer`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repository = MovieRepository.getInstance(context)
    val viewModel = Z1MoviesViewModel(repository)

    // 1. Verify Category Creation & Retrieval
    val customCategory = com.example.data.model.MovieCategory(
      id = "cat_scifi_future",
      name = "Futuristic Sci-Fi",
      icon = "RocketLaunch",
      movieCount = 5,
      gradientStart = 0xFF0D9488,
      gradientEnd = 0xFF0284C7,
      description = "Mind-bending futuristic thrillers and space epics"
    )
    repository.saveCategory(customCategory)
    assertTrue(viewModel.categoriesFlow.value.any { it.id == customCategory.id })

    // 2. Verify Banner Creation & Active Toggle
    val customBanner = com.example.data.model.BannerItem(
      id = "banner_premiere_2026",
      title = "Cyber Odyssey",
      subtitle = "The Next Frontier in Cinematic 4K Streaming",
      imageUrl = "https://images.unsplash.com/photo-1518709268805-4e9042af9f23",
      movieId = "m1",
      badgeText = "PREMIERE",
      displayOrder = 1,
      isActive = true
    )
    repository.saveBanner(customBanner)
    assertTrue(viewModel.banners.value.any { it.id == customBanner.id })
    assertTrue(viewModel.allAdminBanners.value.any { it.id == customBanner.id })

    // Toggle active status
    repository.setBannerActiveStatus(customBanner.id, isActive = false)
    assertFalse(viewModel.banners.value.any { it.id == customBanner.id })
    assertTrue(viewModel.allAdminBanners.value.any { it.id == customBanner.id && !it.isActive })

    // 3. Verify Firestore fallback status is initialized
    assertNotNull(viewModel.firestoreSyncStatus.value)
  }
}
