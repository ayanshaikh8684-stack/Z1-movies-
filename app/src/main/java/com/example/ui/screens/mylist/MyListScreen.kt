package com.example.ui.screens.mylist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ContinueWatchingItem
import com.example.data.model.Movie
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GridMovieCard
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MyListScreen(
    allMovies: List<Movie>,
    watchlistMovieIds: Set<String>,
    favoriteMovieIds: Set<String>,
    continueWatching: List<ContinueWatchingItem>,
    onMovieClick: (Movie) -> Unit,
    onBookmarkToggle: (Movie) -> Unit,
    onClearHistory: () -> Unit = {},
    onBackClick: () -> Unit,
    onExploreMoviesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Watchlist (${watchlistMovieIds.size})", "Favorites (${favoriteMovieIds.size})", "History (${continueWatching.size})")

    val displayedMovies = when (selectedTabIndex) {
        0 -> allMovies.filter { watchlistMovieIds.contains(it.id) }
        1 -> allMovies.filter { favoriteMovieIds.contains(it.id) }
        else -> {
            val watchedIds = continueWatching.map { it.movieId }.toSet()
            allMovies.filter { watchedIds.contains(it.id) }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
            .statusBarsPadding()
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "My Personal Library",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Saved movies & bookmarks",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            if (selectedTabIndex == 2 && continueWatching.isNotEmpty()) {
                IconButton(onClick = onClearHistory) {
                    Icon(
                        imageVector = Icons.Filled.DeleteSweep,
                        contentDescription = "Clear History",
                        tint = com.example.ui.theme.LiveRed
                    )
                }
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = CinemaDarkBackground,
            contentColor = ElectricBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = ElectricBlue
                )
            },
            divider = {}
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedTabIndex == index) ElectricBlue else TextSecondary
                        )
                    }
                )
            }
        }

        // Grid Content or Empty State
        if (displayedMovies.isEmpty()) {
            val (icon, title, desc) = when (selectedTabIndex) {
                0 -> Triple(
                    Icons.Filled.BookmarkBorder,
                    "Your watchlist is empty",
                    "Save movies here and watch them later. Tap the bookmark button on any movie poster."
                )
                1 -> Triple(
                    Icons.Filled.Favorite,
                    "No favorites yet",
                    "Mark your most loved titles as favorites to find them easily here."
                )
                else -> Triple(
                    Icons.Filled.History,
                    "No watch history yet",
                    "Movies you start watching will automatically appear here with progress tracking."
                )
            }

            EmptyStateView(
                icon = icon,
                title = title,
                description = desc,
                actionButtonText = "Discover Movies",
                onActionClick = onExploreMoviesClick,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(displayedMovies, key = { it.id }) { movie ->
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
