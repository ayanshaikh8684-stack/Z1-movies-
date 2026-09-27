package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.VideoFile
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
import com.example.ui.theme.NeonGold
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddEditMovieView(
    existingMovie: Movie? = null,
    onSaveMovie: (Movie, Boolean) -> Unit, // movie, isEditing
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditing = existingMovie != null

    // Form fields
    var title by remember { mutableStateOf(existingMovie?.title ?: "") }
    var tagline by remember { mutableStateOf(existingMovie?.tagline ?: "") }
    var description by remember { mutableStateOf(existingMovie?.description ?: "") }
    var year by remember { mutableStateOf(existingMovie?.year?.toString() ?: "2026") }
    var duration by remember { mutableStateOf(existingMovie?.duration ?: "2h 05m") }
    var durationMinutes by remember { mutableStateOf(existingMovie?.durationMinutes?.toString() ?: "125") }
    var rating by remember { mutableStateOf(existingMovie?.rating?.toString() ?: "4.8") }
    var director by remember { mutableStateOf(existingMovie?.director ?: "") }
    var castText by remember { mutableStateOf(existingMovie?.cast?.joinToString(", ") ?: "") }
    var language by remember { mutableStateOf(existingMovie?.language ?: "English") }
    var quality by remember { mutableStateOf(existingMovie?.quality ?: "4K Ultra HD") }

    // Media & Cloud Streaming URLs
    var posterUrl by remember {
        mutableStateOf(
            existingMovie?.posterUrl
                ?: "https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=80"
        )
    }
    var backdropUrl by remember {
        mutableStateOf(
            existingMovie?.backdropUrl
                ?: "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop&q=80"
        )
    }
    var videoUrl by remember {
        mutableStateOf(
            existingMovie?.videoUrl
                ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        )
    }
    var trailerUrl by remember {
        mutableStateOf(
            existingMovie?.trailerUrl
                ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
        )
    }

    // Genres (Multi-select)
    val availableGenres = listOf(
        "Action", "Sci-Fi", "Drama", "Islamic", "Indian", "Adventure",
        "Crime", "Thriller", "Horror", "Comedy", "Romance", "Animation",
        "Documentary", "Family", "Kids", "Sports", "International"
    )
    var selectedGenres by remember {
        mutableStateOf(
            existingMovie?.genres?.toSet() ?: setOf("Sci-Fi", "Action")
        )
    }

    // Subtitles
    val availableSubtitles = listOf("English", "Hindi", "Urdu", "Arabic", "Spanish", "French")
    var selectedSubtitles by remember {
        mutableStateOf(setOf("English", "Hindi", "Urdu"))
    }

    // Publication and Distribution Flags
    var isPublished by remember { mutableStateOf(existingMovie?.isPublished ?: true) }
    var isFeatured by remember { mutableStateOf(existingMovie?.isFeatured ?: false) }
    var isTrending by remember { mutableStateOf(existingMovie?.isTrending ?: true) }
    var isNewRelease by remember { mutableStateOf(existingMovie?.isNewRelease ?: true) }

    // Rights, Scheduling & Licensing Fields
    var digitalReleaseDate by remember { mutableStateOf(existingMovie?.digitalReleaseDate ?: "") }
    var isScheduledPublication by remember { mutableStateOf(existingMovie?.publishAt != null) }
    var scheduledDateText by remember {
        mutableStateOf(
            if (existingMovie?.publishAt != null) {
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(existingMovie.publishAt))
            } else ""
        )
    }
    var isRightsExpiryActive by remember { mutableStateOf(existingMovie?.rightsExpiryDate != null) }
    var rightsExpiryDateText by remember {
        mutableStateOf(
            if (existingMovie?.rightsExpiryDate != null) {
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(existingMovie.rightsExpiryDate))
            } else ""
        )
    }
    var rightsStartDateText by remember {
        mutableStateOf(
            if (existingMovie?.rightsStartDate != null) {
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(existingMovie.rightsStartDate))
            } else ""
        )
    }
    var subtitleUrl by remember { mutableStateOf(existingMovie?.subtitleUrl ?: "") }
    var rightsProvider by remember { mutableStateOf(existingMovie?.rightsProvider ?: "Authorized Studio Partner") }
    var territory by remember { mutableStateOf(existingMovie?.territory ?: "Global / Worldwide") }
    var isStreamingPermitted by remember { mutableStateOf(existingMovie?.isStreamingPermitted ?: true) }
    var isDownloadPermitted by remember { mutableStateOf(existingMovie?.isDownloadPermitted ?: true) }
    var isAdSupportedPermitted by remember { mutableStateOf(existingMovie?.isAdSupportedPermitted ?: true) }
    var hideWhenExpired by remember { mutableStateOf(existingMovie?.hideWhenExpired ?: false) }

    // Premium Early Access & Release Window Fields
    var isPremiumEarlyAccess by remember { mutableStateOf(existingMovie?.isPremiumEarlyAccess ?: false) }
    var premiumReleaseDateText by remember {
        mutableStateOf(
            if (existingMovie?.premiumReleaseDate != null) {
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(existingMovie.premiumReleaseDate))
            } else ""
        )
    }
    var freeReleaseDateText by remember {
        mutableStateOf(
            if (existingMovie?.freeReleaseDate != null) {
                java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US).format(java.util.Date(existingMovie.freeReleaseDate))
            } else ""
        )
    }
    var autoFreeAfterDate by remember { mutableStateOf(existingMovie?.autoFreeAfterDate ?: true) }
    var premiumDurationDaysText by remember {
        mutableStateOf((existingMovie?.premiumOnlyDurationDays ?: 14).toString())
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Cancel",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = if (isEditing) "Edit Movie Title" else "Add New Licensed Movie",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isPublished) "Will be published live on Z1 Movies app" else "Saved as unpublished draft",
                        color = if (isPublished) NeonGreen else NeonGold,
                        fontSize = 11.sp
                    )
                }
            }

            // Publish status switch at the very top
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (isPublished) "PUBLISHED" else "DRAFT",
                    color = if (isPublished) NeonGreen else NeonGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(6.dp))
                Switch(
                    checked = isPublished,
                    onCheckedChange = { isPublished = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = NeonGreen,
                        uncheckedTrackColor = Color(0xFF334155)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 1: Video & Streaming CDN URLs
        FormSection(title = "1. Authorized Video & Cloud Streaming URLs") {
            Text(
                text = "Stream architecture: Use Cloudflare Stream, AWS S3/CloudFront, or BunnyCDN HLS (.m3u8 / .mp4). Videos are not bundled in the APK.",
                color = TextTertiary,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FormTextField(
                label = "Authorized Movie / Video Stream URL",
                value = videoUrl,
                onValueChange = { videoUrl = it },
                placeholder = "https://cdn.z1movies.com/stream/master.m3u8",
                leadingIcon = Icons.Filled.PlayCircle
            )

            Spacer(modifier = Modifier.height(10.dp))

            FormTextField(
                label = "Trailer Stream URL",
                value = trailerUrl,
                onValueChange = { trailerUrl = it },
                placeholder = "https://cdn.z1movies.com/trailers/trailer.mp4",
                leadingIcon = Icons.Filled.VideoFile
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick preset stream fillers for testing
            Text(
                text = "Quick Sample Streams (Tap to test):",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "Sci-Fi / Big Buck" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                    "Action / Tears" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4",
                    "Cyber / Elephant" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                    "Blazes / Chase" to "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
                ).forEach { (label, url) ->
                    OutlinedButton(
                        onClick = { videoUrl = url },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, if (videoUrl == url) ElectricBlue else Color(0xFF334155)),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(label, color = if (videoUrl == url) ElectricBlue else TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 2: Artwork & Media Uploads
        FormSection(title = "2. Artwork & Poster Uploads") {
            Row(modifier = Modifier.fillMaxWidth()) {
                // Poster Preview
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Poster Preview", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = "Poster Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(width = 72.dp, height = 105.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Backdrop Preview
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                    Text("Backdrop Preview", color = TextSecondary, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    AsyncImage(
                        model = backdropUrl,
                        contentDescription = "Backdrop Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(105.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E293B))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            FormTextField(
                label = "Poster Image URL",
                value = posterUrl,
                onValueChange = { posterUrl = it },
                placeholder = "https://images.unsplash.com/...",
                leadingIcon = Icons.Filled.Image
            )

            Spacer(modifier = Modifier.height(10.dp))

            FormTextField(
                label = "Backdrop Image URL",
                value = backdropUrl,
                onValueChange = { backdropUrl = it },
                placeholder = "https://images.unsplash.com/...",
                leadingIcon = Icons.Filled.Image
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick poster presets
            Text(
                text = "Preset Artwork Palettes (Tap to assign):",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf(
                    "Cyber Neon" to ("https://images.unsplash.com/photo-1578632767115-351597cf2477?w=600&auto=format&fit=crop&q=80" to "https://images.unsplash.com/photo-1509198397868-475647b2a1e5?w=1200&auto=format&fit=crop&q=80"),
                    "Deep Space" to ("https://images.unsplash.com/photo-1451187580459-43490279c0fa?w=600&auto=format&fit=crop&q=80" to "https://images.unsplash.com/photo-1446776811953-b23d57bd21aa?w=1200&auto=format&fit=crop&q=80"),
                    "Desert War" to ("https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=600&auto=format&fit=crop&q=80" to "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=1200&auto=format&fit=crop&q=80"),
                    "Neo-Noir" to ("https://images.unsplash.com/photo-1514565131-fce0801e5785?w=600&auto=format&fit=crop&q=80" to "https://images.unsplash.com/photo-1519501025264-65ba15a82390?w=1200&auto=format&fit=crop&q=80"),
                    "Alpine" to ("https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?w=600&auto=format&fit=crop&q=80" to "https://images.unsplash.com/photo-1486870591958-9b9d0d1dda99?w=1200&auto=format&fit=crop&q=80")
                ).forEach { (name, urls) ->
                    OutlinedButton(
                        onClick = {
                            posterUrl = urls.first
                            backdropUrl = urls.second
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFF334155)),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text(name, color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 3: Content Details
        FormSection(title = "3. Core Information & Metadata") {
            FormTextField(
                label = "Movie Title *",
                value = title,
                onValueChange = { title = it },
                placeholder = "e.g. Apex Vanguard"
            )

            Spacer(modifier = Modifier.height(10.dp))

            FormTextField(
                label = "Tagline",
                value = tagline,
                onValueChange = { tagline = it },
                placeholder = "e.g. Beyond the frontiers of reality."
            )

            Spacer(modifier = Modifier.height(10.dp))

            FormTextField(
                label = "Description / Synopsis *",
                value = description,
                onValueChange = { description = it },
                placeholder = "Full movie synopsis...",
                singleLine = false,
                lines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FormTextField(
                    label = "Release Year",
                    value = year,
                    onValueChange = { year = it },
                    placeholder = "2026",
                    modifier = Modifier.weight(1f)
                )
                FormTextField(
                    label = "Duration (Text)",
                    value = duration,
                    onValueChange = { duration = it },
                    placeholder = "2h 10m",
                    modifier = Modifier.weight(1f)
                )
                FormTextField(
                    label = "Rating (0-5)",
                    value = rating,
                    onValueChange = { rating = it },
                    placeholder = "4.8",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            FormTextField(
                label = "Director",
                value = director,
                onValueChange = { director = it },
                placeholder = "e.g. Christopher Nolan / Denis Villeneuve"
            )

            Spacer(modifier = Modifier.height(10.dp))

            FormTextField(
                label = "Cast (comma-separated)",
                value = castText,
                onValueChange = { castText = it },
                placeholder = "e.g. Marcus Croft, Dr. Aria, Julian Vance"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 4: Genres & Classification
        FormSection(title = "4. Genres, Languages & Audio") {
            Text("Select Genres (Tap to toggle):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                availableGenres.forEach { genre ->
                    val isSelected = selectedGenres.contains(genre)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            selectedGenres = if (isSelected) {
                                if (selectedGenres.size > 1) selectedGenres - genre else selectedGenres
                            } else {
                                selectedGenres + genre
                            }
                        },
                        label = { Text(genre, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue.copy(alpha = 0.2f),
                            selectedLabelColor = ElectricBlue
                        ),
                        border = BorderStroke(1.dp, if (isSelected) ElectricBlue else Color(0xFF334155))
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Language & Quality selector
            Text("Audio Language:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("English", "Hindi", "Urdu", "Arabic", "Spanish", "French", "Japanese", "Korean").forEach { lang ->
                    FilterChip(
                        selected = language == lang,
                        onClick = { language = lang },
                        label = { Text(lang, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue.copy(alpha = 0.2f),
                            selectedLabelColor = ElectricBlue
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Video Quality:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("4K Ultra HD", "1080p FHD", "720p HD").forEach { q ->
                    FilterChip(
                        selected = quality == q,
                        onClick = { quality = q },
                        label = { Text(q, fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NeonGold.copy(alpha = 0.2f),
                            selectedLabelColor = NeonGold
                        ),
                        border = BorderStroke(1.dp, if (quality == q) NeonGold else Color(0xFF334155))
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Subtitles Included:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                availableSubtitles.forEach { sub ->
                    val isSubSelected = selectedSubtitles.contains(sub)
                    FilterChip(
                        selected = isSubSelected,
                        onClick = {
                            selectedSubtitles = if (isSubSelected) selectedSubtitles - sub else selectedSubtitles + sub
                        },
                        label = { Text(sub, fontSize = 11.sp) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 5: Rights, Scheduling & Licensing Dates
        FormSection(title = "5. Rights Management & Release Scheduling") {
            Text(
                text = "Set digital rights dates, automatic expiry, and scheduled future release.",
                color = TextTertiary,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            FormTextField(
                label = "Digital Release Date (e.g. 2026-06-15)",
                value = digitalReleaseDate,
                onValueChange = { digitalReleaseDate = it },
                placeholder = "2026-06-15"
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Scheduled Publication Toggle & Input
            ToggleOptionRow(
                title = "Schedule Future Release",
                subtitle = "Will remain hidden from users until scheduled release timestamp",
                checked = isScheduledPublication,
                onCheckedChange = { isScheduledPublication = it }
            )

            if (isScheduledPublication) {
                Spacer(modifier = Modifier.height(8.dp))
                FormTextField(
                    label = "Publish At Date (YYYY-MM-DD)",
                    value = scheduledDateText,
                    onValueChange = { scheduledDateText = it },
                    placeholder = "2026-10-01"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Rights Expiry Toggle & Input
            ToggleOptionRow(
                title = "Set Content Rights Expiry",
                subtitle = "Automatically revokes streaming rights and hides movie when expired",
                checked = isRightsExpiryActive,
                onCheckedChange = { isRightsExpiryActive = it }
            )

            if (isRightsExpiryActive) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FormTextField(
                        label = "Rights Start (YYYY-MM-DD)",
                        value = rightsStartDateText,
                        onValueChange = { rightsStartDateText = it },
                        placeholder = "2026-01-01",
                        modifier = Modifier.weight(1f)
                    )
                    FormTextField(
                        label = "Rights Expiry (YYYY-MM-DD)",
                        value = rightsExpiryDateText,
                        onValueChange = { rightsExpiryDateText = it },
                        placeholder = "2027-01-01",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FormTextField(
                    label = "Rights Provider",
                    value = rightsProvider,
                    onValueChange = { rightsProvider = it },
                    placeholder = "Sony, Warner, Public Domain, etc.",
                    modifier = Modifier.weight(1f)
                )
                FormTextField(
                    label = "Territory / Region",
                    value = territory,
                    onValueChange = { territory = it },
                    placeholder = "Global / US / IN / PK",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            ToggleOptionRow(
                title = "Streaming Permission",
                subtitle = "Allow VOD online streaming playback",
                checked = isStreamingPermitted,
                onCheckedChange = { isStreamingPermitted = it }
            )

            ToggleOptionRow(
                title = "Offline Download Permission",
                subtitle = "Permit offline saving on user device",
                checked = isDownloadPermitted,
                onCheckedChange = { isDownloadPermitted = it }
            )

            ToggleOptionRow(
                title = "Ad-Supported Playback",
                subtitle = "Allow ad placement or AVOD monetization",
                checked = isAdSupportedPermitted,
                onCheckedChange = { isAdSupportedPermitted = it }
            )

            ToggleOptionRow(
                title = "Hide Content When Expired",
                subtitle = "Completely remove from user browsing upon expiry",
                checked = hideWhenExpired,
                onCheckedChange = { hideWhenExpired = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            FormTextField(
                label = "Subtitle Track URL (.vtt / .srt)",
                value = subtitleUrl,
                onValueChange = { subtitleUrl = it },
                placeholder = "https://cdn.z1movies.com/subtitles/en.vtt"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 6: Premium Early Access & Release Window
        FormSection(title = "6. Premium Early Access / Release Window") {
            Text(
                text = "Release to Premium subscribers first, then automatically or manually unlock for free users.",
                color = TextTertiary,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            ToggleOptionRow(
                title = "Premium Early Access",
                subtitle = "Only active Premium subscribers can watch during the early access window",
                checked = isPremiumEarlyAccess,
                onCheckedChange = { isPremiumEarlyAccess = it }
            )

            if (isPremiumEarlyAccess) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    FormTextField(
                        label = "Premium Release (YYYY-MM-DD)",
                        value = premiumReleaseDateText,
                        onValueChange = { premiumReleaseDateText = it },
                        placeholder = "2026-04-01",
                        modifier = Modifier.weight(1f)
                    )
                    FormTextField(
                        label = "Free Release (YYYY-MM-DD)",
                        value = freeReleaseDateText,
                        onValueChange = { freeReleaseDateText = it },
                        placeholder = "2026-04-15",
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                FormTextField(
                    label = "Early Access Period (Days)",
                    value = premiumDurationDaysText,
                    onValueChange = { premiumDurationDaysText = it },
                    placeholder = "14"
                )

                Spacer(modifier = Modifier.height(8.dp))

                ToggleOptionRow(
                    title = "Auto-Unlock for Free Users",
                    subtitle = "Automatically make available to free users once Free Release Date is reached",
                    checked = autoFreeAfterDate,
                    onCheckedChange = { autoFreeAfterDate = it }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SECTION 7: Publishing & Distribution Flags
        FormSection(title = "7. Publishing & App Placement") {
            // Big Publish Toggle Card
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isPublished) NeonGreen.copy(alpha = 0.1f) else Color(0xFF1E293B),
                border = BorderStroke(1.dp, if (isPublished) NeonGreen else Color(0xFF334155)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isPublished) "Publish Live on Z1 Movies App" else "Save as Draft (Hidden)",
                            color = if (isPublished) NeonGreen else TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isPublished)
                                "Visible immediately to all app users in Home, Movies, and Search without an APK update."
                            else
                                "Saved only in Admin database for review before releasing.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = isPublished,
                        onCheckedChange = { isPublished = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonGreen
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            ToggleOptionRow(
                title = "Featured on Hero Banner",
                subtitle = "Spotlighted in the large rotating top carousel on the Home screen",
                checked = isFeatured,
                onCheckedChange = { isFeatured = it }
            )

            ToggleOptionRow(
                title = "Trending Now",
                subtitle = "Appears in the Trending Top 10 cinema rails",
                checked = isTrending,
                onCheckedChange = { isTrending = it }
            )

            ToggleOptionRow(
                title = "New Release Badge",
                subtitle = "Tagged with vibrant green 'NEW' badge across catalog",
                checked = isNewRelease,
                onCheckedChange = { isNewRelease = it }
            )
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF450A0A),
                border = BorderStroke(1.dp, Color(0xFFEF4444)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = errorMessage ?: "",
                    color = Color(0xFFFCA5A5),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Button(
            onClick = {
                if (title.isBlank()) {
                    errorMessage = "Please provide a movie title."
                    return@Button
                }
                if (description.isBlank()) {
                    errorMessage = "Please enter a movie description."
                    return@Button
                }

                val castList = castText.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                val parsedYear = year.toIntOrNull() ?: 2026
                val parsedDurationMinutes = durationMinutes.toIntOrNull() ?: 120
                val parsedRating = rating.toFloatOrNull() ?: 4.8f

                val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                val parsedPublishAt = if (isScheduledPublication && scheduledDateText.isNotBlank()) {
                    try { dateFormat.parse(scheduledDateText.trim())?.time } catch (e: Exception) { null }
                } else null

                val parsedRightsStart = if (isRightsExpiryActive && rightsStartDateText.isNotBlank()) {
                    try { dateFormat.parse(rightsStartDateText.trim())?.time } catch (e: Exception) { null }
                } else null

                val parsedRightsExpiry = if (isRightsExpiryActive && rightsExpiryDateText.isNotBlank()) {
                    try { dateFormat.parse(rightsExpiryDateText.trim())?.time } catch (e: Exception) { null }
                } else null

                val parsedPremiumRelease = if (isPremiumEarlyAccess && premiumReleaseDateText.isNotBlank()) {
                    try { dateFormat.parse(premiumReleaseDateText.trim())?.time } catch (e: Exception) { null }
                } else null

                val parsedFreeRelease = if (isPremiumEarlyAccess && freeReleaseDateText.isNotBlank()) {
                    try { dateFormat.parse(freeReleaseDateText.trim())?.time } catch (e: Exception) { null }
                } else null

                val parsedPremiumDurationDays = premiumDurationDaysText.toIntOrNull() ?: 14

                val movieToSave = Movie(
                    id = existingMovie?.id ?: "m_${System.currentTimeMillis()}",
                    title = title.trim(),
                    tagline = tagline.trim().ifBlank { "A cinematic marvel." },
                    description = description.trim(),
                    posterUrl = posterUrl.trim(),
                    backdropUrl = backdropUrl.trim(),
                    videoUrl = videoUrl.trim(),
                    trailerUrl = trailerUrl.trim(),
                    year = parsedYear,
                    duration = duration.trim().ifBlank { "2h 00m" },
                    durationMinutes = parsedDurationMinutes,
                    rating = parsedRating,
                    genres = selectedGenres.toList(),
                    language = language,
                    director = director.trim().ifBlank { "Z1 Studio Productions" },
                    cast = if (castList.isNotEmpty()) castList else listOf("Lead Cast", "Supporting Actor"),
                    quality = quality,
                    isFeatured = isFeatured,
                    isTrending = isTrending,
                    isNewRelease = isNewRelease,
                    isPublished = isPublished,
                    digitalReleaseDate = digitalReleaseDate.trim(),
                    rightsStartDate = parsedRightsStart,
                    rightsExpiryDate = parsedRightsExpiry,
                    publishAt = parsedPublishAt,
                    subtitleUrl = subtitleUrl.trim(),
                    rightsProvider = rightsProvider.trim().ifBlank { "Authorized Studio Partner" },
                    territory = territory.trim().ifBlank { "Global / Worldwide" },
                    isStreamingPermitted = isStreamingPermitted,
                    isDownloadPermitted = isDownloadPermitted,
                    isAdSupportedPermitted = isAdSupportedPermitted,
                    hideWhenExpired = hideWhenExpired,
                    isPremiumEarlyAccess = isPremiumEarlyAccess,
                    premiumReleaseDate = parsedPremiumRelease,
                    freeReleaseDate = parsedFreeRelease,
                    autoFreeAfterDate = autoFreeAfterDate,
                    premiumOnlyDurationDays = parsedPremiumDurationDays,
                    createdAt = existingMovie?.createdAt ?: System.currentTimeMillis(),
                    updatedAt = System.currentTimeMillis()
                )

                onSaveMovie(movieToSave, isEditing)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isPublished) ElectricBlue else NeonGold
            )
        ) {
            Icon(
                imageVector = if (isPublished) Icons.Filled.Publish else Icons.Filled.Save,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when {
                    isEditing && isPublished -> "Update & Publish Live to App"
                    isEditing -> "Update Draft"
                    isPublished -> "Publish Movie to Z1 App Now"
                    else -> "Save as Unpublished Draft"
                },
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Text("Cancel", color = TextSecondary, fontSize = 14.sp)
        }
    }
}

@Composable
private fun FormSection(
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = CinemaSurfaceElevated,
        border = BorderStroke(1.dp, Color(0xFF26324D)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                color = ElectricBlue,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
private fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    singleLine: Boolean = true,
    lines: Int = 1,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = { Text(placeholder, color = TextTertiary) },
        leadingIcon = if (leadingIcon != null) {
            { Icon(leadingIcon, contentDescription = null, tint = ElectricBlue) }
        } else null,
        singleLine = singleLine,
        minLines = lines,
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedBorderColor = ElectricBlue,
            unfocusedBorderColor = Color(0xFF334155),
            focusedContainerColor = CinemaSurface,
            unfocusedContainerColor = CinemaSurface
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.fillMaxWidth()
    )
}

@Composable
private fun ToggleOptionRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = TextSecondary, fontSize = 11.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ElectricBlue
            )
        )
    }
}
