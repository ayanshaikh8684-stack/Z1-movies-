package com.example.ui.screens.admin

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.data.firestore.FirestoreSyncStatus
import com.example.data.model.BannerItem
import com.example.data.model.Movie
import com.example.data.model.MovieCategory
import com.example.data.model.UserRole
import com.example.data.remote.AdminSession
import com.example.ui.theme.CinemaDarkBackground

enum class AdminTab {
    LOGIN,
    DASHBOARD,
    ADD_EDIT_MOVIE,
    MANAGE_CATALOG,
    MANAGE_CATEGORIES,
    MANAGE_BANNERS,
    MANAGE_ANNOUNCEMENTS,
    MANAGE_LIVE_TV,
    ADD_EDIT_LIVE_CHANNEL,
    ARCHITECTURE
}

@Composable
fun AdminScreen(
    allMovies: List<Movie>,
    categories: List<MovieCategory> = emptyList(),
    banners: List<BannerItem> = emptyList(),
    notifications: List<com.example.data.model.NotificationItem> = emptyList(),
    liveChannels: List<com.example.data.model.LiveChannel> = emptyList(),
    liveChannelReports: List<com.example.data.model.LiveChannelReport> = emptyList(),
    firestoreSyncStatus: FirestoreSyncStatus? = null,
    session: AdminSession,
    onLoginSubmit: (String, String) -> Unit,
    onQuickRoleSelect: (UserRole) -> Unit,
    onSaveMovie: (Movie, Boolean) -> Unit,
    onTogglePublish: (String, Boolean) -> Unit,
    onDeleteMovie: (String) -> Unit,
    onSaveCategory: (MovieCategory) -> Unit = {},
    onDeleteCategory: (String) -> Unit = {},
    onSaveBanner: (BannerItem) -> Unit = {},
    onDeleteBanner: (String) -> Unit = {},
    onToggleBannerActive: (String, Boolean) -> Unit = { _, _ -> },
    onSendAnnouncement: (title: String, message: String) -> Unit = { _, _ -> },
    onDeleteNotification: (String) -> Unit = {},
    onSaveLiveChannel: (com.example.data.model.LiveChannel) -> Unit = {},
    onDeleteLiveChannel: (String) -> Unit = {},
    onToggleLiveChannelPublish: (String, Boolean) -> Unit = { _, _ -> },
    onToggleLiveChannelEnabled: (String, Boolean) -> Unit = { _, _ -> },
    onResolveLiveChannelReport: (String) -> Unit = {},
    onMigrateToFirestore: () -> Unit = {},
    onLogout: () -> Unit,
    onSyncWithCloud: (String) -> Unit,
    onResetCatalog: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember(session.isAuthenticated) {
        mutableStateOf(if (session.isAuthenticated) AdminTab.DASHBOARD else AdminTab.LOGIN)
    }

    var editingMovie by remember { mutableStateOf<Movie?>(null) }
    var editingChannel by remember { mutableStateOf<com.example.data.model.LiveChannel?>(null) }

    // Sub-screen hardware back handling
    BackHandler(enabled = activeTab != AdminTab.DASHBOARD && activeTab != AdminTab.LOGIN) {
        activeTab = AdminTab.DASHBOARD
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
    ) {
        Crossfade(targetState = activeTab, label = "AdminTabCrossfade") { tab ->
            when (tab) {
                AdminTab.LOGIN -> {
                    AdminLoginView(
                        onLoginSubmit = { u, p ->
                            onLoginSubmit(u, p)
                            activeTab = AdminTab.DASHBOARD
                        },
                        onQuickRoleSelect = { role ->
                            onQuickRoleSelect(role)
                            activeTab = AdminTab.DASHBOARD
                        },
                        onBackToApp = onBackClick
                    )
                }

                AdminTab.DASHBOARD -> {
                    AdminDashboardView(
                        session = session,
                        allMovies = allMovies,
                        categories = categories,
                        banners = banners,
                        firestoreSyncStatus = firestoreSyncStatus,
                        onAddNewMovieClick = {
                            editingMovie = null
                            activeTab = AdminTab.ADD_EDIT_MOVIE
                        },
                        onManageCatalogClick = {
                            activeTab = AdminTab.MANAGE_CATALOG
                        },
                        onManageCategoriesClick = {
                            activeTab = AdminTab.MANAGE_CATEGORIES
                        },
                        onManageBannersClick = {
                            activeTab = AdminTab.MANAGE_BANNERS
                        },
                        onManageAnnouncementsClick = {
                            activeTab = AdminTab.MANAGE_ANNOUNCEMENTS
                        },
                        onManageLiveTvClick = {
                            activeTab = AdminTab.MANAGE_LIVE_TV
                        },
                        onMigrateToFirestoreClick = onMigrateToFirestore,
                        onBackendArchitectureClick = {
                            activeTab = AdminTab.ARCHITECTURE
                        },
                        onEditMovieClick = { movie ->
                            editingMovie = movie
                            activeTab = AdminTab.ADD_EDIT_MOVIE
                        },
                        onLogoutClick = {
                            onLogout()
                            activeTab = AdminTab.LOGIN
                        },
                        onBackToAppClick = onBackClick
                    )
                }

                AdminTab.ADD_EDIT_MOVIE -> {
                    AddEditMovieView(
                        existingMovie = editingMovie,
                        onSaveMovie = { movie, isEditing ->
                            onSaveMovie(movie, isEditing)
                            activeTab = AdminTab.MANAGE_CATALOG
                        },
                        onCancel = {
                            activeTab = AdminTab.DASHBOARD
                        }
                    )
                }

                AdminTab.MANAGE_CATALOG -> {
                    MovieManagementView(
                        allMovies = allMovies,
                        onTogglePublish = onTogglePublish,
                        onEditMovie = { movie ->
                            editingMovie = movie
                            activeTab = AdminTab.ADD_EDIT_MOVIE
                        },
                        onDeleteMovie = onDeleteMovie,
                        onAddNewMovie = {
                            editingMovie = null
                            activeTab = AdminTab.ADD_EDIT_MOVIE
                        },
                        onBackClick = {
                            activeTab = AdminTab.DASHBOARD
                        }
                    )
                }

                AdminTab.MANAGE_CATEGORIES -> {
                    CategoryManagementView(
                        categories = categories,
                        onSaveCategory = onSaveCategory,
                        onDeleteCategory = onDeleteCategory,
                        onBackClick = {
                            activeTab = AdminTab.DASHBOARD
                        }
                    )
                }

                AdminTab.MANAGE_BANNERS -> {
                    BannerManagementView(
                        banners = banners,
                        allMovies = allMovies,
                        onSaveBanner = onSaveBanner,
                        onDeleteBanner = onDeleteBanner,
                        onToggleActive = onToggleBannerActive,
                        onBackClick = {
                            activeTab = AdminTab.DASHBOARD
                        }
                    )
                }

                AdminTab.MANAGE_ANNOUNCEMENTS -> {
                    AnnouncementManagementView(
                        notifications = notifications,
                        onSendAnnouncement = onSendAnnouncement,
                        onDeleteNotification = onDeleteNotification,
                        onBackClick = {
                            activeTab = AdminTab.DASHBOARD
                        }
                    )
                }

                AdminTab.MANAGE_LIVE_TV -> {
                    LiveTvManagementView(
                        channels = liveChannels,
                        reports = liveChannelReports,
                        onAddNewChannel = {
                            editingChannel = null
                            activeTab = AdminTab.ADD_EDIT_LIVE_CHANNEL
                        },
                        onEditChannel = { ch ->
                            editingChannel = ch
                            activeTab = AdminTab.ADD_EDIT_LIVE_CHANNEL
                        },
                        onDeleteChannel = onDeleteLiveChannel,
                        onTogglePublish = onToggleLiveChannelPublish,
                        onToggleEnabled = onToggleLiveChannelEnabled,
                        onResolveReport = onResolveLiveChannelReport,
                        onBackClick = {
                            activeTab = AdminTab.DASHBOARD
                        }
                    )
                }

                AdminTab.ADD_EDIT_LIVE_CHANNEL -> {
                    AddEditLiveChannelView(
                        existingChannel = editingChannel,
                        onSaveChannel = { ch ->
                            onSaveLiveChannel(ch)
                            activeTab = AdminTab.MANAGE_LIVE_TV
                        },
                        onCancel = {
                            activeTab = AdminTab.MANAGE_LIVE_TV
                        }
                    )
                }

                AdminTab.ARCHITECTURE -> {
                    BackendArchitectureView(
                        onSyncWithCloud = onSyncWithCloud,
                        onResetCatalog = onResetCatalog,
                        onBackClick = {
                            activeTab = AdminTab.DASHBOARD
                        }
                    )
                }
            }
        }
    }
}
