package com.example.ui.screens.downloads

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.DownloadItem
import com.example.data.model.DownloadStatus
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LiveRed
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    downloads: List<DownloadItem>,
    onPauseClick: (String) -> Unit,
    onResumeClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onPlayDownloaded: (String) -> Unit,
    onExploreMoviesClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilterIndex by remember { mutableIntStateOf(0) }
    var isSettingsOpen by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // Download Settings State
    var wifiOnly by remember { mutableStateOf(true) }
    var autoDownload by remember { mutableStateOf(false) }
    var defaultQuality by remember { mutableStateOf("1080p FHD") }
    var storageLocation by remember { mutableStateOf("Internal Storage (/Android/data/z1)") }

    val filterTabs = listOf("All (${downloads.size})", "Downloading", "Completed", "Failed")

    val filteredList = when (selectedFilterIndex) {
        1 -> downloads.filter { it.status == DownloadStatus.DOWNLOADING || it.status == DownloadStatus.PAUSED }
        2 -> downloads.filter { it.status == DownloadStatus.COMPLETED }
        3 -> downloads.filter { it.status == DownloadStatus.FAILED }
        else -> downloads
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Top Header with Title and Settings Icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Offline Downloads",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Licensed offline viewing",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }

            IconButton(
                onClick = { isSettingsOpen = true },
                modifier = Modifier
                    .size(40.dp)
                    .background(CinemaSurfaceElevated, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Download Settings",
                    tint = ElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Storage Usage Indicator (e.g., "Storage used: 2.4 GB / 10 GB")
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            shape = RoundedCornerShape(12.dp),
            color = CinemaSurfaceElevated,
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26324D))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Storage,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Storage Used",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = "2.4 GB / 10 GB",
                        color = ElectricBlue,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Dual progress bar representation
                LinearProgressIndicator(
                    progress = { 0.24f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ElectricBlue,
                    trackColor = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "7.6 GB free for offline downloads • High speed write",
                    color = TextTertiary,
                    fontSize = 11.sp
                )
            }
        }

        // Tabs Row: All, Downloading, Completed, Failed
        TabRow(
            selectedTabIndex = selectedFilterIndex,
            containerColor = CinemaDarkBackground,
            contentColor = ElectricBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedFilterIndex]),
                    color = ElectricBlue
                )
            },
            divider = {}
        ) {
            filterTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedFilterIndex == index,
                    onClick = { selectedFilterIndex = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 13.sp,
                            fontWeight = if (selectedFilterIndex == index) FontWeight.Bold else FontWeight.Normal,
                            color = if (selectedFilterIndex == index) ElectricBlue else TextSecondary
                        )
                    }
                )
            }
        }

        // Downloads List or Empty State
        if (filteredList.isEmpty()) {
            EmptyStateView(
                icon = Icons.Filled.Download,
                title = "No downloads yet",
                description = "Movies you are allowed to download will appear here for high-definition offline watching.",
                actionButtonText = "Browse Movies",
                onActionClick = onExploreMoviesClick,
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredList, key = { it.id }) { item ->
                    DownloadItemCard(
                        item = item,
                        onPause = { onPauseClick(item.id) },
                        onResume = { onResumeClick(item.id) },
                        onDelete = { onDeleteClick(item.id) },
                        onPlay = { onPlayDownloaded(item.movieId) }
                    )
                }
            }
        }
    }

    // Download Settings Bottom Sheet
    if (isSettingsOpen) {
        ModalBottomSheet(
            onDismissRequest = { isSettingsOpen = false },
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
                        text = "Download Preferences",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { isSettingsOpen = false }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Wi-Fi Only Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Download via Wi-Fi only", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Avoid consuming mobile network data", color = TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = wifiOnly,
                        onCheckedChange = { wifiOnly = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricBlue)
                    )
                }

                // Auto-Download Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-download next episode", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        Text("Pre-buffers subsequent licensed episodes", color = TextSecondary, fontSize = 12.sp)
                    }
                    Switch(
                        checked = autoDownload,
                        onCheckedChange = { autoDownload = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricBlue)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Preferred Quality
                Text("Offline Video Quality", color = ElectricBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("720p HD", "1080p FHD", "4K Ultra HD").forEach { q ->
                        FilterChip(
                            selected = defaultQuality == q,
                            onClick = { defaultQuality = q },
                            label = { Text(q) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Storage Location
                Text("Storage Location", color = ElectricBlue, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = CinemaSurfaceElevated,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = storageLocation,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun DownloadItemCard(
    item: DownloadItem,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onDelete: () -> Unit,
    onPlay: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (item.status == DownloadStatus.COMPLETED) onPlay()
            },
        shape = RoundedCornerShape(12.dp),
        color = CinemaSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26324D))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Movie Poster
            Box(
                modifier = Modifier
                    .size(width = 65.dp, height = 90.dp)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(item.posterUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = item.movieTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                if (item.status == DownloadStatus.COMPLETED) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f)),
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
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details and progress
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.movieTitle,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.quality,
                        color = ElectricBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(text = " • ", color = TextTertiary, fontSize = 11.sp)
                    Text(
                        text = item.fileSizeFormatted,
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Progress Bar for In-Progress downloads
                if (item.status == DownloadStatus.DOWNLOADING || item.status == DownloadStatus.PAUSED) {
                    LinearProgressIndicator(
                        progress = { item.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = if (item.status == DownloadStatus.PAUSED) Color(0xFFFFB800) else ElectricBlue,
                        trackColor = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${(item.progress * 100).toInt()}% • ${item.speedFormatted}",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "${item.downloadedSizeFormatted} of ${item.fileSizeFormatted}",
                            color = TextTertiary,
                            fontSize = 10.sp
                        )
                    }
                } else if (item.status == DownloadStatus.COMPLETED) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Ready to stream offline",
                            color = SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else if (item.status == DownloadStatus.FAILED) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Error,
                            contentDescription = null,
                            tint = LiveRed,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Download failed • Tap to retry",
                            color = LiveRed,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Action Buttons: Pause/Resume/Play & Delete
            Row(verticalAlignment = Alignment.CenterVertically) {
                when (item.status) {
                    DownloadStatus.DOWNLOADING -> {
                        IconButton(onClick = onPause) {
                            Icon(Icons.Filled.Pause, contentDescription = "Pause", tint = ElectricBlue)
                        }
                    }
                    DownloadStatus.PAUSED -> {
                        IconButton(onClick = onResume) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Resume", tint = ElectricBlue)
                        }
                    }
                    DownloadStatus.FAILED -> {
                        IconButton(onClick = onResume) {
                            Icon(Icons.Filled.Refresh, contentDescription = "Retry", tint = ElectricBlue)
                        }
                    }
                    DownloadStatus.COMPLETED -> {
                        IconButton(onClick = onPlay) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Play", tint = ElectricBlue)
                        }
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = TextTertiary
                    )
                }
            }
        }
    }
}
