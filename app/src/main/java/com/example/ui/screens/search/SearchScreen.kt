package com.example.ui.screens.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LiveChannel
import com.example.data.model.Movie
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GridMovieCard
import com.example.ui.screens.livetv.LiveChannelCard
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchScreen(
    searchQuery: String,
    searchResults: List<Movie>,
    searchChannelResults: List<LiveChannel> = emptyList(),
    recentSearches: List<String>,
    popularSearches: List<String>,
    watchlistMovieIds: Set<String>,
    favoriteChannelIds: Set<String> = emptySet(),
    onQueryChange: (String) -> Unit,
    onMovieClick: (Movie) -> Unit,
    onChannelClick: (LiveChannel) -> Unit = {},
    onBookmarkToggle: (Movie) -> Unit,
    onChannelFavoriteToggle: (LiveChannel) -> Unit = {},
    onClearHistory: () -> Unit,
    onCloseSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVoiceDialogActive by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
            .statusBarsPadding()
    ) {
        // Search Input Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onCloseSearch) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onQueryChange,
                placeholder = {
                    Text(
                        text = "Search movies, actors, directors, genres...",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = ElectricBlue
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = "Clear",
                                    tint = TextSecondary
                                )
                            }
                        }
                        IconButton(onClick = { isVoiceDialogActive = true }) {
                            Icon(
                                imageVector = Icons.Filled.Mic,
                                contentDescription = "Voice Search",
                                tint = ElectricBlue
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = Color(0xFF26324D),
                    focusedContainerColor = CinemaSurfaceElevated,
                    unfocusedContainerColor = CinemaSurfaceElevated,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
            )
        }

        // Search Content or Discovery Recommendations
        if (searchQuery.isBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Recent Searches
                if (recentSearches.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.History, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Recent Searches", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        }
                        Text(
                            text = "Clear All",
                            color = ElectricBlue,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.clickable { onClearHistory() }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        recentSearches.forEach { term ->
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = CinemaSurfaceElevated,
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26324D)),
                                modifier = Modifier.clickable { onQueryChange(term) }
                            ) {
                                Text(
                                    text = term,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                // Popular Searches
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Popular Searches", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    popularSearches.forEach { term ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = CinemaSurfaceElevated,
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.3f)),
                            modifier = Modifier.clickable { onQueryChange(term) }
                        ) {
                            Text(
                                text = term,
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        } else {
            // Search Results (Movies + Live Channels)
            if (searchResults.isEmpty() && searchChannelResults.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Filled.Search,
                    title = "No results found",
                    description = "Try another title, actor, director, genre, channel, or keyword to find movies & live feeds.",
                    actionButtonText = "Clear Search",
                    onActionClick = { onQueryChange("") },
                    modifier = Modifier.weight(1f)
                )
            } else {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "${searchResults.size + searchChannelResults.size} results found for \"$searchQuery\"",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    // Live Channels horizontal highlight if any matched
                    if (searchChannelResults.isNotEmpty()) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Filled.Tv, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Live TV Channels (${searchChannelResults.size})", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }

                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(searchChannelResults, key = { it.id }) { ch ->
                                    LiveChannelCard(
                                        channel = ch,
                                        isFavorite = favoriteChannelIds.contains(ch.id),
                                        onChannelClick = { onChannelClick(ch) },
                                        onFavoriteToggle = { onChannelFavoriteToggle(ch) },
                                        modifier = Modifier.width(200.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (searchResults.isNotEmpty()) {
                        if (searchChannelResults.isNotEmpty()) {
                            Text(
                                text = "Movies & Titles (${searchResults.size})",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 150.dp),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 96.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(searchResults, key = { it.id }) { movie ->
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
            }
        }
    }

    // Interactive Voice Search Dialog simulation
    if (isVoiceDialogActive) {
        AlertDialog(
            onDismissRequest = { isVoiceDialogActive = false },
            title = {
                Text("Listening...", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(ElectricBlue.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Mic,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Say a movie title, actor, or genre (e.g., 'Midnight Protocol' or 'Sci-Fi 4K')",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onQueryChange("Midnight Protocol")
                        isVoiceDialogActive = false
                    }
                ) {
                    Text("Search 'Midnight Protocol'", color = ElectricBlue, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isVoiceDialogActive = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CinemaSurfaceElevated
        )
    }
}
