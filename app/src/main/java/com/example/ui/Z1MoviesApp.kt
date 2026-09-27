package com.example.ui

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.CustomTopAppBar
import com.example.ui.components.Z1BottomNavigation
import com.example.ui.screens.admin.AdminScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.categories.CategoriesScreen
import com.example.ui.screens.details.MovieDetailsScreen
import com.example.ui.screens.downloads.DownloadsScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.livetv.LiveChannelDetailSheet
import com.example.ui.screens.livetv.LiveChannelPlayerScreen
import com.example.ui.screens.livetv.LiveTvScreen
import com.example.ui.screens.movies.MoviesScreen
import com.example.ui.screens.mylist.MyListScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.player.VideoPlayerScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.theme.Z1MoviesTheme
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.viewmodel.MainTab
import com.example.ui.viewmodel.Z1MoviesViewModel

@Composable
fun Z1MoviesApp(
    viewModel: Z1MoviesViewModel = viewModel(
        factory = Z1MoviesViewModel.provideFactory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val isSplashVisible by viewModel.isSplashVisible.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val activeMovieDetail by viewModel.activeMovieDetail.collectAsState()
    val activePlayerMovie by viewModel.activePlayerMovie.collectAsState()
    val isSearchOpen by viewModel.isSearchOpen.collectAsState()
    val isNotificationsOpen by viewModel.isNotificationsOpen.collectAsState()
    val isMyListOpen by viewModel.isMyListOpen.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val isAdminOpen by viewModel.isAdminOpen.collectAsState()
    val isAuthOpen by viewModel.isAuthOpen.collectAsState()
    val feedbackMessage by viewModel.feedbackMessage.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    val allLiveChannels by viewModel.allLiveChannels.collectAsState()
    val liveChannelReports by viewModel.liveChannelReports.collectAsState()
    val selectedLiveChannel by viewModel.selectedLiveChannel.collectAsState()
    val activePlayerChannel by viewModel.activePlayerChannel.collectAsState()
    val searchChannelResults by viewModel.searchChannelResults.collectAsState()

    val allMovies by viewModel.allMovies.collectAsState()
    val allAdminMovies by viewModel.allAdminMovies.collectAsState()
    val banners by viewModel.banners.collectAsState()
    val allAdminBanners by viewModel.allAdminBanners.collectAsState()
    val categoriesFlow by viewModel.categoriesFlow.collectAsState()
    val firestoreSyncStatus by viewModel.firestoreSyncStatus.collectAsState()
    val adminSession by viewModel.adminSession.collectAsState()
    val filteredMovies by viewModel.filteredMovies.collectAsState()
    val continueWatching by viewModel.continueWatching.collectAsState()
    val downloads by viewModel.downloads.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val recentSearches by viewModel.recentSearches.collectAsState()

    val genreFilter by viewModel.movieGenreFilter.collectAsState()
    val languageFilter by viewModel.movieLanguageFilter.collectAsState()
    val qualityFilter by viewModel.movieQualityFilter.collectAsState()
    val sortOption by viewModel.movieSortOption.collectAsState()
    val notifiedComingSoonIds by viewModel.notifiedComingSoonIds.collectAsState()

    // Player State
    val isPlaying by viewModel.isPlaying.collectAsState()
    val playerCurrentTimeMs by viewModel.playerCurrentTimeMs.collectAsState()
    val playerDurationMs by viewModel.playerDurationMs.collectAsState()
    val playbackSpeed by viewModel.playbackSpeed.collectAsState()
    val selectedQuality by viewModel.selectedQuality.collectAsState()
    val selectedSubtitle by viewModel.selectedSubtitle.collectAsState()
    val selectedAudioTrack by viewModel.selectedAudioTrack.collectAsState()
    val isControlsLocked by viewModel.isControlsLocked.collectAsState()
    val volume by viewModel.volume.collectAsState()

    // Hardware Back Button Handling
    BackHandler(
        enabled = activePlayerMovie != null ||
                activePlayerChannel != null ||
                selectedLiveChannel != null ||
                activeMovieDetail != null ||
                isSearchOpen ||
                isNotificationsOpen ||
                isMyListOpen ||
                selectedCategory != null ||
                isAdminOpen ||
                isAuthOpen
    ) {
        when {
            activePlayerMovie != null -> viewModel.closePlayer()
            activePlayerChannel != null -> viewModel.closeLiveChannelPlayer()
            selectedLiveChannel != null -> viewModel.closeLiveChannelDetail()
            activeMovieDetail != null -> viewModel.closeMovieDetail()
            isSearchOpen -> viewModel.closeSearch()
            isNotificationsOpen -> viewModel.closeNotifications()
            isMyListOpen -> viewModel.closeMyList()
            selectedCategory != null -> viewModel.closeCategory()
            isAdminOpen -> viewModel.closeAdmin()
            isAuthOpen -> viewModel.closeAuth()
        }
    }

    Z1MoviesTheme(darkTheme = isDarkMode) {
        if (isSplashVisible) {
            SplashScreen()
        } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .background(CinemaDarkBackground),
            containerColor = CinemaDarkBackground,
            topBar = {
                // Top bar shown only on the primary Home tab when no full-screen overlays are active
                if (selectedTab == MainTab.HOME &&
                    activeMovieDetail == null &&
                    activePlayerMovie == null &&
                    !isSearchOpen &&
                    !isNotificationsOpen &&
                    !isMyListOpen &&
                    selectedCategory == null &&
                    !isAdminOpen &&
                    !isAuthOpen
                ) {
                    CustomTopAppBar(
                        onSearchClick = { viewModel.openSearch() },
                        onNotificationClick = { viewModel.openNotifications() },
                        onProfileClick = { viewModel.selectTab(MainTab.PROFILE) },
                        unreadNotificationCount = notifications.count { !it.isRead }
                    )
                }
            },
            bottomBar = {
                // Bottom navigation shown on the main tabs, hidden in fullscreen players
                if (activePlayerMovie == null && activePlayerChannel == null && !isAuthOpen) {
                    Z1BottomNavigation(
                        selectedTab = selectedTab,
                        onTabSelected = { tab -> viewModel.selectTab(tab) }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        top = if (selectedTab == MainTab.HOME &&
                            activeMovieDetail == null &&
                            activePlayerMovie == null &&
                            activePlayerChannel == null &&
                            !isSearchOpen &&
                            !isNotificationsOpen &&
                            !isMyListOpen &&
                            selectedCategory == null &&
                            !isAdminOpen &&
                            !isAuthOpen
                        ) innerPadding.calculateTopPadding() else 0.dp,
                        bottom = if (activePlayerMovie == null && activePlayerChannel == null && !isAuthOpen) innerPadding.calculateBottomPadding() else 0.dp
                    )
            ) {
                // 1. Primary Bottom Navigation Tab Content
                Crossfade(targetState = selectedTab, label = "tabCrossfade") { tab ->
                    when (tab) {
                        MainTab.HOME -> HomeScreen(
                            movies = allMovies,
                            continueWatching = continueWatching,
                            watchlistMovieIds = userProfile.watchlistMovieIds,
                            banners = banners,
                            categories = categoriesFlow,
                            isPremiumUser = userProfile.isPremiumActive,
                            onUpgradeClick = { viewModel.toggleUserPremiumTier() },
                            onCategoryClick = { cat -> viewModel.openCategory(cat) },
                            onMovieClick = { movie -> viewModel.openMovieDetail(movie) },
                            onWatchNowClick = { movie -> viewModel.playMovie(movie) },
                            onAddToListClick = { movie -> viewModel.toggleWatchlist(movie) },
                            onResumeWatchingClick = { item ->
                                val movie = allMovies.find { it.id == item.movieId }
                                if (movie != null) viewModel.playMovie(movie)
                            },
                            onSeeAllSection = { section ->
                                if (section == "Categories") {
                                    viewModel.selectTab(MainTab.CATEGORIES)
                                } else {
                                    viewModel.selectTab(MainTab.MOVIES)
                                }
                            }
                        )

                        MainTab.MOVIES -> MoviesScreen(
                            movies = filteredMovies,
                            watchlistMovieIds = userProfile.watchlistMovieIds,
                            onMovieClick = { movie -> viewModel.openMovieDetail(movie) },
                            onBookmarkToggle = { movie -> viewModel.toggleWatchlist(movie) },
                            selectedGenre = genreFilter,
                            onGenreSelected = { viewModel.movieGenreFilter.value = it },
                            selectedLanguage = languageFilter,
                            onLanguageSelected = { viewModel.movieLanguageFilter.value = it },
                            selectedQuality = qualityFilter,
                            onQualitySelected = { viewModel.movieQualityFilter.value = it },
                            selectedSort = sortOption,
                            onSortSelected = { viewModel.movieSortOption.value = it },
                            onSearchFieldClick = { viewModel.openSearch() }
                        )

                        MainTab.LIVE_TV -> LiveTvScreen(
                            channels = allLiveChannels,
                            favoriteChannelIds = userProfile.favoriteChannelIds,
                            onChannelClick = { ch -> viewModel.openLiveChannelDetail(ch) },
                            onFavoriteToggle = { ch -> viewModel.toggleFavoriteChannel(ch) },
                            onSearchClick = { viewModel.openSearch() }
                        )

                        MainTab.CATEGORIES -> CategoriesScreen(
                            categories = categoriesFlow,
                            allMovies = allMovies,
                            watchlistMovieIds = userProfile.watchlistMovieIds,
                            selectedCategory = selectedCategory,
                            onCategoryClick = { cat -> viewModel.openCategory(cat) },
                            onBackFromCategory = { viewModel.closeCategory() },
                            onMovieClick = { movie -> viewModel.openMovieDetail(movie) },
                            onBookmarkToggle = { movie -> viewModel.toggleWatchlist(movie) }
                        )

                        MainTab.DOWNLOADS -> DownloadsScreen(
                            downloads = downloads,
                            onPauseClick = { viewModel.pauseDownload(it) },
                            onResumeClick = { viewModel.resumeDownload(it) },
                            onDeleteClick = { viewModel.deleteDownload(it) },
                            onPlayDownloaded = { movieId ->
                                val movie = allMovies.find { it.id == movieId }
                                if (movie != null) viewModel.playMovie(movie)
                            },
                            onExploreMoviesClick = { viewModel.selectTab(MainTab.MOVIES) }
                        )

                        MainTab.PROFILE -> ProfileScreen(
                            userProfile = userProfile,
                            adminSession = adminSession,
                            isDarkMode = isDarkMode,
                            onToggleDarkMode = { viewModel.toggleDarkMode() },
                            onTogglePremiumTier = { viewModel.toggleUserPremiumTier() },
                            onMyListClick = { viewModel.openMyList() },
                            onLiveTvClick = { viewModel.selectTab(MainTab.LIVE_TV) },
                            onDownloadsClick = { viewModel.selectTab(MainTab.DOWNLOADS) },
                            onNotificationsClick = { viewModel.openNotifications() },
                            onAdminClick = { viewModel.openAdmin() },
                            onAuthClick = { viewModel.openAuth() },
                            onShowMessage = { viewModel.showMessage(it) }
                        )
                    }
                }

                // 2. Fullscreen Search Overlay
                AnimatedVisibility(
                    visible = isSearchOpen,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    SearchScreen(
                        searchQuery = searchQuery,
                        searchResults = searchResults,
                        searchChannelResults = searchChannelResults,
                        recentSearches = recentSearches,
                        popularSearches = viewModel.popularSearches,
                        watchlistMovieIds = userProfile.watchlistMovieIds,
                        favoriteChannelIds = userProfile.favoriteChannelIds,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onMovieClick = { movie ->
                            viewModel.openMovieDetail(movie)
                        },
                        onChannelClick = { channel ->
                            viewModel.openLiveChannelDetail(channel)
                        },
                        onBookmarkToggle = { movie -> viewModel.toggleWatchlist(movie) },
                        onChannelFavoriteToggle = { channel -> viewModel.toggleFavoriteChannel(channel) },
                        onClearHistory = { viewModel.clearSearchHistory() },
                        onCloseSearch = { viewModel.closeSearch() }
                    )
                }

                // 3. Notifications Overlay
                AnimatedVisibility(
                    visible = isNotificationsOpen,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    NotificationsScreen(
                        notifications = notifications,
                        onDismissNotification = { viewModel.dismissNotification(it) },
                        onNotificationClick = { item ->
                            val movie = allMovies.find { it.id == item.relatedMovieId }
                            if (movie != null) {
                                viewModel.closeNotifications()
                                viewModel.openMovieDetail(movie)
                            }
                        },
                        onBackClick = { viewModel.closeNotifications() }
                    )
                }

                // 4. My List Overlay
                AnimatedVisibility(
                    visible = isMyListOpen,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    MyListScreen(
                        allMovies = allMovies,
                        watchlistMovieIds = userProfile.watchlistMovieIds,
                        favoriteMovieIds = userProfile.favoriteMovieIds,
                        continueWatching = continueWatching,
                        onMovieClick = { movie -> viewModel.openMovieDetail(movie) },
                        onBookmarkToggle = { movie -> viewModel.toggleWatchlist(movie) },
                        onClearHistory = { viewModel.clearWatchHistory() },
                        onBackClick = { viewModel.closeMyList() },
                        onExploreMoviesClick = {
                            viewModel.closeMyList()
                            viewModel.selectTab(MainTab.MOVIES)
                        }
                    )
                }

                // 5. Admin Panel Overlay
                AnimatedVisibility(
                    visible = isAdminOpen,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    AdminScreen(
                        allMovies = allAdminMovies,
                        categories = categoriesFlow,
                        banners = allAdminBanners,
                        notifications = notifications,
                        liveChannels = allLiveChannels,
                        liveChannelReports = liveChannelReports,
                        firestoreSyncStatus = firestoreSyncStatus,
                        session = adminSession,
                        onLoginSubmit = { u, p ->
                            viewModel.loginAdmin(u, p) { _, _ -> }
                        },
                        onQuickRoleSelect = { role ->
                            viewModel.setAdminRole(role)
                        },
                        onSaveMovie = { movie, isEditing ->
                            viewModel.saveMovie(movie, isEditing) { _, _ -> }
                        },
                        onTogglePublish = { id, isPub ->
                            viewModel.togglePublishStatus(id, isPub)
                        },
                        onDeleteMovie = { id ->
                            viewModel.deleteMovie(id)
                        },
                        onSaveCategory = { cat ->
                            viewModel.saveCategory(cat)
                        },
                        onDeleteCategory = { id ->
                            viewModel.deleteCategory(id)
                        },
                        onSaveBanner = { banner ->
                            viewModel.saveBanner(banner)
                        },
                        onDeleteBanner = { id ->
                            viewModel.deleteBanner(id)
                        },
                        onToggleBannerActive = { id, isActive ->
                            viewModel.setBannerActiveStatus(id, isActive)
                        },
                        onSendAnnouncement = { title, msg ->
                            viewModel.sendAnnouncement(title, msg) {}
                        },
                        onDeleteNotification = { id ->
                            viewModel.deleteNotification(id)
                        },
                        onSaveLiveChannel = { channel ->
                            viewModel.saveLiveChannel(channel)
                        },
                        onDeleteLiveChannel = { id ->
                            viewModel.deleteLiveChannel(id)
                        },
                        onToggleLiveChannelPublish = { id, isPub ->
                            viewModel.toggleLiveChannelPublish(id, isPub)
                        },
                        onToggleLiveChannelEnabled = { id, isEnabled ->
                            viewModel.toggleLiveChannelEnabled(id, isEnabled)
                        },
                        onResolveLiveChannelReport = { reportId ->
                            viewModel.resolveLiveChannelReport(reportId)
                        },
                        onMigrateToFirestore = {
                            viewModel.migrateRoomToFirestore { msg ->
                                viewModel.showMessage(msg)
                            }
                        },
                        onLogout = {
                            viewModel.logoutAdmin()
                        },
                        onSyncWithCloud = { url ->
                            viewModel.syncWithCloud(url) {}
                        },
                        onResetCatalog = {
                            viewModel.resetCatalogToDefault()
                        },
                        onBackClick = { viewModel.closeAdmin() }
                    )
                }

                // 6. Auth Screen Overlay
                AnimatedVisibility(
                    visible = isAuthOpen,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    AuthScreen(
                        onDismiss = { viewModel.closeAuth() },
                        onSuccessLogin = { name ->
                            viewModel.closeAuth()
                            viewModel.showMessage("Welcome, $name!")
                        }
                    )
                }

                // 7. Movie Details Fullscreen Overlay
                AnimatedVisibility(
                    visible = activeMovieDetail != null,
                    enter = slideInVertically(initialOffsetY = { it / 3 }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it / 3 }) + fadeOut()
                ) {
                    val movie = activeMovieDetail
                    if (movie != null) {
                        MovieDetailsScreen(
                            movie = movie,
                            allMovies = allMovies,
                            watchlistMovieIds = userProfile.watchlistMovieIds,
                            isUserPremium = userProfile.isPremiumActive,
                            onBackClick = { viewModel.closeMovieDetail() },
                            onWatchNowClick = { m -> viewModel.playMovie(m) },
                            onAddToListClick = { m -> viewModel.toggleWatchlist(m) },
                            onDownloadClick = { m -> viewModel.downloadMovie(m) },
                            onMovieClick = { m -> viewModel.openMovieDetail(m) },
                            onShareClick = { m ->
                                viewModel.showMessage("Movie link copied: ${m.title}")
                            },
                            onUpgradeToPremiumClick = {
                                viewModel.upgradeToPremium()
                            },
                            onReportIssue = { movieId, movieTitle, issueType, desc ->
                                viewModel.submitMovieReport(movieId, movieTitle, issueType, desc)
                            },
                            onNotifyMeToggle = { m ->
                                viewModel.toggleComingSoonNotification(m)
                            },
                            isNotified = notifiedComingSoonIds.contains(movie.id)
                        )
                    }
                }

                // 8. Live Channel Detail Sheet Overlay
                AnimatedVisibility(
                    visible = selectedLiveChannel != null,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    val channel = selectedLiveChannel
                    if (channel != null) {
                        LiveChannelDetailSheet(
                            channel = channel,
                            isFavorite = userProfile.favoriteChannelIds.contains(channel.id),
                            isUserPremium = userProfile.isPremiumActive,
                            onPlayClick = {
                                viewModel.closeLiveChannelDetail()
                                viewModel.playLiveChannel(channel)
                            },
                            onFavoriteToggle = { viewModel.toggleFavoriteChannel(channel) },
                            onShareClick = {
                                viewModel.showMessage("Live TV Stream link copied: ${channel.name}")
                            },
                            onSubmitReport = { issueType, desc ->
                                viewModel.submitLiveChannelReport(channel.id, channel.name, issueType, desc)
                            },
                            onUpgradeClick = { viewModel.upgradeToPremium() },
                            onClose = { viewModel.closeLiveChannelDetail() }
                        )
                    }
                }

                // 9. Live TV Video Player (Full Screen)
                AnimatedVisibility(
                    visible = activePlayerChannel != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val channel = activePlayerChannel
                    if (channel != null) {
                        LiveChannelPlayerScreen(
                            channel = channel,
                            isUserPremium = userProfile.isPremiumActive,
                            onClosePlayer = { viewModel.closeLiveChannelPlayer() }
                        )
                    }
                }

                // 10. Custom Video Player Screen (Topmost movie player layer)
                AnimatedVisibility(
                    visible = activePlayerMovie != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    val playerMovie = activePlayerMovie
                    if (playerMovie != null) {
                        VideoPlayerScreen(
                            movie = playerMovie,
                            isPlaying = isPlaying,
                            currentTimeMs = playerCurrentTimeMs,
                            durationMs = playerDurationMs,
                            playbackSpeed = playbackSpeed,
                            selectedQuality = selectedQuality,
                            selectedSubtitle = selectedSubtitle,
                            selectedAudioTrack = selectedAudioTrack,
                            isControlsLocked = isControlsLocked,
                            volume = volume,
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onSeekRelative = { delta -> viewModel.seekRelative(delta) },
                            onSeekToPosition = { fraction -> viewModel.seekToPosition(fraction) },
                            onSetPlaybackSpeed = { spd -> viewModel.setPlaybackSpeed(spd) },
                            onSetQuality = { q -> viewModel.setQuality(q) },
                            onSetSubtitle = { sub -> viewModel.setSubtitle(sub) },
                            onSetAudioTrack = { track -> viewModel.setAudioTrack(track) },
                            onToggleLock = { viewModel.toggleLockControls() },
                            onSetVolume = { v -> viewModel.setVolume(v) },
                            onClosePlayer = { viewModel.closePlayer() }
                        )
                    }
                }

                // 9. Toast / Feedback Banner
                AnimatedVisibility(
                    visible = feedbackMessage != null,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 16.dp)
                ) {
                    feedbackMessage?.let { msg ->
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = CinemaSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue),
                            shadowElevation = 8.dp
                        ) {
                            Text(
                                text = msg,
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
}
