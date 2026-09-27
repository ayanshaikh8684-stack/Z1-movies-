package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Movie
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.RatingGold
import com.example.ui.theme.TextPrimary

@Composable
fun CinemaPoster(
    movie: Movie,
    modifier: Modifier = Modifier,
    isBookmarked: Boolean = false,
    onBookmarkToggle: (() -> Unit)? = null,
    showQualityBadge: Boolean = true,
    showRatingBadge: Boolean = true,
    showPlayOverlay: Boolean = false,
    rankingNumber: Int? = null
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(movie.colorGradientStart),
                        Color(movie.colorGradientEnd)
                    )
                )
            )
    ) {
        // Coil Image with Fallback gradient container
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.posterUrl)
                .crossfade(true)
                .build(),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay at bottom for title/badge readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            CinemaBlack.copy(alpha = 0.2f),
                            CinemaBlack.copy(alpha = 0.85f)
                        ),
                        startY = 0.4f
                    )
                )
        )

        // Top Badges: Quality, Premium Early Access & Bookmark
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.TopStart),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(4.dp)
            ) {
                if (showQualityBadge) {
                    Surface(
                        color = CinemaBlack.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = if (movie.quality.contains("4K")) "4K" else "HD",
                            color = ElectricBlue,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                if (movie.isInPremiumEarlyAccess) {
                    Surface(
                        color = Color(0xFFFFB800),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "PREMIUM",
                            color = CinemaBlack,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (onBookmarkToggle != null) {
                IconButton(
                    onClick = onBookmarkToggle,
                    modifier = Modifier
                        .size(28.dp)
                        .align(Alignment.TopEnd)
                        .background(CinemaBlack.copy(alpha = 0.65f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (isBookmarked) ElectricBlue else Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Play icon overlay if enabled
        if (showPlayOverlay) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .align(Alignment.Center)
                    .background(CinemaBlack.copy(alpha = 0.65f), CircleShape)
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = "Play",
                    tint = ElectricBlue,
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        // Bottom section: Rating & Ranking Number
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(6.dp)
        ) {
            if (showRatingBadge) {
                Surface(
                    color = CinemaBlack.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Rating",
                            tint = RatingGold,
                            modifier = Modifier.size(11.dp)
                        )
                        Text(
                            text = String.format("%.1f", movie.rating),
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }
        }

        // Subtle ranking indicator for Trending cards
        if (rankingNumber != null) {
            Text(
                text = "#$rankingNumber",
                color = ElectricBlue.copy(alpha = 0.9f),
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.End,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(6.dp)
            )
        }
    }
}
