package com.example.ui.components

import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Movie
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.RatingGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun HeroBanner(
    featuredMovies: List<Movie>,
    watchlistMovieIds: Set<String>,
    banners: List<com.example.data.model.BannerItem> = emptyList(),
    onWatchNowClick: (Movie) -> Unit,
    onAddToListClick: (Movie) -> Unit,
    onMovieClick: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    // If dynamic custom hero banners exist, adapt them; otherwise use featured movies
    val activeBanners = banners.filter { it.isActive }
    val displayMovies = if (activeBanners.isNotEmpty()) {
        activeBanners.mapNotNull { banner ->
            val linkedMovie = banner.movieId?.let { mid -> featuredMovies.find { it.id == mid } }
            linkedMovie ?: Movie(
                id = banner.movieId ?: banner.id,
                title = banner.title,
                tagline = banner.subtitle.ifEmpty { "Featured Cinema Event" },
                description = banner.subtitle.ifEmpty { "Special featured premiere exclusively on Z1 Movies." },
                posterUrl = banner.imageUrl,
                backdropUrl = banner.imageUrl,
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                genres = listOf("Featured", "Cinema"),
                quality = "4K UHD",
                rating = 4.9f,
                year = 2025,
                duration = "Special",
                language = "English",
                director = "Featured Studio",
                cast = listOf("Featured Cast")
            )
        }
    } else {
        featuredMovies
    }

    if (displayMovies.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { displayMovies.size })

    // Auto-changing featured carousel
    LaunchedEffect(pagerState, displayMovies.size) {
        while (true) {
            delay(5500)
            if (!pagerState.isScrollInProgress && displayMovies.isNotEmpty()) {
                val nextPage = (pagerState.currentPage + 1) % displayMovies.size
                pagerState.animateScrollToPage(nextPage, animationSpec = tween(700))
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(440.dp)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val movie = displayMovies[page]
            val inWatchlist = watchlistMovieIds.contains(movie.id)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { onMovieClick(movie) }
            ) {
                // Backdrop Image with rich fallback
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(movie.backdropUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Cinematic Multi-Stop Gradient Overlays
                // 1. Top subtle shadow for status bar / app bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    CinemaBlack.copy(alpha = 0.8f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // 2. Bottom deep gradient transitioning smoothly into the screen content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0.0f to Color.Transparent,
                                0.45f to CinemaDarkBackground.copy(alpha = 0.4f),
                                0.75f to CinemaDarkBackground.copy(alpha = 0.85f),
                                1.0f to CinemaDarkBackground
                            )
                        )
                )

                // Content Information Layer
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    // Category / Quality Pill + Year + Rating
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = ElectricBlue.copy(alpha = 0.2f),
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

                        Text(
                            text = "${movie.year}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = "•",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Text(
                            text = movie.duration,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Text(
                            text = "•",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Rating",
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

                    // Movie Title
                    Text(
                        text = movie.title,
                        color = TextPrimary,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 30.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Genres pills
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        movie.genres.take(3).forEach { genre ->
                            Text(
                                text = genre,
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            if (genre != movie.genres.take(3).last()) {
                                Text(
                                    text = "•",
                                    color = TextSecondary.copy(alpha = 0.5f),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // Short Description
                    Text(
                        text = movie.description,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = { onWatchNowClick(movie) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricBlue,
                                contentColor = CinemaBlack
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(44.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = "Play",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Watch Now",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { onAddToListClick(movie) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (inWatchlist) ElectricBlue else TextPrimary
                            ),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (inWatchlist) ElectricBlue else Color(0xFF334155)
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Icon(
                                imageVector = if (inWatchlist) Icons.Filled.Check else Icons.Filled.Add,
                                contentDescription = "My List",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (inWatchlist) "In My List" else "My List",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        // Pager Indicators
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(displayMovies.size) { iteration ->
                val isSelected = pagerState.currentPage == iteration
                Box(
                    modifier = Modifier
                        .height(3.5.dp)
                        .width(if (isSelected) 18.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) ElectricBlue else Color.White.copy(alpha = 0.35f))
                )
            }
        }
    }
}
