package com.example.ui.screens.player

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PictureInPicture
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.Movie
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.CinemaSurface
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoPlayerScreen(
    movie: Movie,
    isPlaying: Boolean,
    currentTimeMs: Long,
    durationMs: Long,
    playbackSpeed: Float,
    selectedQuality: String,
    selectedSubtitle: String,
    selectedAudioTrack: String,
    isControlsLocked: Boolean,
    volume: Float,
    onTogglePlayPause: () -> Unit,
    onSeekRelative: (Int) -> Unit,
    onSeekToPosition: (Float) -> Unit,
    onSetPlaybackSpeed: (Float) -> Unit,
    onSetQuality: (String) -> Unit,
    onSetSubtitle: (String) -> Unit,
    onSetAudioTrack: (String) -> Unit,
    onToggleLock: () -> Unit,
    onSetVolume: (Float) -> Unit,
    onClosePlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var areControlsVisible by remember { mutableStateOf(true) }
    var activeModal by remember { mutableStateOf<String?>(null) }
    val sheetState = rememberModalBottomSheetState()

    // Auto-hide controls after 4 seconds of inactivity if playing and not locked
    LaunchedEffect(areControlsVisible, isPlaying, isControlsLocked) {
        if (areControlsVisible && isPlaying && !isControlsLocked) {
            delay(4200)
            areControlsVisible = false
        }
    }

    val speedOptions = listOf(0.5f, 0.75f, 1.0f, 1.25f, 1.5f, 2.0f)
    val qualityOptions = listOf("Auto", "360p", "480p", "720p", "1080p FHD", "4K Ultra HD")
    val subtitleOptions = listOf("Off", "English", "Hindi", "Urdu", "Spanish")
    val audioOptions = listOf("English (Dolby Atmos)", "Hindi (Original 5.1)", "Urdu (Stereo)")

    val progressFraction = if (durationMs > 0) (currentTimeMs.toFloat() / durationMs).coerceIn(0f, 1f) else 0f

    fun formatTime(ms: Long): String {
        val totalSecs = (ms / 1000).toInt()
        val hours = totalSecs / 3600
        val minutes = (totalSecs % 3600) / 60
        val seconds = totalSecs % 60
        return if (hours > 0) {
            String.format("%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                areControlsVisible = !areControlsVisible
            }
    ) {
        // Video Stage (Backdrop Stream Simulation)
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(movie.backdropUrl)
                .crossfade(true)
                .build(),
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark Vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    if (areControlsVisible) CinemaBlack.copy(alpha = 0.65f) else Color.Transparent
                )
        )

        // Subtle Pause Overlay with Movie Metadata (when paused & controls visible)
        if (!isPlaying && areControlsVisible) {
            Surface(
                color = CinemaBlack.copy(alpha = 0.85f),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.3f)),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 24.dp)
                    .width(260.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "PAUSED",
                        color = ElectricBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = movie.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${movie.year} • ${movie.quality} • ${movie.duration}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = movie.description,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        maxLines = 3,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        // Subtitle Overlay at bottom when active
        if (selectedSubtitle != "Off") {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (areControlsVisible) 110.dp else 40.dp)
                    .background(Color.Black.copy(alpha = 0.75f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = if (movie.subtitleUrl.isNotBlank()) {
                        "[$selectedSubtitle Track Active • Loaded from VTT Stream]"
                    } else {
                        "[$selectedSubtitle] Transmitting cinematic audio dialogue stream..."
                    },
                    color = Color.Yellow,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Player Controls (fade in/out)
        AnimatedVisibility(
            visible = areControlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onClosePlayer) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close Player", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = movie.title,
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${movie.quality} • ${movie.licenseType}",
                                color = ElectricBlue,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Top Action Buttons
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (!isControlsLocked) {
                            IconButton(onClick = { activeModal = "speed" }) {
                                Icon(Icons.Filled.Speed, contentDescription = "Speed", tint = Color.White)
                            }
                            IconButton(onClick = { activeModal = "quality" }) {
                                Icon(Icons.Filled.HighQuality, contentDescription = "Quality", tint = Color.White)
                            }
                            IconButton(onClick = { activeModal = "subtitles" }) {
                                Icon(Icons.Filled.Subtitles, contentDescription = "Subtitles", tint = Color.White)
                            }
                            IconButton(onClick = { activeModal = "audio" }) {
                                Icon(Icons.Filled.Audiotrack, contentDescription = "Audio Track", tint = Color.White)
                            }
                        }

                        IconButton(onClick = onToggleLock) {
                            Icon(
                                imageVector = if (isControlsLocked) Icons.Filled.Lock else Icons.Filled.LockOpen,
                                contentDescription = "Lock",
                                tint = if (isControlsLocked) ElectricBlue else Color.White
                            )
                        }
                    }
                }

                // Center Play / Rewind / Fast Forward buttons (Only if not locked)
                if (!isControlsLocked) {
                    Row(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalArrangement = Arrangement.spacedBy(28.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Rewind 10s
                        IconButton(
                            onClick = { onSeekRelative(-10) },
                            modifier = Modifier
                                .size(50.dp)
                                .background(CinemaBlack.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Replay10,
                                contentDescription = "Rewind 10s",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        // Play / Pause Primary
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .background(ElectricBlue, CircleShape)
                                .clickable { onTogglePlayPause() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = CinemaBlack,
                                modifier = Modifier.size(38.dp)
                            )
                        }

                        // Forward 10s
                        IconButton(
                            onClick = { onSeekRelative(10) },
                            modifier = Modifier
                                .size(50.dp)
                                .background(CinemaBlack.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Forward10,
                                contentDescription = "Forward 10s",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }

                // Bottom Controls Bar (Only if not locked)
                if (!isControlsLocked) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        // Time indicators
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = formatTime(currentTimeMs),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = formatTime(durationMs),
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        // Seek Bar Slider
                        Slider(
                            value = progressFraction,
                            onValueChange = { onSeekToPosition(it) },
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricBlue,
                                activeTrackColor = ElectricBlue,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Bottom Actions: Volume, Next Episode, PIP, Fullscreen
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Volume control
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.width(130.dp)
                            ) {
                                Icon(
                                    imageVector = if (volume > 0.1f) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeDown,
                                    contentDescription = "Volume",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Slider(
                                    value = volume,
                                    onValueChange = onSetVolume,
                                    colors = SliderDefaults.colors(
                                        thumbColor = ElectricBlue,
                                        activeTrackColor = ElectricBlue
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    color = CinemaSurfaceElevated,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable {
                                        onSeekToPosition(0.01f)
                                    }
                                ) {
                                    Text(
                                        text = "Next Episode",
                                        color = ElectricBlue,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                    )
                                }

                                IconButton(onClick = { /* Toggle PIP mode */ }) {
                                    Icon(Icons.Filled.PictureInPicture, contentDescription = "PIP", tint = Color.White)
                                }

                                IconButton(onClick = { /* Fullscreen toggle */ }) {
                                    Icon(Icons.Filled.Fullscreen, contentDescription = "Fullscreen", tint = Color.White)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheets for Player Controls: Speed, Quality, Subtitles, Audio
    if (activeModal != null) {
        ModalBottomSheet(
            onDismissRequest = { activeModal = null },
            sheetState = sheetState,
            containerColor = CinemaSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                val title = when (activeModal) {
                    "speed" -> "Playback Speed"
                    "quality" -> "Video Quality"
                    "subtitles" -> "Subtitles & Captions"
                    "audio" -> "Audio Track"
                    else -> ""
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = { activeModal = null }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                when (activeModal) {
                    "speed" -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            speedOptions.forEach { spd ->
                                FilterChip(
                                    selected = playbackSpeed == spd,
                                    onClick = {
                                        onSetPlaybackSpeed(spd)
                                        activeModal = null
                                    },
                                    label = { Text("${spd}x") }
                                )
                            }
                        }
                    }
                    "quality" -> {
                        Column {
                            qualityOptions.forEach { q ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSetQuality(q)
                                            activeModal = null
                                        }
                                        .padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(q, color = if (selectedQuality == q) ElectricBlue else TextPrimary, fontSize = 14.sp)
                                    if (selectedQuality == q) {
                                        Text("Selected", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    "subtitles" -> {
                        Column {
                            subtitleOptions.forEach { sub ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSetSubtitle(sub)
                                            activeModal = null
                                        }
                                        .padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(sub, color = if (selectedSubtitle == sub) ElectricBlue else TextPrimary, fontSize = 14.sp)
                                    if (selectedSubtitle == sub) {
                                        Text("Selected", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                    "audio" -> {
                        Column {
                            audioOptions.forEach { aud ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            onSetAudioTrack(aud)
                                            activeModal = null
                                        }
                                        .padding(vertical = 10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(aud, color = if (selectedAudioTrack == aud) ElectricBlue else TextPrimary, fontSize = 14.sp)
                                    if (selectedAudioTrack == aud) {
                                        Text("Selected", color = ElectricBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}
