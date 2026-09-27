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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EpgProgram
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AddEditLiveChannelView(
    existingChannel: LiveChannel? = null,
    onSaveChannel: (LiveChannel) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isEditing = existingChannel != null

    var name by remember { mutableStateOf(existingChannel?.name ?: "") }
    var logoUrl by remember { mutableStateOf(existingChannel?.logoUrl ?: "https://images.unsplash.com/photo-1585829365295-ab7cd400c167?w=300&auto=format&fit=crop&q=80") }
    var description by remember { mutableStateOf(existingChannel?.description ?: "") }
    var category by remember { mutableStateOf(existingChannel?.category ?: "News") }
    var language by remember { mutableStateOf(existingChannel?.language ?: "English") }
    var country by remember { mutableStateOf(existingChannel?.country ?: "Global") }
    var streamUrl by remember { mutableStateOf(existingChannel?.streamUrl ?: "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4") }
    var epgSourceUrl by remember { mutableStateOf(existingChannel?.epgSourceUrl ?: "") }
    var currentProgram by remember { mutableStateOf(existingChannel?.currentProgram ?: "Live Broadcast") }
    var nextProgram by remember { mutableStateOf(existingChannel?.nextProgram ?: "Upcoming Show") }
    var currentProgramTime by remember { mutableStateOf(existingChannel?.currentProgramTime ?: "Live Now") }
    var isFeatured by remember { mutableStateOf(existingChannel?.isFeatured ?: false) }
    var isPublished by remember { mutableStateOf(existingChannel?.isPublished ?: true) }
    var isEnabled by remember { mutableStateOf(existingChannel?.isEnabled ?: true) }
    var isPremiumOnly by remember { mutableStateOf(existingChannel?.isPremiumOnly ?: false) }
    var displayOrder by remember { mutableStateOf(existingChannel?.displayOrder?.toString() ?: "1") }

    // Rights Management (Mandatory for legal compliance)
    var rightsProvider by remember { mutableStateOf(existingChannel?.rightsProvider ?: "Authorized Broadcaster Syndicate") }
    var territory by remember { mutableStateOf(existingChannel?.territory ?: "Worldwide / Open Digital Stream") }

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val defaultStartDate = remember {
        existingChannel?.rightsStartDate?.let { dateFormat.format(Date(it)) } ?: dateFormat.format(Date())
    }
    val defaultExpiryDate = remember {
        existingChannel?.rightsExpiryDate?.let { dateFormat.format(Date(it)) } ?: dateFormat.format(Date(System.currentTimeMillis() + (365L * 24 * 3600 * 1000L)))
    }

    var rightsStartDateStr by remember { mutableStateOf(defaultStartDate) }
    var rightsExpiryDateStr by remember { mutableStateOf(defaultExpiryDate) }
    var isStreamingPermitted by remember { mutableStateOf(existingChannel?.isStreamingPermitted ?: true) }
    var isAdSupportedPermitted by remember { mutableStateOf(existingChannel?.isAdSupportedPermitted ?: true) }
    var isPremiumAccessPermitted by remember { mutableStateOf(existingChannel?.isPremiumAccessPermitted ?: true) }

    // Schedule items
    val scheduleItems = remember {
        mutableStateListOf<EpgProgram>().apply {
            if (existingChannel?.schedule?.isNotEmpty() == true) {
                addAll(existingChannel.schedule)
            } else {
                add(EpgProgram("p_init_1", "Morning Highlights", "Comprehensive morning program", "08:00", "10:00", true))
                add(EpgProgram("p_init_2", "Daytime Magazine", "Afternoon feature stories", "10:00", "12:00", false))
            }
        }
    }

    var newProgTitle by remember { mutableStateOf("") }
    var newProgStart by remember { mutableStateOf("12:00") }
    var newProgEnd by remember { mutableStateOf("14:00") }

    var validationError by remember { mutableStateOf<String?>(null) }

    fun validateAndSave() {
        if (name.isBlank()) {
            validationError = "Channel Name is required."
            return
        }
        if (streamUrl.isBlank()) {
            validationError = "Authorized Stream URL / Feed is required."
            return
        }
        if (rightsProvider.isBlank()) {
            validationError = "Rights Provider is legally mandatory before publishing."
            return
        }

        val startMs = try { dateFormat.parse(rightsStartDateStr)?.time } catch (e: Exception) { System.currentTimeMillis() }
        val expiryMs = try { dateFormat.parse(rightsExpiryDateStr)?.time } catch (e: Exception) { System.currentTimeMillis() + (365L * 24 * 3600 * 1000L) }

        val channel = LiveChannel(
            id = existingChannel?.id ?: "ch_${System.currentTimeMillis()}",
            name = name.trim(),
            logoUrl = logoUrl.trim(),
            description = description.trim(),
            category = category.trim(),
            language = language.trim(),
            country = country.trim(),
            streamUrl = streamUrl.trim(),
            epgSourceUrl = epgSourceUrl.trim(),
            currentProgram = currentProgram.trim(),
            nextProgram = nextProgram.trim(),
            currentProgramTime = currentProgramTime.trim(),
            isFeatured = isFeatured,
            isPublished = isPublished,
            isEnabled = isEnabled,
            isPremiumOnly = isPremiumOnly,
            displayOrder = displayOrder.toIntOrNull() ?: 1,
            rightsProvider = rightsProvider.trim(),
            territory = territory.trim(),
            rightsStartDate = startMs,
            rightsExpiryDate = expiryMs,
            isStreamingPermitted = isStreamingPermitted,
            isAdSupportedPermitted = isAdSupportedPermitted,
            isPremiumAccessPermitted = isPremiumAccessPermitted,
            schedule = scheduleItems.toList()
        )

        onSaveChannel(channel)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onCancel,
                modifier = Modifier
                    .size(40.dp)
                    .background(CinemaSurfaceElevated, CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Cancel", tint = TextPrimary)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = if (isEditing) "Edit Live Channel" else "Add Authorized TV Feed",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Button(
                onClick = { validateAndSave() },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.Filled.Check, contentDescription = null, tint = CinemaBlack, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Feed", color = CinemaBlack, fontWeight = FontWeight.Bold)
            }
        }

        if (validationError != null) {
            Surface(
                color = LiveRed.copy(alpha = 0.15f),
                shape = RoundedCornerShape(8.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, LiveRed),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(12.dp)
                ) {
                    Icon(Icons.Filled.Warning, contentDescription = null, tint = LiveRed, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(validationError ?: "", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Section 1: Channel Basic Info
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CinemaSurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("1. Channel Details", color = ElectricBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it; validationError = null },
                            label = { Text("Channel Name *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = logoUrl,
                            onValueChange = { logoUrl = it },
                            label = { Text("Logo / Banner URL") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Channel Description") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        // Category & Display Order Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = { category = it },
                                label = { Text("Category (News, Sports...)") },
                                modifier = Modifier.weight(1.5f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricBlue,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            OutlinedTextField(
                                value = displayOrder,
                                onValueChange = { displayOrder = it },
                                label = { Text("Order") },
                                modifier = Modifier.weight(0.8f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricBlue,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }

                        // Language & Country Row
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = language,
                                onValueChange = { language = it },
                                label = { Text("Language") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricBlue,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            OutlinedTextField(
                                value = country,
                                onValueChange = { country = it },
                                label = { Text("Country") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = ElectricBlue,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Section 2: Authorized Stream Feed
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CinemaSurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("2. Authorized Stream Feed", color = ElectricBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                        OutlinedTextField(
                            value = streamUrl,
                            onValueChange = { streamUrl = it; validationError = null },
                            label = { Text("Authorized Stream URL / Feed *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = epgSourceUrl,
                            onValueChange = { epgSourceUrl = it },
                            label = { Text("EPG Source Feed URL (XMLTV / JSON - Optional)") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        // Current Program info
                        OutlinedTextField(
                            value = currentProgram,
                            onValueChange = { currentProgram = it },
                            label = { Text("Current On-Air Program") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = nextProgram,
                            onValueChange = { nextProgram = it },
                            label = { Text("Upcoming Program") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )
                    }
                }
            }

            // Section 3: Legal Rights & Licensing Management (CRITICAL)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CinemaSurfaceElevated),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NeonGold.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Shield, contentDescription = null, tint = NeonGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("3. Rights Management & Legal Licensing", color = NeonGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }

                        Text(
                            text = "Streaming rights are required before publishing. Expired feeds are automatically hidden from regular users.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        OutlinedTextField(
                            value = rightsProvider,
                            onValueChange = { rightsProvider = it; validationError = null },
                            label = { Text("Rights Provider / Broadcaster *") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGold,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        OutlinedTextField(
                            value = territory,
                            onValueChange = { territory = it },
                            label = { Text("Authorized Territory") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonGold,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = rightsStartDateStr,
                                onValueChange = { rightsStartDateStr = it },
                                label = { Text("Rights Start (YYYY-MM-DD)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonGold,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )

                            OutlinedTextField(
                                value = rightsExpiryDateStr,
                                onValueChange = { rightsExpiryDateStr = it },
                                label = { Text("Rights Expiry (YYYY-MM-DD)") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = NeonGold,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                )
                            )
                        }
                    }
                }
            }

            // Section 4: Access Tiers & Visibility Switches
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CinemaSurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("4. Access & Publishing", color = ElectricBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)

                        // Published
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Published in Live TV", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("Visible to audience in live catalog", color = TextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = isPublished,
                                onCheckedChange = { isPublished = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = ElectricBlue)
                            )
                        }

                        // Enabled
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Feed Active (Enabled)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("Disable to temporarily take channel off-air", color = TextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = isEnabled,
                                onCheckedChange = { isEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF10B981))
                            )
                        }

                        // Featured
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Featured Channel", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("Highlighted in top Live Now landing carousel", color = TextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = isFeatured,
                                onCheckedChange = { isFeatured = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = NeonGold)
                            )
                        }

                        // Premium Only
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Premium Channel Access", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text("Only active Z1 Cinema Pro subscribers can watch", color = TextSecondary, fontSize = 11.sp)
                            }
                            Switch(
                                checked = isPremiumOnly,
                                onCheckedChange = { isPremiumOnly = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = NeonGold)
                            )
                        }
                    }
                }
            }

            // Section 5: Program Schedule (EPG)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CinemaSurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("5. Electronic Program Guide (EPG)", color = ElectricBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Manage today's schedule for this channel:", color = TextSecondary, fontSize = 11.sp)

                        Spacer(modifier = Modifier.height(10.dp))

                        // Schedule items list
                        scheduleItems.forEachIndexed { index, prog ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .background(Color(0xFF141A28), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${prog.startTime} - ${prog.endTime}", color = ElectricBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(prog.title, color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                                IconButton(
                                    onClick = { scheduleItems.removeAt(index) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = LiveRed, modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Add new schedule slot
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            OutlinedTextField(
                                value = newProgStart,
                                onValueChange = { newProgStart = it },
                                label = { Text("Start") },
                                modifier = Modifier.width(70.dp),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = newProgEnd,
                                onValueChange = { newProgEnd = it },
                                label = { Text("End") },
                                modifier = Modifier.width(70.dp),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = newProgTitle,
                                onValueChange = { newProgTitle = it },
                                label = { Text("Program Title") },
                                modifier = Modifier.weight(1f),
                                singleLine = true
                            )
                            IconButton(
                                onClick = {
                                    if (newProgTitle.isNotBlank()) {
                                        scheduleItems.add(
                                            EpgProgram(
                                                id = "p_${System.currentTimeMillis()}",
                                                title = newProgTitle.trim(),
                                                startTime = newProgStart.trim(),
                                                endTime = newProgEnd.trim()
                                            )
                                        )
                                        newProgTitle = ""
                                    }
                                },
                                modifier = Modifier
                                    .background(ElectricBlue, RoundedCornerShape(8.dp))
                                    .size(44.dp)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = "Add slot", tint = CinemaBlack)
                            }
                        }
                    }
                }
            }

            item {
                Button(
                    onClick = { validateAndSave() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Filled.Check, contentDescription = null, tint = CinemaBlack, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save Live Channel", color = CinemaBlack, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
