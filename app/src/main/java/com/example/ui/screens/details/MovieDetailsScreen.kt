package com.example.ui.screens.details

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Movie
import com.example.ui.components.CinemaPoster
import com.example.ui.components.HorizontalMovieCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonGold
import com.example.ui.theme.RatingGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun MovieDetailsScreen(
    movie: Movie,
    allMovies: List<Movie>,
    watchlistMovieIds: Set<String>,
    isUserPremium: Boolean = true,
    onBackClick: () -> Unit,
    onWatchNowClick: (Movie) -> Unit,
    onAddToListClick: (Movie) -> Unit,
    onDownloadClick: (Movie) -> Unit,
    onMovieClick: (Movie) -> Unit,
    onShareClick: (Movie) -> Unit,
    onUpgradeToPremiumClick: () -> Unit = {},
    onReportIssue: (movieId: String, movieTitle: String, issueType: String, description: String) -> Unit = { _, _, _, _ -> },
    onNotifyMeToggle: (Movie) -> Unit = {},
    isNotified: Boolean = false,
    modifier: Modifier = Modifier
) {
    val inWatchlist = watchlistMovieIds.contains(movie.id)
    var isReportDialogOpen by remember { mutableStateOf(false) }
    var selectedReportType by remember { mutableStateOf("Broken video stream") }
    var reportDescription by remember { mutableStateOf("") }
    var isNotifyActive by remember { mutableStateOf(isNotified) }

    // Live countdown computation for Free Release
    val freeReleaseMs = movie.freeReleaseDate
    val isEarlyAccessActive = movie.isInPremiumEarlyAccess

    fun formatRemainingCountdown(targetMs: Long): String {
        val diff = targetMs - System.currentTimeMillis()
        if (diff <= 0) return "Available Now"
        val totalSec = diff / 1000L
        val days = totalSec / (24 * 3600)
        val hours = (totalSec % (24 * 3600)) / 3600
        val minutes = (totalSec % 3600) / 60
        return when {
            days > 0 -> "${days}d ${hours}h ${minutes}m"
            hours > 0 -> "${hours}h ${minutes}m"
            else -> "${minutes}m"
        }
    }

    val countdownText = if (freeReleaseMs != null) formatRemainingCountdown(freeReleaseMs) else ""

    val moreLikeThis = allMovies.filter { other ->
        other.id != movie.id && other.genres.any { movie.genres.contains(it) }
    }
    val recommended = allMovies.filter { other ->
        other.id != movie.id && !moreLikeThis.contains(other)
    }.shuffled().take(6)

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 96.dp)
        ) {
            // Full-Width Backdrop Header with deep gradient overlay
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(movie.backdropUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = movie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Cinematic Gradient (Top and bottom fading)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    0.0f to CinemaBlack.copy(alpha = 0.7f),
                                    0.4f to Color.Transparent,
                                    0.8f to CinemaDarkBackground.copy(alpha = 0.8f),
                                    1.0f to CinemaDarkBackground
                                )
                            )
                    )

                    // Floating Center Play Button
                    val canPlayCenter = !movie.isRightsExpired && !movie.isScheduledRelease
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .align(Alignment.Center)
                            .background(if (canPlayCenter) ElectricBlue else Color(0xFF334155), CircleShape)
                            .clickable(enabled = canPlayCenter) { onWatchNowClick(movie) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (movie.isRightsExpired) Icons.Filled.Security else Icons.Filled.PlayArrow,
                            contentDescription = "Watch Now",
                            tint = if (canPlayCenter) CinemaBlack else Color(0xFF64748B),
                            modifier = Modifier.size(if (movie.isRightsExpired) 24.dp else 32.dp)
                        )
                    }
                }
            }

            // Movie Main Info & Poster Block
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Poster Thumbnail
                        CinemaPoster(
                            movie = movie,
                            modifier = Modifier
                                .size(width = 110.dp, height = 160.dp),
                            showQualityBadge = false,
                            showRatingBadge = false
                        )

                        // Key Metadata Info
                        Column(modifier = Modifier.weight(1f)) {
                            // Quality & Age Rating Pills
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    color = ElectricBlue.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = movie.quality,
                                        color = ElectricBlue,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Surface(
                                    color = Color(0xFF334155),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = movie.ageRating,
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = movie.title,
                                color = TextPrimary,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.ExtraBold,
                                lineHeight = 26.sp
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Year, Duration, Rating
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(text = "${movie.year}", color = TextSecondary, fontSize = 12.sp)
                                Text(text = "•", color = TextTertiary, fontSize = 12.sp)
                                Text(text = movie.duration, color = TextSecondary, fontSize = 12.sp)
                                Text(text = "•", color = TextTertiary, fontSize = 12.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = RatingGold,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = String.format("%.1f", movie.rating),
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Genres
                            Text(
                                text = movie.genres.joinToString(", "),
                                color = ElectricBlue,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Content License Tag
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.VerifiedUser,
                                    contentDescription = null,
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = movie.licenseType,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            if (movie.digitalReleaseDate.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Digital Release: ${movie.digitalReleaseDate}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }

                            if (movie.isRightsExpired) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Rights Status: Streaming Window Expired",
                                    color = Color(0xFFEF4444),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else if (movie.isScheduledRelease) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Coming Soon: Scheduled Release",
                                    color = ElectricBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            } else if (isEarlyAccessActive) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Premium Early Access",
                                    color = NeonGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Premium Early Access Notice & Countdown Banner
                    if (isEarlyAccessActive) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = NeonGold.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NeonGold.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Star,
                                            contentDescription = null,
                                            tint = NeonGold,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Premium Early Access",
                                            color = NeonGold,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    if (countdownText.isNotBlank()) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = CinemaBlack.copy(alpha = 0.6f)
                                        ) {
                                            Text(
                                                text = "Free in $countdownText",
                                                color = ElectricBlue,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                val freeDateFormatted = movie.freeReleaseDate?.let {
                                    java.text.SimpleDateFormat("MMM dd, yyyy 'at' hh:mm a", java.util.Locale.US).format(java.util.Date(it))
                                } ?: "a scheduled date"

                                Text(
                                    text = if (isUserPremium) {
                                        "You have active Z1 Cinema Pro access! Enjoy this exclusive release before everyone else."
                                    } else {
                                        "Exclusive for Premium subscribers. Available for everyone on $freeDateFormatted."
                                    },
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Primary Action Buttons: WATCH NOW & ADD TO MY LIST
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val isWatchDisabled = movie.isRightsExpired || movie.isScheduledRelease

                        if (isEarlyAccessActive && !isUserPremium) {
                            // Non-premium user: Watch with Premium button (opens subscription / upgrade)
                            Button(
                                onClick = onUpgradeToPremiumClick,
                                enabled = !isWatchDisabled,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = NeonGold,
                                    contentColor = CinemaBlack,
                                    disabledContainerColor = Color(0xFF334155),
                                    disabledContentColor = Color(0xFF64748B)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(46.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Star,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Watch with Premium",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        } else {
                            // Premium user or standard free release
                            Button(
                                onClick = { onWatchNowClick(movie) },
                                enabled = !isWatchDisabled,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isEarlyAccessActive) NeonGold else ElectricBlue,
                                    contentColor = CinemaBlack,
                                    disabledContainerColor = Color(0xFF334155),
                                    disabledContentColor = Color(0xFF64748B)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1.3f)
                                    .height(46.dp)
                            ) {
                                Icon(
                                    imageVector = if (movie.isRightsExpired) Icons.Filled.Security else Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = when {
                                        movie.isRightsExpired -> "Expired"
                                        movie.isScheduledRelease -> "Coming Soon"
                                        isEarlyAccessActive -> "Watch Now — Premium Early Access"
                                        else -> "Watch Now"
                                    },
                                    fontWeight = FontWeight.Bold,
                                    fontSize = if (isEarlyAccessActive) 12.sp else 14.sp
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { onAddToListClick(movie) },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (inWatchlist) ElectricBlue else Color(0xFF334155)
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (inWatchlist) ElectricBlue else TextPrimary
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        ) {
                            Icon(
                                imageVector = if (inWatchlist) Icons.Filled.Check else Icons.Filled.BookmarkBorder,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (inWatchlist) "In List" else "My List",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Download Button
                        IconButton(
                            onClick = { onDownloadClick(movie) },
                            modifier = Modifier
                                .size(46.dp)
                                .background(CinemaSurfaceElevated, RoundedCornerShape(10.dp))
                                .border(1.dp, Color(0xFF26324D), RoundedCornerShape(10.dp))
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = "Download Movie",
                                tint = ElectricBlue,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    if (movie.isScheduledRelease) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                isNotifyActive = !isNotifyActive
                                onNotifyMeToggle(movie)
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isNotifyActive) Color(0xFF10B981) else ElectricBlue,
                                contentColor = CinemaBlack
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(
                                imageVector = if (isNotifyActive) Icons.Filled.NotificationsActive else Icons.Filled.NotificationsNone,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isNotifyActive) "Notified! We will alert you upon release" else "Notify Me When Available",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (movie.trailerUrl.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = {
                                val trailerMovie = movie.copy(videoUrl = movie.trailerUrl)
                                onWatchNowClick(trailerMovie)
                            },
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = ElectricBlue
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayCircleOutline,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Watch Official Trailer",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Short Synopsis
                    Text(
                        text = "Synopsis",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = movie.description,
                        color = TextSecondary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Technical Specs & Metadata Table
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = CinemaSurfaceElevated,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26324D)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            SpecRow("Director", movie.director)
                            SpecDivider()
                            SpecRow("Audio Tracks", movie.audioTracks.joinToString(", "))
                            SpecDivider()
                            SpecRow("Subtitles", movie.subtitles.joinToString(", "))
                            SpecDivider()
                            SpecRow("Video Quality", movie.quality)
                            SpecDivider()
                            SpecRow("Release Year", "${movie.year}")
                            if (movie.digitalReleaseDate.isNotBlank()) {
                                SpecDivider()
                                SpecRow("Digital Release", movie.digitalReleaseDate)
                            }
                            if (movie.rightsStartDate != null || movie.rightsExpiryDate != null) {
                                SpecDivider()
                                val startStr = movie.rightsStartDate?.let {
                                    java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(it))
                                }
                                val endStr = movie.rightsExpiryDate?.let {
                                    java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date(it))
                                }
                                val rightsWindow = when {
                                    startStr != null && endStr != null -> "$startStr to $endStr"
                                    endStr != null -> "Expires $endStr"
                                    else -> "From $startStr"
                                }
                                SpecRow("Rights Window", rightsWindow)
                            }
                            SpecDivider()
                            SpecRow("License Status", "${movie.licenseType} (Authorized)")
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Cast Section
                    Text(
                        text = "Top Cast",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Cast Carousel
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(movie.cast) { actorName ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.width(80.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(62.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(Color(0xFF1E293B), Color(0xFF334155))
                                        )
                                    )
                                    .border(1.5.dp, ElectricBlue.copy(alpha = 0.6f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = actorName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString(""),
                                    color = ElectricBlue,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = actorName,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            // AdMob-Ready Native Banner placement (hidden for VIP subscribers)
            item {
                Spacer(modifier = Modifier.height(14.dp))
                com.example.ui.components.AdMobBannerView(
                    isPremiumUser = isUserPremium,
                    onUpgradeClick = onUpgradeToPremiumClick
                )
            }

            // More Like This Section
            if (moreLikeThis.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    SectionHeader(
                        title = "More Like This",
                        subtitle = "Titles with similar genres"
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(moreLikeThis, key = { it.id }) { item ->
                            HorizontalMovieCard(
                                movie = item,
                                onClick = { onMovieClick(item) },
                                isBookmarked = watchlistMovieIds.contains(item.id),
                                onBookmarkToggle = { onAddToListClick(item) }
                            )
                        }
                    }
                }
            }

            // Recommended Movies Section
            if (recommended.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    SectionHeader(
                        title = "Recommended",
                        subtitle = "You may also like"
                    )
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(recommended, key = { it.id }) { item ->
                            HorizontalMovieCard(
                                movie = item,
                                onClick = { onMovieClick(item) },
                                isBookmarked = watchlistMovieIds.contains(item.id),
                                onBookmarkToggle = { onAddToListClick(item) }
                            )
                        }
                    }
                }
            }
        }

        // Floating Top Bar with Back, Share, and Bookmark actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(CinemaBlack.copy(alpha = 0.65f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = { isReportDialogOpen = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(CinemaBlack.copy(alpha = 0.65f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Flag,
                        contentDescription = "Report Issue",
                        tint = TextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = { onShareClick(movie) },
                    modifier = Modifier
                        .size(40.dp)
                        .background(CinemaBlack.copy(alpha = 0.65f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Share,
                        contentDescription = "Share",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = { onAddToListClick(movie) },
                    modifier = Modifier
                        .size(40.dp)
                        .background(CinemaBlack.copy(alpha = 0.65f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (inWatchlist) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (inWatchlist) ElectricBlue else TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Report Issue Modal Dialog
        if (isReportDialogOpen) {
            AlertDialog(
                onDismissRequest = { isReportDialogOpen = false },
                title = {
                    Text(
                        text = "Report Issue",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Reporting: ${movie.title}",
                            color = ElectricBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "What seems to be the problem?",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        val reportTypes = listOf(
                            "Broken video stream",
                            "Incorrect metadata / details",
                            "Subtitle sync or missing audio",
                            "Licensing / Copyright query",
                            "Other playback error"
                        )
                        reportTypes.forEach { type ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedReportType = type }
                                    .padding(vertical = 3.dp)
                            ) {
                                RadioButton(
                                    selected = selectedReportType == type,
                                    onClick = { selectedReportType = type },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = ElectricBlue,
                                        unselectedColor = TextTertiary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = type,
                                    color = TextPrimary,
                                    fontSize = 13.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = reportDescription,
                            onValueChange = { reportDescription = it },
                            placeholder = { Text("Additional details (optional)...", color = TextTertiary, fontSize = 12.sp) },
                            maxLines = 3,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = Color(0xFF334155),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onReportIssue(movie.id, movie.title, selectedReportType, reportDescription)
                            isReportDialogOpen = false
                            reportDescription = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = CinemaBlack)
                    ) {
                        Text("Submit Report", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { isReportDialogOpen = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                },
                containerColor = CinemaSurfaceElevated
            )
        }
    }
}

@Composable
fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextTertiary, fontSize = 12.sp)
        Text(text = value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun SpecDivider() {
    HorizontalDivider(
        color = Color(0xFF26324D).copy(alpha = 0.5f),
        thickness = 0.5.dp,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}
