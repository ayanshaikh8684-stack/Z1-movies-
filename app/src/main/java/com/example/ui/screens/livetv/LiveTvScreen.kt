package com.example.ui.screens.livetv

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LiveChannel
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NeonGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun LiveTvScreen(
    channels: List<LiveChannel>,
    favoriteChannelIds: Set<String>,
    onChannelClick: (LiveChannel) -> Unit,
    onFavoriteToggle: (LiveChannel) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Only display channels that are published & enabled
    val activeChannels = channels.filter { it.isPublished && it.isEnabled }

    // Dynamic categories: "All", "Favorites", plus categories configured by Admin that actually have channels
    val configuredCategories = activeChannels.map { it.category }.distinct().sorted()
    val allCategories = listOf("All", "Favorites", "Featured") + configuredCategories

    var selectedCategory by remember { mutableStateOf("All") }

    val filteredChannels = when (selectedCategory) {
        "All" -> activeChannels
        "Favorites" -> activeChannels.filter { favoriteChannelIds.contains(it.id) }
        "Featured" -> activeChannels.filter { it.isFeatured }
        else -> activeChannels.filter { it.category.equals(selectedCategory, ignoreCase = true) }
    }

    val liveNowChannels = activeChannels.take(5)
    val featuredChannels = activeChannels.filter { it.isFeatured }
    val favoriteChannels = activeChannels.filter { favoriteChannelIds.contains(it.id) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
    ) {
        // Live TV Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    color = ElectricBlue.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Tv,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.padding(6.dp).size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Live TV",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = LiveRed, shape = RoundedCornerShape(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Filled.FiberManualRecord, contentDescription = null, tint = Color.White, modifier = Modifier.size(6.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("STREAMING", color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                    Text(
                        text = "Authorized broadcast feeds & scheduled guides",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .size(38.dp)
                    .background(CinemaSurfaceElevated, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Search Channels",
                    tint = TextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Categories Scrollable Bar
        ScrollableTabRow(
            selectedTabIndex = allCategories.indexOf(selectedCategory).coerceAtLeast(0),
            containerColor = CinemaDarkBackground,
            contentColor = ElectricBlue,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                val index = allCategories.indexOf(selectedCategory).coerceAtLeast(0)
                if (index < tabPositions.size) {
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[index]),
                        color = ElectricBlue,
                        height = 3.dp
                    )
                }
            },
            divider = {}
        ) {
            allCategories.forEach { category ->
                val isSelected = selectedCategory == category
                Tab(
                    selected = isSelected,
                    onClick = { selectedCategory = category },
                    text = {
                        Text(
                            text = category,
                            color = if (isSelected) ElectricBlue else TextSecondary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Content Area
        if (selectedCategory == "All") {
            // Full Landing view: Live Now row, Featured row, All channels grid
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 96.dp)
            ) {
                // Section: Live Now Horizontal Highlights
                if (liveNowChannels.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(color = LiveRed, shape = CircleShape, modifier = Modifier.size(8.dp)) {}
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Live Now",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(liveNowChannels) { ch ->
                                LiveChannelCard(
                                    channel = ch,
                                    isFavorite = favoriteChannelIds.contains(ch.id),
                                    onChannelClick = { onChannelClick(ch) },
                                    onFavoriteToggle = { onFavoriteToggle(ch) },
                                    modifier = Modifier.width(220.dp)
                                )
                            }
                        }
                    }
                }

                // Section: Favorite Channels (if any)
                if (favoriteChannels.isNotEmpty()) {
                    item {
                        Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Bookmark, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Favorite Channels",
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(favoriteChannels) { ch ->
                                LiveChannelCard(
                                    channel = ch,
                                    isFavorite = true,
                                    onChannelClick = { onChannelClick(ch) },
                                    onFavoriteToggle = { onFavoriteToggle(ch) },
                                    modifier = Modifier.width(220.dp)
                                )
                            }
                        }
                    }
                }

                // Section: All Configured Channels
                item {
                    Column(modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)) {
                        Text(
                            text = "All TV Channels (${activeChannels.size})",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                items(activeChannels.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LiveChannelCard(
                            channel = pair[0],
                            isFavorite = favoriteChannelIds.contains(pair[0].id),
                            onChannelClick = { onChannelClick(pair[0]) },
                            onFavoriteToggle = { onFavoriteToggle(pair[0]) },
                            modifier = Modifier.weight(1f)
                        )
                        if (pair.size > 1) {
                            LiveChannelCard(
                                channel = pair[1],
                                isFavorite = favoriteChannelIds.contains(pair[1].id),
                                onChannelClick = { onChannelClick(pair[1]) },
                                onFavoriteToggle = { onFavoriteToggle(pair[1]) },
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        } else {
            // Category Filtered Grid
            if (filteredChannels.isEmpty()) {
                EmptyStateView(
                    icon = Icons.Filled.Tv,
                    title = "No channels in '$selectedCategory'",
                    description = if (selectedCategory == "Favorites") "You haven't bookmarked any favorite channels yet. Tap the bookmark icon on any channel card to save it here."
                    else "There are currently no active broadcast feeds configured for this category.",
                    actionButtonText = if (selectedCategory == "Favorites") "Browse Channels" else "View All Channels",
                    onActionClick = { selectedCategory = "All" },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 160.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredChannels, key = { it.id }) { ch ->
                        LiveChannelCard(
                            channel = ch,
                            isFavorite = favoriteChannelIds.contains(ch.id),
                            onChannelClick = { onChannelClick(ch) },
                            onFavoriteToggle = { onFavoriteToggle(ch) }
                        )
                    }
                }
            }
        }
    }
}
