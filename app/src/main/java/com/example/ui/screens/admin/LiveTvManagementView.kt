package com.example.ui.screens.admin

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.LiveChannel
import com.example.data.model.LiveChannelReport
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
fun LiveTvManagementView(
    channels: List<LiveChannel>,
    reports: List<LiveChannelReport>,
    onAddNewChannel: () -> Unit,
    onEditChannel: (LiveChannel) -> Unit,
    onDeleteChannel: (String) -> Unit,
    onTogglePublish: (String, Boolean) -> Unit,
    onToggleEnabled: (String, Boolean) -> Unit,
    onResolveReport: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Channels, 1: Reports
    var channelToDelete by remember { mutableStateOf<LiveChannel?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .size(40.dp)
                    .background(CinemaSurfaceElevated, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Live TV Management",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Authorized Feeds, Program Schedules & Rights",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = onAddNewChannel,
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = CinemaBlack, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Feed", color = CinemaBlack, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Tabs: Channels vs Reports
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = CinemaDarkBackground,
            contentColor = ElectricBlue,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = ElectricBlue,
                    height = 2.dp
                )
            },
            divider = {}
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = {
                    Text(
                        text = "Channels (${channels.size})",
                        color = if (selectedTab == 0) ElectricBlue else TextSecondary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = {
                    val pendingCount = reports.count { it.status == "Pending" }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Problem Reports",
                            color = if (selectedTab == 1) ElectricBlue else TextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        if (pendingCount > 0) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = LiveRed, shape = RoundedCornerShape(10.dp)) {
                                Text(
                                    text = "$pendingCount",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (selectedTab == 0) {
            // Channels List
            if (channels.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Tv, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No Live Channels Configured", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Tap '+ Add Feed' to configure your first legal broadcast stream.", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(channels, key = { it.id }) { ch ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CinemaSurfaceElevated)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(ch.logoUrl)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = ch.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier
                                            .size(54.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF1E283C))
                                    )

                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = ch.name,
                                                color = TextPrimary,
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            if (ch.isFeatured) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Icon(Icons.Filled.Star, contentDescription = "Featured", tint = NeonGold, modifier = Modifier.size(14.dp))
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = "${ch.category} • ${ch.language} • ${ch.country}",
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = "Now: ${ch.currentProgram}",
                                            color = ElectricBlue,
                                            fontSize = 11.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    IconButton(onClick = { onEditChannel(ch) }) {
                                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = ElectricBlue, modifier = Modifier.size(20.dp))
                                    }

                                    IconButton(onClick = { channelToDelete = ch }) {
                                        Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = LiveRed, modifier = Modifier.size(20.dp))
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color(0xFF1F2B3E), thickness = 0.8.dp)
                                Spacer(modifier = Modifier.height(10.dp))

                                // Controls & Status Badges
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Badges
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Surface(
                                            color = if (ch.isPublished) ElectricBlue.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (ch.isPublished) "PUBLISHED" else "DRAFT",
                                                color = if (ch.isPublished) ElectricBlue else TextSecondary,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        Surface(
                                            color = if (ch.isEnabled) Color(0xFF10B981).copy(alpha = 0.2f) else LiveRed.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = if (ch.isEnabled) "ACTIVE" else "OFFLINE",
                                                color = if (ch.isEnabled) Color(0xFF10B981) else LiveRed,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }

                                        if (ch.isPremiumOnly) {
                                            Surface(
                                                color = NeonGold.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "PREMIUM",
                                                    color = NeonGold,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        if (ch.isRightsExpired) {
                                            Surface(
                                                color = LiveRed,
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "RIGHTS EXPIRED",
                                                    color = Color.White,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }
                                    }

                                    // Switches
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("Pub", color = TextSecondary, fontSize = 11.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Switch(
                                                checked = ch.isPublished,
                                                onCheckedChange = { onTogglePublish(ch.id, it) },
                                                colors = SwitchDefaults.colors(checkedThumbColor = ElectricBlue)
                                            )
                                        }

                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text("On", color = TextSecondary, fontSize = 11.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Switch(
                                                checked = ch.isEnabled,
                                                onCheckedChange = { onToggleEnabled(ch.id, it) },
                                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF10B981))
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // Reports List
            if (reports.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No Problem Reports", color = TextPrimary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("All live channel feeds are functioning smoothly.", color = TextSecondary, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 16.dp, end = 16.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(reports, key = { it.id }) { rep ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = CinemaSurfaceElevated)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = rep.channelName,
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Surface(
                                        color = if (rep.status == "Resolved") Color(0xFF10B981).copy(alpha = 0.2f) else LiveRed.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = rep.status.uppercase(),
                                            color = if (rep.status == "Resolved") Color(0xFF10B981) else LiveRed,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "Issue: ${rep.issueType}",
                                    color = ElectricBlue,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )

                                if (rep.description.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = rep.description,
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }

                                if (rep.status != "Resolved") {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { onResolveReport(rep.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.align(Alignment.End)
                                    ) {
                                        Icon(Icons.Filled.Check, contentDescription = null, tint = CinemaBlack, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Mark Resolved", color = CinemaBlack, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Delete Confirmation Dialog
        if (channelToDelete != null) {
            AlertDialog(
                onDismissRequest = { channelToDelete = null },
                containerColor = CinemaSurfaceElevated,
                title = { Text("Delete Live Channel?", color = TextPrimary, fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Are you sure you want to delete '${channelToDelete?.name}'? Normal users will no longer see this channel in Live TV.",
                        color = TextSecondary,
                        fontSize = 13.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            channelToDelete?.let { onDeleteChannel(it.id) }
                            channelToDelete = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LiveRed)
                    ) {
                        Text("Delete Feed", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { channelToDelete = null }) {
                        Text("Cancel", color = TextSecondary)
                    }
                }
            )
        }
    }
}
