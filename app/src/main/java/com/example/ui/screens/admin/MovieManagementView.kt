package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Movie
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

enum class CatalogFilter {
    ALL,
    PUBLISHED,
    SCHEDULED,
    EXPIRED,
    DRAFTS
}

@Composable
fun MovieManagementView(
    allMovies: List<Movie>,
    onTogglePublish: (String, Boolean) -> Unit,
    onEditMovie: (Movie) -> Unit,
    onDeleteMovie: (String) -> Unit,
    onAddNewMovie: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(CatalogFilter.ALL) }
    var movieToDelete by remember { mutableStateOf<Movie?>(null) }

    val filteredMovies = allMovies.filter { movie ->
        val matchesQuery = searchQuery.isBlank() ||
                movie.title.contains(searchQuery, ignoreCase = true) ||
                movie.genres.any { it.contains(searchQuery, ignoreCase = true) } ||
                movie.director.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            CatalogFilter.ALL -> true
            CatalogFilter.PUBLISHED -> movie.isPublished
            CatalogFilter.SCHEDULED -> movie.isScheduledRelease
            CatalogFilter.EXPIRED -> movie.isRightsExpired
            CatalogFilter.DRAFTS -> !movie.isPublished
        }

        matchesQuery && matchesFilter
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
            .statusBarsPadding()
    ) {
        // Top Header
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
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = "Manage Movie Catalog",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${allMovies.size} total titles in Room database",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Button(
                onClick = onAddNewMovie,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                modifier = Modifier.height(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("+ New", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Search Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search catalog by title, genre, director...", color = TextTertiary, fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Filled.Search, contentDescription = null, tint = ElectricBlue)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = ElectricBlue,
                unfocusedBorderColor = Color(0xFF334155),
                focusedContainerColor = CinemaSurface,
                unfocusedContainerColor = CinemaSurface
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            singleLine = true
        )

        // Filter Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedFilter == CatalogFilter.ALL,
                onClick = { selectedFilter = CatalogFilter.ALL },
                label = { Text("All (${allMovies.size})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ElectricBlue.copy(alpha = 0.2f),
                    selectedLabelColor = ElectricBlue
                ),
                border = BorderStroke(1.dp, if (selectedFilter == CatalogFilter.ALL) ElectricBlue else Color(0xFF334155))
            )
            FilterChip(
                selected = selectedFilter == CatalogFilter.PUBLISHED,
                onClick = { selectedFilter = CatalogFilter.PUBLISHED },
                label = { Text("Live (${allMovies.count { it.isPublished }})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonGreen.copy(alpha = 0.2f),
                    selectedLabelColor = NeonGreen
                ),
                border = BorderStroke(1.dp, if (selectedFilter == CatalogFilter.PUBLISHED) NeonGreen else Color(0xFF334155))
            )
            FilterChip(
                selected = selectedFilter == CatalogFilter.SCHEDULED,
                onClick = { selectedFilter = CatalogFilter.SCHEDULED },
                label = { Text("Scheduled (${allMovies.count { it.isScheduledRelease }})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = ElectricBlue.copy(alpha = 0.2f),
                    selectedLabelColor = ElectricBlue
                ),
                border = BorderStroke(1.dp, if (selectedFilter == CatalogFilter.SCHEDULED) ElectricBlue else Color(0xFF334155))
            )
            FilterChip(
                selected = selectedFilter == CatalogFilter.EXPIRED,
                onClick = { selectedFilter = CatalogFilter.EXPIRED },
                label = { Text("Expired (${allMovies.count { it.isRightsExpired }})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = LiveRed.copy(alpha = 0.2f),
                    selectedLabelColor = LiveRed
                ),
                border = BorderStroke(1.dp, if (selectedFilter == CatalogFilter.EXPIRED) LiveRed else Color(0xFF334155))
            )
            FilterChip(
                selected = selectedFilter == CatalogFilter.DRAFTS,
                onClick = { selectedFilter = CatalogFilter.DRAFTS },
                label = { Text("Drafts (${allMovies.count { !it.isPublished }})", fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = NeonGold.copy(alpha = 0.2f),
                    selectedLabelColor = NeonGold
                ),
                border = BorderStroke(1.dp, if (selectedFilter == CatalogFilter.DRAFTS) NeonGold else Color(0xFF334155))
            )
        }

        // Movie List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (filteredMovies.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No movie titles found", color = TextSecondary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onAddNewMovie,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                        ) {
                            Text("+ Add First Movie", color = Color.Black)
                        }
                    }
                }
            }

            items(filteredMovies, key = { it.id }) { movie ->
                CatalogMovieCard(
                    movie = movie,
                    onTogglePublish = { isPub -> onTogglePublish(movie.id, isPub) },
                    onEdit = { onEditMovie(movie) },
                    onDelete = { movieToDelete = movie }
                )
            }

            item { Spacer(modifier = Modifier.height(30.dp)) }
        }
    }

    // Delete Confirmation Dialog
    if (movieToDelete != null) {
        val target = movieToDelete!!
        AlertDialog(
            onDismissRequest = { movieToDelete = null },
            title = { Text("Delete Movie", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Are you sure you want to remove '${target.title}' from the catalog? This will delete it from the local database.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteMovie(target.id)
                        movieToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LiveRed)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { movieToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CinemaSurfaceElevated
        )
    }
}

@Composable
private fun CatalogMovieCard(
    movie: Movie,
    onTogglePublish: (Boolean) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CinemaSurfaceElevated,
        border = BorderStroke(1.dp, if (movie.isPublished) Color(0xFF26324D) else Color(0xFF3B2D18)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 64.dp, height = 90.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1E293B))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = movie.title,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when {
                                movie.isRightsExpired -> LiveRed.copy(alpha = 0.2f)
                                movie.isScheduledRelease -> ElectricBlue.copy(alpha = 0.2f)
                                movie.isPublished -> NeonGreen.copy(alpha = 0.15f)
                                else -> NeonGold.copy(alpha = 0.15f)
                            }
                        ) {
                            Text(
                                text = when {
                                    movie.isRightsExpired -> "RIGHTS EXPIRED"
                                    movie.isScheduledRelease -> "SCHEDULED"
                                    movie.isPublished -> "LIVE IN APP"
                                    else -> "DRAFT"
                                },
                                color = when {
                                    movie.isRightsExpired -> LiveRed
                                    movie.isScheduledRelease -> ElectricBlue
                                    movie.isPublished -> NeonGreen
                                    else -> NeonGold
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${movie.year} · ${movie.duration} · ${movie.quality}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = movie.genres.joinToString(", "),
                        color = ElectricBlue,
                        fontSize = 11.sp,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = NeonGold, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("${movie.rating}", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        if (movie.isFeatured) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("★ Featured", color = NeonGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        if (movie.isTrending) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("🔥 Trending", color = Color(0xFFF97316), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (movie.publishAt != null && movie.isScheduledRelease) {
                        val dateStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(movie.publishAt))
                        Spacer(modifier = Modifier.height(3.dp))
                        Text("📅 Scheduled: $dateStr", color = ElectricBlue, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }

                    if (movie.rightsExpiryDate != null) {
                        val expStr = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(movie.rightsExpiryDate))
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (movie.isRightsExpired) "⚠️ Rights Expired: $expStr" else "🛡️ Rights Until: $expStr",
                            color = if (movie.isRightsExpired) LiveRed else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom action strip: Instant Publish Switch, Edit, Delete
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Publish Switch
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (movie.isPublished) "Publish Live" else "Draft",
                            color = if (movie.isPublished) NeonGreen else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = movie.isPublished,
                            onCheckedChange = { onTogglePublish(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonGreen
                            )
                        )
                    }

                    // Action buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = ElectricBlue, modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = LiveRed, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}
