package com.example.ui.screens.categories

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Movie
import com.example.data.model.MovieCategory
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GridMovieCard
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CategoriesScreen(
    categories: List<MovieCategory>,
    allMovies: List<Movie>,
    watchlistMovieIds: Set<String>,
    selectedCategory: MovieCategory?,
    onCategoryClick: (MovieCategory) -> Unit,
    onBackFromCategory: () -> Unit,
    onMovieClick: (Movie) -> Unit,
    onBookmarkToggle: (Movie) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedCategory != null) {
        // Dedicated Category Content Listing View
        val categoryMovies = allMovies.filter { movie ->
            movie.genres.any { it.equals(selectedCategory.name, ignoreCase = true) }
        }

        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Category Header with Back Button and Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(selectedCategory.gradientStart),
                                Color(selectedCategory.gradientEnd),
                                CinemaDarkBackground
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.TopStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onBackFromCategory,
                        modifier = Modifier
                            .size(38.dp)
                            .background(CinemaBlack.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = selectedCategory.name,
                            color = TextPrimary,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${categoryMovies.size} Movies • ${selectedCategory.description}",
                            color = ElectricBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Category Movie Grid
            if (categoryMovies.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Filled.Movie,
                    title = "More ${selectedCategory.name} coming soon",
                    description = "We are continuously expanding our licensed ${selectedCategory.name} streaming library.",
                    actionButtonText = "Back to Categories",
                    onActionClick = onBackFromCategory,
                    modifier = Modifier.weight(1f)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 150.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 96.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(categoryMovies, key = { it.id }) { movie ->
                        GridMovieCard(
                            movie = movie,
                            onClick = { onMovieClick(movie) },
                            isBookmarked = watchlistMovieIds.contains(movie.id),
                            onBookmarkToggle = { onBookmarkToggle(movie) }
                        )
                    }
                }
            }
        }
    } else {
        // Main Categories Grid Screen
        Column(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Movie Categories",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Select any genre or theme to discover curated titles",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 155.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categories, key = { it.id }) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onCategoryClick(category) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: MovieCategory,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(105.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26324D).copy(alpha = 0.8f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(category.gradientStart),
                            Color(category.gradientEnd)
                        )
                    )
                )
                .padding(14.dp)
        ) {
            // Icon Watermark
            Icon(
                imageVector = when (category.name) {
                    "Action" -> Icons.Filled.LocalFireDepartment
                    "Sci-Fi" -> Icons.Filled.Explore
                    "Islamic" -> Icons.Filled.Star
                    "Adventure" -> Icons.Filled.Explore
                    else -> Icons.Filled.PlayCircle
                },
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.18f),
                modifier = Modifier
                    .size(64.dp)
                    .align(Alignment.BottomEnd)
            )

            // Category Details
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = category.name,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "${category.movieCount} Titles",
                    color = ElectricBlue,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.description,
                    color = TextSecondary.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}
