package com.example.ui.screens.movies

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Movie
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GridMovieCard
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoviesScreen(
    movies: List<Movie>,
    watchlistMovieIds: Set<String>,
    onMovieClick: (Movie) -> Unit,
    onBookmarkToggle: (Movie) -> Unit,
    selectedGenre: String,
    onGenreSelected: (String) -> Unit,
    selectedLanguage: String,
    onLanguageSelected: (String) -> Unit,
    selectedQuality: String,
    onQualitySelected: (String) -> Unit,
    selectedSort: String,
    onSortSelected: (String) -> Unit,
    onSearchFieldClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFilterSheetOpen by remember { mutableStateOf(false) }
    var isSortSheetOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    val genreList = listOf("All", "Action", "Sci-Fi", "Drama", "Thriller", "Adventure", "Crime", "Indian", "Islamic", "Animation", "Sports", "Horror", "Comedy")
    val sortOptions = listOf("Popular", "Newest", "Highest Rated", "A-Z")
    val qualityOptions = listOf("All", "4K Ultra HD", "1080p FHD")
    val languageOptions = listOf("All", "English", "Hindi", "Arabic")

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Header & Quick Search Bar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Explore Movies",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Stream licensed cinematic titles in 4K HDR",
                color = TextSecondary,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar & Filter / Sort Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Tappable Search Field
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CinemaSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26324D)),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .clickable { onSearchFieldClick() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search",
                            tint = ElectricBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Search movies, cast, genres...",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                }

                // Filter Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (selectedGenre != "All" || selectedQuality != "All" || selectedLanguage != "All") ElectricBlue else CinemaSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26324D)),
                    modifier = Modifier
                        .size(46.dp)
                        .clickable { isFilterSheetOpen = true }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Filled.FilterList,
                            contentDescription = "Filters",
                            tint = if (selectedGenre != "All" || selectedQuality != "All" || selectedLanguage != "All") CinemaDarkBackground else TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Sort Button
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = CinemaSurfaceElevated,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26324D)),
                    modifier = Modifier
                        .size(46.dp)
                        .clickable { isSortSheetOpen = true }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Sort,
                            contentDescription = "Sort",
                            tint = TextPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Horizontal Quick Genre Chips Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            genreList.forEach { genre ->
                val isSelected = selectedGenre.equals(genre, ignoreCase = true)
                FilterChip(
                    selected = isSelected,
                    onClick = { onGenreSelected(genre) },
                    label = {
                        Text(
                            text = genre,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricBlue,
                        selectedLabelColor = CinemaDarkBackground,
                        containerColor = CinemaSurfaceElevated,
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Color(0xFF26324D),
                        selectedBorderColor = ElectricBlue,
                        enabled = true,
                        selected = isSelected
                    )
                )
            }
        }

        // Result count & active sort label
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${movies.size} Movies available",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = "Sorted by: $selectedSort",
                color = ElectricBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { isSortSheetOpen = true }
            )
        }

        // Movie Responsive Grid
        if (movies.isEmpty()) {
            EmptyStateView(
                icon = Icons.Filled.Movie,
                title = "No movies match filters",
                description = "Try selecting different genres, qualities, or clear filters to see more titles.",
                actionButtonText = "Reset Filters",
                onActionClick = {
                    onGenreSelected("All")
                    onLanguageSelected("All")
                    onQualitySelected("All")
                },
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(movies, key = { it.id }) { movie ->
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

    // Filter Bottom Sheet
    if (isFilterSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isFilterSheetOpen = false },
            sheetState = sheetState,
            containerColor = CinemaSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filter Movies",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { isFilterSheetOpen = false }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quality Filter
                Text("Video Quality", color = ElectricBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    qualityOptions.forEach { qual ->
                        FilterChip(
                            selected = selectedQuality == qual,
                            onClick = { onQualitySelected(qual) },
                            label = { Text(qual) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Language Filter
                Text("Audio Language", color = ElectricBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    languageOptions.forEach { lang ->
                        FilterChip(
                            selected = selectedLanguage == lang,
                            onClick = { onLanguageSelected(lang) },
                            label = { Text(lang) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Sort Bottom Sheet
    if (isSortSheetOpen) {
        ModalBottomSheet(
            onDismissRequest = { isSortSheetOpen = false },
            sheetState = sheetState,
            containerColor = CinemaSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Sort Movies By",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(14.dp))
                sortOptions.forEach { sort ->
                    val isSelected = selectedSort == sort
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSortSelected(sort)
                                isSortSheetOpen = false
                            }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = sort,
                            color = if (isSelected) ElectricBlue else TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (isSelected) {
                            Surface(
                                modifier = Modifier.size(8.dp),
                                shape = CircleShape,
                                color = ElectricBlue
                            ) {}
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
