package com.example.ui.screens.livetv

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ClosedCaption
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.LiveChannel
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NeonGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

@Composable
fun LiveChannelPlayerScreen(
    channel: LiveChannel,
    isUserPremium: Boolean,
    onClosePlayer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPlaying by remember { mutableStateOf(true) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }
    var areControlsVisible by remember { mutableStateOf(true) }
    var volume by remember { mutableFloatStateOf(0.85f) }
    var isFullscreen by remember { mutableStateOf(false) }
    var subtitlesEnabled by remember { mutableStateOf(false) }

    // Stream connection simulation: buffer for 1.2s then play
    LaunchedEffect(channel.id) {
        isLoading = true
        isError = false
        delay(1200)
        isLoading = false
    }

    // Auto-hide controls after 4 seconds
    LaunchedEffect(areControlsVisible, isPlaying) {
        if (areControlsVisible && isPlaying && !isLoading && !isError) {
            delay(4000)
            areControlsVisible = false
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
        // Video Stage Simulation
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(channel.logoUrl)
                .crossfade(true)
                .build(),
            contentDescription = channel.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Dark Vignette
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CinemaBlack.copy(alpha = 0.75f),
                            CinemaBlack.copy(alpha = 0.4f),
                            CinemaBlack.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Loading Overlay
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(
                        color = ElectricBlue,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Connecting to authorized live broadcast...",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Error State Overlay
        if (isError) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(24.dp)
                        .background(CinemaBlack.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                        .border(1.dp, LiveRed.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                        .padding(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Warning,
                        contentDescription = null,
                        tint = LiveRed,
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Live stream temporarily unavailable.",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Please check your network connection or try reconnecting to the feed.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = ElectricBlue,
                        modifier = Modifier.clickable {
                            isError = false
                            isLoading = true
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Filled.Refresh, contentDescription = null, tint = CinemaBlack, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Retry Stream", color = CinemaBlack, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Subtitles Overlay simulation
        if (subtitlesEnabled && !isLoading && !isError) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (areControlsVisible) 110.dp else 40.dp)
            ) {
                Surface(
                    color = CinemaBlack.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text(
                        text = "[Live Broadcast Audio - ${channel.language}]",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Interactive Controls Layer
        AnimatedVisibility(
            visible = areControlsVisible && !isLoading,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Top App Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .align(Alignment.TopCenter),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClosePlayer,
                        modifier = Modifier
                            .background(CinemaBlack.copy(alpha = 0.5f), CircleShape)
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Channel Name & Category
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = channel.name,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${channel.category} • ${channel.country}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    // LIVE Badge
                    Surface(
                        color = LiveRed,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Filled.FiberManualRecord, contentDescription = null, tint = Color.White, modifier = Modifier.size(8.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("LIVE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Subtitle Button
                    IconButton(
                        onClick = { subtitlesEnabled = !subtitlesEnabled },
                        modifier = Modifier
                            .background(
                                if (subtitlesEnabled) ElectricBlue.copy(alpha = 0.3f) else CinemaBlack.copy(alpha = 0.5f),
                                CircleShape
                            )
                            .size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ClosedCaption,
                            contentDescription = "Subtitles",
                            tint = if (subtitlesEnabled) ElectricBlue else Color.White
                        )
                    }
                }

                // Center Play/Pause & Reconnect
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            isError = false
                            isLoading = true
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .background(CinemaBlack.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = "Reconnect",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Large Play/Pause
                    IconButton(
                        onClick = { isPlaying = !isPlaying },
                        modifier = Modifier
                            .size(70.dp)
                            .background(ElectricBlue, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = CinemaBlack,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    // Trigger error simulation button (for verifying error prompt)
                    IconButton(
                        onClick = { isError = true },
                        modifier = Modifier
                            .size(46.dp)
                            .background(CinemaBlack.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Warning,
                            contentDescription = "Test Failure",
                            tint = Color.White.copy(alpha = 0.6f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Bottom Bar Controls: Program Info & Volume & Fullscreen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    CinemaBlack.copy(alpha = 0.95f)
                                )
                            )
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    // Current Program Details
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            color = ElectricBlue.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "ON AIR",
                                color = ElectricBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = channel.currentProgram,
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (channel.currentProgramTime.isNotBlank()) {
                                Text(
                                    text = channel.currentProgramTime,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Volume & Fullscreen row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = when {
                                volume == 0f -> Icons.Filled.VolumeMute
                                volume < 0.5f -> Icons.Filled.VolumeDown
                                else -> Icons.Filled.VolumeUp
                            },
                            contentDescription = "Volume",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Slider(
                            value = volume,
                            onValueChange = { volume = it },
                            modifier = Modifier.weight(1f),
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricBlue,
                                activeTrackColor = ElectricBlue,
                                inactiveTrackColor = Color.White.copy(alpha = 0.3f)
                            )
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        IconButton(
                            onClick = { isFullscreen = !isFullscreen },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isFullscreen) Icons.Filled.FullscreenExit else Icons.Filled.Fullscreen,
                                contentDescription = "Fullscreen",
                                tint = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
