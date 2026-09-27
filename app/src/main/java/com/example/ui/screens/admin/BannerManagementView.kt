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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.data.model.BannerItem
import com.example.data.model.Movie
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun BannerManagementView(
    banners: List<BannerItem>,
    allMovies: List<Movie>,
    onSaveBanner: (BannerItem) -> Unit,
    onDeleteBanner: (String) -> Unit,
    onToggleActive: (String, Boolean) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var editingBanner by remember { mutableStateOf<BannerItem?>(null) }
    var bannerToDelete by remember { mutableStateOf<BannerItem?>(null) }

    val activeCount = banners.count { it.isActive }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
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
                        text = "Hero Banners",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$activeCount active / ${banners.size} total configured",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Button(
                onClick = {
                    editingBanner = null
                    isDialogOpen = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "New Banner", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info Banner
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF0F172A),
            border = BorderStroke(1.dp, Color(0xFF1E293B)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.ViewCarousel,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Active banners rotate on the Home screen hero carousel. Toggle the switch to show or hide instantly.",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Banner List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(banners, key = { it.id }) { banner ->
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = CinemaSurfaceElevated),
                    border = BorderStroke(
                        1.dp,
                        if (banner.isActive) ElectricBlue.copy(alpha = 0.4f) else Color(0xFF1E293B)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Thumbnail Preview
                            Box(
                                modifier = Modifier
                                    .size(width = 110.dp, height = 65.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF0F172A))
                            ) {
                                if (banner.imageUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = banner.imageUrl,
                                        contentDescription = banner.title,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Image,
                                            contentDescription = null,
                                            tint = Color(0xFF475569)
                                        )
                                    }
                                }

                                // Badge Overlay
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.Black.copy(alpha = 0.7f),
                                    modifier = Modifier
                                        .align(Alignment.TopStart)
                                        .padding(4.dp)
                                ) {
                                    Text(
                                        text = banner.badgeText,
                                        color = NeonGold,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = banner.title,
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = banner.subtitle,
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                val linkedMovie = allMovies.find { it.id == banner.movieId }
                                Text(
                                    text = "Linked: ${linkedMovie?.title ?: (banner.movieId ?: "None")}",
                                    color = ElectricBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }

                            // Active Switch
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Switch(
                                    checked = banner.isActive,
                                    onCheckedChange = { onToggleActive(banner.id, it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.Black,
                                        checkedTrackColor = NeonGreen,
                                        uncheckedThumbColor = TextSecondary,
                                        uncheckedTrackColor = Color(0xFF334155)
                                    )
                                )
                                Text(
                                    text = if (banner.isActive) "Active" else "Hidden",
                                    color = if (banner.isActive) NeonGreen else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Bottom Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                onClick = {
                                    editingBanner = banner
                                    isDialogOpen = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = null,
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit", color = ElectricBlue, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            TextButton(
                                onClick = { bannerToDelete = banner }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = null,
                                    tint = LiveRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Delete", color = LiveRed, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (isDialogOpen) {
        AddEditBannerDialog(
            banner = editingBanner,
            allMovies = allMovies,
            onDismiss = { isDialogOpen = false },
            onSave = { saved ->
                onSaveBanner(saved)
                isDialogOpen = false
            }
        )
    }

    // Delete Confirmation
    if (bannerToDelete != null) {
        val b = bannerToDelete!!
        AlertDialog(
            onDismissRequest = { bannerToDelete = null },
            containerColor = CinemaSurfaceElevated,
            title = {
                Text(text = "Delete Banner?", color = TextPrimary, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    text = "Are you sure you want to delete '${b.title}' from the hero banner rotation?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteBanner(b.id)
                        bannerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = LiveRed)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { bannerToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

@Composable
fun AddEditBannerDialog(
    banner: BannerItem?,
    allMovies: List<Movie>,
    onDismiss: () -> Unit,
    onSave: (BannerItem) -> Unit
) {
    var title by remember { mutableStateOf(banner?.title ?: "") }
    var subtitle by remember { mutableStateOf(banner?.subtitle ?: "") }
    var imageUrl by remember { mutableStateOf(banner?.imageUrl ?: "") }
    var badgeText by remember { mutableStateOf(banner?.badgeText ?: "FEATURED") }
    var selectedMovieId by remember { mutableStateOf(banner?.movieId ?: (allMovies.firstOrNull()?.id ?: "")) }
    var isActive by remember { mutableStateOf(banner?.isActive ?: true) }
    var displayOrder by remember { mutableStateOf((banner?.displayOrder ?: 1).toString()) }

    val isEditing = banner != null

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CinemaSurfaceElevated,
        title = {
            Text(
                text = if (isEditing) "Edit Hero Banner" else "Add Hero Banner",
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Banner Title") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = subtitle,
                    onValueChange = { subtitle = it },
                    label = { Text("Subtitle / Catchphrase") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("Image URL (Wide 16:9 Backdrop)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = badgeText,
                        onValueChange = { badgeText = it },
                        label = { Text("Badge Text") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
                        value = displayOrder,
                        onValueChange = { displayOrder = it },
                        label = { Text("Order") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = Color(0xFF334155),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.weight(0.6f)
                    )
                }

                // Quick preset autofill from movies
                if (allMovies.isNotEmpty() && imageUrl.isBlank()) {
                    Text(
                        text = "Quick Link from Movie:",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        allMovies.take(3).forEach { movie ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF1E293B),
                                border = BorderStroke(1.dp, Color(0xFF334155)),
                                onClick = {
                                    title = movie.title
                                    subtitle = movie.tagline
                                    imageUrl = movie.backdropUrl.ifBlank { movie.posterUrl }
                                    selectedMovieId = movie.id
                                    badgeText = if (movie.isTrending) "TRENDING" else "STREAMING NOW"
                                }
                            ) {
                                Text(
                                    text = movie.title,
                                    color = TextPrimary,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val id = banner?.id ?: "banner_${System.currentTimeMillis()}"
                        val orderNum = displayOrder.toIntOrNull() ?: 1
                        val updated = BannerItem(
                            id = id,
                            title = title.trim(),
                            subtitle = subtitle.trim(),
                            imageUrl = imageUrl.trim(),
                            movieId = selectedMovieId.ifBlank { null },
                            badgeText = badgeText.ifBlank { "FEATURED" },
                            displayOrder = orderNum,
                            isActive = isActive
                        )
                        onSave(updated)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                enabled = title.isNotBlank()
            ) {
                Text(if (isEditing) "Save Changes" else "Create Banner", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}
