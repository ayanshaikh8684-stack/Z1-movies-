package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.data.remote.AdminSession
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.LiveRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun ProfileScreen(
    userProfile: UserProfile,
    adminSession: AdminSession? = null,
    isDarkMode: Boolean = true,
    onToggleDarkMode: () -> Unit = {},
    onTogglePremiumTier: () -> Unit = {},
    onMyListClick: () -> Unit,
    onLiveTvClick: () -> Unit = {},
    onDownloadsClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onAdminClick: () -> Unit,
    onAuthClick: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var dataSaverEnabled by remember { mutableStateOf(userProfile.dataSaver) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 100.dp)
    ) {
        // User Profile Header Card
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = CinemaSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26324D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(ElectricBlue, Color(0xFF7C3AED))
                                )
                            )
                            .border(2.dp, ElectricBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = userProfile.name.take(1),
                            color = Color.White,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = userProfile.name,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Filled.VerifiedUser,
                                contentDescription = "Verified Pro",
                                tint = ElectricBlue,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = userProfile.email,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (userProfile.isPremiumActive) Color(0xFFFFB800).copy(alpha = 0.2f) else ElectricBlue.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = userProfile.membershipTier,
                                    color = if (userProfile.isPremiumActive) Color(0xFFFFB800) else ElectricBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            if (adminSession?.isAuthenticated == true) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFEAB308).copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = adminSession.user.role.name,
                                        color = Color(0xFFEAB308),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Fast toggle membership tier (for verifying and testing Premium Early Access)
                        androidx.compose.material3.OutlinedButton(
                            onClick = onTogglePremiumTier,
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (userProfile.isPremiumActive) Color(0xFFFFB800).copy(alpha = 0.6f) else ElectricBlue.copy(alpha = 0.6f)
                            ),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Text(
                                text = if (userProfile.isPremiumActive) "Switch to Free Tier" else "Activate Z1 Cinema Pro",
                                fontSize = 11.sp,
                                color = if (userProfile.isPremiumActive) Color(0xFFFFB800) else ElectricBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Section: Content & Activity
        item {
            Text(
                text = "Content & Library",
                color = ElectricBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CinemaSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26324D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProfileOptionRow(
                        icon = Icons.Filled.Bookmark,
                        title = "My List",
                        subtitle = "Saved movies & favorites",
                        onClick = onMyListClick
                    )
                    DividerOption()
                    ProfileOptionRow(
                        icon = Icons.Filled.History,
                        title = "Watch History",
                        subtitle = "Resume recently viewed",
                        onClick = onMyListClick
                    )
                    DividerOption()
                    ProfileOptionRow(
                        icon = Icons.Filled.Download,
                        title = "Downloads",
                        subtitle = "Manage offline licensed movies",
                        onClick = onDownloadsClick
                    )
                    DividerOption()
                    ProfileOptionRow(
                        icon = Icons.Filled.Notifications,
                        title = "Notifications",
                        subtitle = "New releases & updates",
                        onClick = onNotificationsClick
                    )
                    DividerOption()
                    ProfileOptionRow(
                        icon = Icons.Filled.Tv,
                        title = "Live TV & Broadcasts",
                        subtitle = "Watch 24/7 channels, news & sports",
                        onClick = onLiveTvClick
                    )
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Section: Playback & Streaming Settings
        item {
            Text(
                text = "Playback & Streaming",
                color = ElectricBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CinemaSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26324D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProfileOptionRow(
                        icon = Icons.Filled.PlayCircle,
                        title = "Playback Settings",
                        subtitle = "Default speed: 1.0x • Auto-skip intros",
                        onClick = { onShowMessage("Playback speed presets updated") }
                    )
                    DividerOption()
                    ProfileOptionRow(
                        icon = Icons.Filled.HighQuality,
                        title = "Video Quality",
                        subtitle = "Current: 1080p FHD (Auto 4K on Wi-Fi)",
                        onClick = { onShowMessage("Video quality preset: 1080p / 4K Ultra HD") }
                    )
                    DividerOption()
                    ProfileOptionRow(
                        icon = Icons.Filled.Subtitles,
                        title = "Subtitle Settings",
                        subtitle = "English, Hindi, Urdu styling",
                        onClick = { onShowMessage("Subtitles set to English with high contrast backdrop") }
                    )
                    DividerOption()
                    ProfileOptionRow(
                        icon = Icons.Filled.Language,
                        title = "App Language",
                        subtitle = "English (US)",
                        onClick = { onShowMessage("Language selected: English") }
                    )
                    DividerOption()
                    // Dark Mode Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.DarkMode, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("Dark Cinema Mode", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text(if (isDarkMode) "High contrast OLED dark palette" else "Clean modern light theme", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                        Switch(
                            checked = isDarkMode,
                            onCheckedChange = {
                                onToggleDarkMode()
                                onShowMessage(if (!isDarkMode) "Cinema Dark mode active" else "Cinema Light mode active")
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricBlue)
                        )
                    }
                    DividerOption()
                    // Data Saver Switch
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.DataSaverOn, contentDescription = null, tint = ElectricBlue, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text("Data Saver", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                                Text("Optimize stream bandwidth", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                        Switch(
                            checked = dataSaverEnabled,
                            onCheckedChange = {
                                dataSaverEnabled = it
                                onShowMessage(if (it) "Data saver enabled: 720p cap" else "Data saver disabled: full 4K")
                            },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = ElectricBlue)
                        )
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }

        // Section: System & Legal
        item {
            Text(
                text = "System & Support",
                color = ElectricBlue,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = CinemaSurfaceElevated,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26324D)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    ProfileOptionRow(
                        icon = Icons.Filled.AdminPanelSettings,
                        title = "Admin Content Manager",
                        subtitle = if (adminSession?.isAuthenticated == true) "Manage catalog (${adminSession.user.role.name})" else "Authorized staff login required",
                        onClick = onAdminClick
                    )
                    DividerOption()
                    ProfileOptionRow(
                        icon = Icons.Filled.Lock,
                        title = "Privacy Policy",
                        subtitle = "Zero telemetry on personal streams",
                        onClick = { onShowMessage("Z1 Movies adheres to strict content license & privacy") }
                    )
                    DividerOption()
                    ProfileOptionRow(
                        icon = Icons.AutoMirrored.Filled.HelpOutline,
                        title = "Help & Support",
                        subtitle = "FAQ, license guides & contact",
                        onClick = { onShowMessage("Support: support@z1movies.stream") }
                    )
                    DividerOption()
                    ProfileOptionRow(
                        icon = Icons.Filled.Info,
                        title = "About Z1 Movies",
                        subtitle = "v2.4.0 (Build 308) • Cinema Engine",
                        onClick = { onShowMessage("Z1 Movies v2.4.0 • 'Watch. Discover. Enjoy.'") }
                    )
                    DividerOption()
                    ProfileOptionRow(
                        icon = Icons.AutoMirrored.Filled.Logout,
                        title = "Logout / Switch Account",
                        subtitle = "Signed in as ${userProfile.email}",
                        iconTint = LiveRed,
                        titleColor = LiveRed,
                        onClick = onAuthClick
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    iconTint: Color = ElectricBlue,
    titleColor: Color = TextPrimary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = TextTertiary,
            modifier = Modifier.size(13.dp)
        )
    }
}

@Composable
fun DividerOption() {
    HorizontalDivider(
        color = Color(0xFF26324D).copy(alpha = 0.6f),
        thickness = 0.8.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}
