package com.example.ui.screens.livetv

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LiveRed
import com.example.ui.theme.NeonGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun LiveChannelDetailSheet(
    channel: LiveChannel,
    isFavorite: Boolean,
    isUserPremium: Boolean,
    onPlayClick: () -> Unit,
    onFavoriteToggle: () -> Unit,
    onShareClick: () -> Unit,
    onSubmitReport: (issueType: String, description: String) -> Unit,
    onUpgradeClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showReportDialog by remember { mutableStateOf(false) }
    var selectedIssueType by remember { mutableStateOf("Channel not working") }
    var reportDescription by remember { mutableStateOf("") }

    val isPlayable = channel.isPlayableFor(isUserPremium)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            // Hero Banner with Back Button & Logo
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(channel.logoUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = channel.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        CinemaBlack.copy(alpha = 0.6f),
                                        CinemaDarkBackground.copy(alpha = 0.85f),
                                        CinemaDarkBackground
                                    )
                                )
                            )
                    )

                    // Top Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .align(Alignment.TopCenter),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(40.dp)
                                .background(CinemaBlack.copy(alpha = 0.6f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = onShareClick,
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(CinemaBlack.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = "Share",
                                    tint = Color.White
                                )
                            }

                            IconButton(
                                onClick = { showReportDialog = true },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(CinemaBlack.copy(alpha = 0.6f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Flag,
                                    contentDescription = "Report Problem",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    // Floating Badges on Banner
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(color = LiveRed, shape = RoundedCornerShape(4.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Icon(Icons.Filled.FiberManualRecord, contentDescription = null, tint = Color.White, modifier = Modifier.size(8.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("LIVE STREAM", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        if (channel.isPremiumOnly) {
                            Surface(color = NeonGold, shape = RoundedCornerShape(4.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Icon(Icons.Filled.Lock, contentDescription = null, tint = CinemaBlack, modifier = Modifier.size(10.dp))
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("PREMIUM ONLY", color = CinemaBlack, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }

            // Channel Title, Category, Language & Country Details
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        text = channel.name,
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            color = CinemaSurfaceElevated,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = channel.category,
                                color = ElectricBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text("•", color = TextSecondary)
                        Text(channel.language, color = TextSecondary, fontSize = 12.sp)
                        Text("•", color = TextSecondary)
                        Text(channel.country, color = TextSecondary, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (channel.description.isNotBlank()) {
                        Text(
                            text = channel.description,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 19.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Action Buttons (Play / Upgrade & Favorite)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (channel.isPremiumOnly && !isUserPremium) {
                            Button(
                                onClick = onUpgradeClick,
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = NeonGold),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Filled.Lock, contentDescription = null, tint = CinemaBlack, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Upgrade to Watch", color = CinemaBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        } else {
                            Button(
                                onClick = onPlayClick,
                                modifier = Modifier.weight(1f).height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Filled.PlayArrow, contentDescription = null, tint = CinemaBlack, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Watch Live Now", color = CinemaBlack, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }

                        OutlinedButton(
                            onClick = onFavoriteToggle,
                            modifier = Modifier.height(48.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isFavorite) ElectricBlue else Color.White.copy(alpha = 0.3f)
                            )
                        ) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                contentDescription = null,
                                tint = if (isFavorite) ElectricBlue else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isFavorite) "Favorited" else "Favorite",
                                color = if (isFavorite) ElectricBlue else Color.White,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Rights & Licensing Info Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = CinemaSurfaceElevated,
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E283C))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "LICENSED STREAMING DETAILS",
                                color = TextTertiary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Rights Provider: ${channel.rightsProvider}",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Authorized Territory: ${channel.territory}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            if (channel.rightsExpiryDate != null) {
                                val expiryDays = ((channel.rightsExpiryDate - System.currentTimeMillis()) / (24 * 3600 * 1000L)).coerceAtLeast(0)
                                Text(
                                    text = "Rights Status: Active ($expiryDays days remaining in broadcast agreement)",
                                    color = if (expiryDays > 14) ElectricBlue else NeonGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // EPG Program Guide Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Electronic Program Guide (EPG)",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // EPG Program List Items
            if (channel.schedule.isEmpty()) {
                item {
                    Text(
                        text = "EPG schedule information is being synchronized with the broadcaster feed.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else {
                items(channel.schedule) { prog ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (prog.isLiveNow) CinemaSurfaceElevated else Color(0xFF141926)
                        ),
                        border = if (prog.isLiveNow) androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f)) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.width(72.dp)) {
                                Text(
                                    text = prog.startTime,
                                    color = if (prog.isLiveNow) ElectricBlue else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = prog.endTime,
                                    color = TextTertiary,
                                    fontSize = 11.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = prog.title,
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    if (prog.isLiveNow) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(color = LiveRed, shape = RoundedCornerShape(3.dp)) {
                                            Text(
                                                text = "NOW",
                                                color = Color.White,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Black,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }

                                if (prog.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = prog.description,
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(48.dp))
            }
        }

        // Report Problem Dialog
        if (showReportDialog) {
            AlertDialog(
                onDismissRequest = { showReportDialog = false },
                containerColor = CinemaSurfaceElevated,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Warning, contentDescription = null, tint = LiveRed, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Report Channel Issue", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column {
                        Text(
                            text = "Help us maintain top broadcast quality for '${channel.name}':",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        val issueTypes = listOf(
                            "Channel not working",
                            "Buffering / Stuttering",
                            "Wrong program information",
                            "Wrong channel information",
                            "Other problem"
                        )

                        issueTypes.forEach { issue ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedIssueType = issue }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = selectedIssueType == issue,
                                    onClick = { selectedIssueType = issue },
                                    colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(issue, color = TextPrimary, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = reportDescription,
                            onValueChange = { reportDescription = it },
                            label = { Text("Additional details (optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onSubmitReport(selectedIssueType, reportDescription)
                            showReportDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                    ) {
                        Text("Submit Report", color = CinemaBlack, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showReportDialog = false }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }
    }
}
