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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun BackendArchitectureView(
    onSyncWithCloud: (String) -> Unit,
    onResetCatalog: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var apiUrl by remember { mutableStateOf("https://api.z1movies.com/v1") }
    var cdnProvider by remember { mutableStateOf("Cloudflare Stream / AWS S3 HLS") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    text = "Backend & Cloud Architecture",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Dynamic streaming & zero-APK update pipeline",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Architecture Flow Diagram Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = CinemaSurfaceElevated,
            border = BorderStroke(1.dp, Color(0xFF26324D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Dynamic Data Pipeline",
                    color = ElectricBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                ArchitectureStep(
                    stepNumber = "1",
                    title = "Admin Panel Input",
                    description = "Admin fills title, poster, and authorized streaming URL (.m3u8 / .mp4), sets 'Publish Live'.",
                    icon = Icons.Filled.Security,
                    color = NeonGold
                )
                ArchitectureStep(
                    stepNumber = "2",
                    title = "Room Local Database (SQLite)",
                    description = "Inserts MovieEntity into Z1Database locally with isPublished=true.",
                    icon = Icons.Filled.Storage,
                    color = ElectricBlue
                )
                ArchitectureStep(
                    stepNumber = "3",
                    title = "Reactive Kotlin Flow",
                    description = "getAllPublishedMovies() emits updated list immediately to ViewModel.",
                    icon = Icons.Filled.Stream,
                    color = Color(0xFFA855F7)
                )
                ArchitectureStep(
                    stepNumber = "4",
                    title = "Z1 Movies App UI",
                    description = "HomeScreen, MoviesScreen, and SearchScreen update reactively. Zero APK update needed!",
                    icon = Icons.Filled.CheckCircle,
                    color = NeonGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Cloud Storage & CDN Info
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = CinemaSurfaceElevated,
            border = BorderStroke(1.dp, Color(0xFF26324D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Cloud Video Streaming Architecture",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Large high-bitrate 4K movies are never bundled inside the Android APK. Instead, authorized cloud storage delivers them dynamically via adaptive bitrate HLS:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                listOf(
                    "Cloudflare Stream" to "Adaptive bitrate HLS (.m3u8), automatic multi-resolution transcoding.",
                    "AWS S3 + CloudFront" to "Encrypted object storage with high-speed global edge distribution.",
                    "BunnyCDN Video" to "Low-cost high-bandwidth video delivery network.",
                    "Public Domain / License CDN" to "Direct HTTPS MP4/HLS feeds for verified open media."
                ).forEach { (provider, desc) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(ElectricBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(provider, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(desc, color = TextTertiary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Remote API Configuration Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = CinemaSurfaceElevated,
            border = BorderStroke(1.dp, Color(0xFF26324D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Remote Server Sync (Optional REST API)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Retrofit interface `Z1ApiService` is pre-configured to synchronize catalog across multiple phones.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                OutlinedTextField(
                    value = apiUrl,
                    onValueChange = { apiUrl = it },
                    label = { Text("Base API Endpoint") },
                    leadingIcon = { Icon(Icons.Filled.Dns, contentDescription = null, tint = ElectricBlue) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = CinemaSurface,
                        unfocusedContainerColor = CinemaSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onSyncWithCloud(apiUrl) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Icon(Icons.Filled.CloudSync, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Synchronize Catalog with Server", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reset Seed Data
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = CinemaSurfaceElevated,
            border = BorderStroke(1.dp, Color(0xFF334155)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Catalog Maintenance", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "Restore all pre-seeded licensed demo movies and categories in the local database.",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )
                OutlinedButton(
                    onClick = onResetCatalog,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF334155))
                ) {
                    Icon(Icons.Filled.RestartAlt, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Reset Catalog to Default Curated Seed", color = TextSecondary)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun ArchitectureStep(
    stepNumber: String,
    title: String,
    description: String,
    icon: ImageVector,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Step $stepNumber: ", color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
            Text(description, color = TextSecondary, fontSize = 11.sp)
        }
    }
}
