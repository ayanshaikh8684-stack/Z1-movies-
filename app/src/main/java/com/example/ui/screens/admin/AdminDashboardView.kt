package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.Tv
import com.example.data.firestore.FirestoreSyncStatus
import com.example.data.model.BannerItem
import com.example.data.model.Movie
import com.example.data.model.MovieCategory
import com.example.data.remote.AdminSession
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun AdminDashboardView(
    session: AdminSession,
    allMovies: List<Movie>,
    categories: List<MovieCategory> = emptyList(),
    banners: List<BannerItem> = emptyList(),
    firestoreSyncStatus: FirestoreSyncStatus? = null,
    onAddNewMovieClick: () -> Unit,
    onManageCatalogClick: () -> Unit,
    onManageCategoriesClick: () -> Unit,
    onManageBannersClick: () -> Unit,
    onManageAnnouncementsClick: () -> Unit = {},
    onManageLiveTvClick: () -> Unit = {},
    onMigrateToFirestoreClick: () -> Unit,
    onBackendArchitectureClick: () -> Unit,
    onEditMovieClick: (Movie) -> Unit,
    onLogoutClick: () -> Unit,
    onBackToAppClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val publishedCount = allMovies.count { it.isPublished }
    val draftCount = allMovies.count { !it.isPublished }
    val scheduledCount = allMovies.count { it.isScheduledRelease }
    val expiredCount = allMovies.count { it.isRightsExpired }
    val earlyAccessCount = allMovies.count { it.isInPremiumEarlyAccess }
    val autoFreeUpcomingCount = allMovies.count { it.isPremiumEarlyAccess && it.freeReleaseDate != null && it.freeReleaseDate > System.currentTimeMillis() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Bar: Back, Title, Role Badge, Logout
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackToAppClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to App",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Admin Dashboard",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Signed in as ${session.user.name}",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Role Badge
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = when (session.user.role.name) {
                        "SUPER_ADMIN" -> NeonGold.copy(alpha = 0.15f)
                        "ADMIN" -> ElectricBlue.copy(alpha = 0.15f)
                        else -> Color(0xFF10B981).copy(alpha = 0.15f)
                    },
                    border = BorderStroke(
                        1.dp,
                        when (session.user.role.name) {
                            "SUPER_ADMIN" -> NeonGold
                            "ADMIN" -> ElectricBlue
                            else -> Color(0xFF10B981)
                        }
                    )
                ) {
                    Text(
                        text = session.user.role.name,
                        color = when (session.user.role.name) {
                            "SUPER_ADMIN" -> NeonGold
                            "ADMIN" -> ElectricBlue
                            else -> Color(0xFF10B981)
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = onLogoutClick) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Logout",
                        tint = LiveRed
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Firestore Production Status Banner
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(
                1.dp,
                if (firestoreSyncStatus?.isConnected == true) NeonGreen.copy(alpha = 0.4f) else Color(0xFF1E293B)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (firestoreSyncStatus?.isConnected == true) NeonGreen else NeonGold)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (firestoreSyncStatus?.isConnected == true) "Firestore Production: CONNECTED" else "Firestore: LOCAL FALLBACK ACTIVE",
                            color = if (firestoreSyncStatus?.isConnected == true) NeonGreen else NeonGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF1E293B),
                        border = BorderStroke(1.dp, Color(0xFF334155))
                    ) {
                        Text(
                            text = "Cloud DB",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = firestoreSyncStatus?.statusMessage ?: "Connected to dynamic production database.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onMigrateToFirestoreClick,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B))
                ) {
                    Icon(
                        imageVector = Icons.Filled.CloudSync,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Sync / Migrate Room Catalog to Firestore",
                        color = ElectricBlue,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Metrics Grid (2x2)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Total Movies",
                value = "${allMovies.size}",
                subtitle = "Active in Catalog",
                icon = Icons.Filled.Movie,
                accentColor = ElectricBlue,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Published Live",
                value = "$publishedCount",
                subtitle = "Active in User App",
                icon = Icons.Filled.Visibility,
                accentColor = NeonGreen,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Categories",
                value = "${categories.size}",
                subtitle = "Taxonomy Groups",
                icon = Icons.Filled.Category,
                accentColor = NeonGold,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Hero Banners",
                value = "${banners.size}",
                subtitle = "${banners.count { it.isActive }} Active on Home",
                icon = Icons.Filled.ViewCarousel,
                accentColor = Color(0xFFA855F7),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Scheduled Releases",
                value = "$scheduledCount",
                subtitle = "Pending Publication Date",
                icon = Icons.Filled.Refresh,
                accentColor = ElectricBlue,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Rights Expired",
                value = "$expiredCount",
                subtitle = if (expiredCount > 0) "Requires License Renewal" else "All Licenses Valid",
                icon = Icons.Filled.Shield,
                accentColor = if (expiredCount > 0) LiveRed else Color(0xFF10B981),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricCard(
                title = "Early Access Titles",
                value = "$earlyAccessCount",
                subtitle = "Active Premium Release Window",
                icon = Icons.Filled.Star,
                accentColor = NeonGold,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Upcoming Free",
                value = "$autoFreeUpcomingCount",
                subtitle = "Scheduled Auto-Unlock",
                icon = Icons.Filled.LockOpen,
                accentColor = Color(0xFF38BDF8),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Fast Action Buttons
        Text(
            text = "Catalog & Content Operations",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Button(
            onClick = onAddNewMovieClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "+ Add New Movie",
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onManageCatalogClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Filled.VideoLibrary,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Movies (${allMovies.size})",
                    color = ElectricBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedButton(
                onClick = onManageCategoriesClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, NeonGold.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Filled.Category,
                    contentDescription = null,
                    tint = NeonGold,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Categories (${categories.size})",
                    color = NeonGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(
                onClick = onManageBannersClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFA855F7).copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Filled.ViewCarousel,
                    contentDescription = null,
                    tint = Color(0xFFA855F7),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Hero Banners (${banners.size})",
                    color = Color(0xFFA855F7),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedButton(
                onClick = onBackendArchitectureClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFF334155))
            ) {
                Icon(
                    imageVector = Icons.Filled.Storage,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Architecture",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onManageLiveTvClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.6f))
        ) {
            Icon(
                imageVector = Icons.Filled.Tv,
                contentDescription = null,
                tint = ElectricBlue,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Manage Live TV Channels & Streams",
                color = ElectricBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onManageAnnouncementsClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, NeonGreen.copy(alpha = 0.5f))
        ) {
            Icon(
                imageVector = Icons.Filled.Campaign,
                contentDescription = null,
                tint = NeonGreen,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Manage Announcements & Alerts",
                color = NeonGreen,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Titles Preview Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recently Added / Managed Titles",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "See All",
                color = ElectricBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onManageCatalogClick() }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        allMovies.take(4).forEach { movie ->
            RecentMovieCard(
                movie = movie,
                onEditClick = { onEditMovieClick(movie) }
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onBackToAppClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Icon(Icons.Filled.Visibility, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Switch to User Streaming App", color = TextSecondary, fontSize = 13.sp)
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CinemaSurfaceElevated,
        border = BorderStroke(1.dp, Color(0xFF26324D)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextSecondary, fontSize = 12.sp)
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                color = TextTertiary,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun RecentMovieCard(
    movie: Movie,
    onEditClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = CinemaSurfaceElevated,
        border = BorderStroke(1.dp, Color(0xFF1E293B)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEditClick() }
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(width = 44.dp, height = 62.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF1E293B))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = movie.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // Live or Draft chip
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when {
                            movie.isRightsExpired -> LiveRed.copy(alpha = 0.15f)
                            movie.isScheduledRelease -> ElectricBlue.copy(alpha = 0.15f)
                            movie.isInPremiumEarlyAccess -> NeonGold.copy(alpha = 0.15f)
                            movie.isPublished -> NeonGreen.copy(alpha = 0.15f)
                            else -> Color(0xFF64748B).copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = when {
                                movie.isRightsExpired -> "EXPIRED"
                                movie.isScheduledRelease -> "SCHEDULED"
                                movie.isInPremiumEarlyAccess -> "PREMIUM ACCESS"
                                movie.isPublished -> "LIVE"
                                else -> "DRAFT"
                            },
                            color = when {
                                movie.isRightsExpired -> LiveRed
                                movie.isScheduledRelease -> ElectricBlue
                                movie.isInPremiumEarlyAccess -> NeonGold
                                movie.isPublished -> NeonGreen
                                else -> Color(0xFF94A3B8)
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${movie.year} · ${movie.quality} · ${movie.genres.take(2).joinToString(", ")}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            OutlinedButton(
                onClick = onEditClick,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFF334155)),
                modifier = Modifier.height(34.dp)
            ) {
                Text(text = "Edit", color = ElectricBlue, fontSize = 11.sp)
            }
        }
    }
}
