package com.example.ui.screens.notifications

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationType
import com.example.ui.components.EmptyStateView
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.RatingGold
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun NotificationsScreen(
    notifications: List<NotificationItem>,
    onDismissNotification: (String) -> Unit,
    onNotificationClick: (NotificationItem) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
                    text = "Notifications",
                    color = TextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${notifications.size} alerts & updates",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        if (notifications.isEmpty()) {
            EmptyStateView(
                icon = Icons.Filled.Notifications,
                title = "No new notifications",
                description = "You're all caught up! New movie releases, recommendations, and download status alerts will appear here.",
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { item ->
                    NotificationCard(
                        item = item,
                        onClick = { onNotificationClick(item) },
                        onDismiss = { onDismissNotification(item.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationCard(
    item: NotificationItem,
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = CinemaSurfaceElevated,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isRead) Color(0xFF26324D) else ElectricBlue.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Category Icon Badge
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        when (item.type) {
                            NotificationType.NEW_RELEASE -> ElectricBlue.copy(alpha = 0.15f)
                            NotificationType.DOWNLOAD_COMPLETED -> SuccessGreen.copy(alpha = 0.15f)
                            NotificationType.RECOMMENDED -> RatingGold.copy(alpha = 0.15f)
                            NotificationType.ACCOUNT -> Color(0xFF8B5CF6).copy(alpha = 0.15f)
                            NotificationType.ANNOUNCEMENT -> Color(0xFFF59E0B).copy(alpha = 0.15f)
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (item.type) {
                        NotificationType.NEW_RELEASE -> Icons.Filled.Movie
                        NotificationType.DOWNLOAD_COMPLETED -> Icons.Filled.DownloadDone
                        NotificationType.RECOMMENDED -> Icons.Filled.Star
                        NotificationType.ACCOUNT -> Icons.Filled.Person
                        NotificationType.ANNOUNCEMENT -> Icons.Filled.Campaign
                    },
                    contentDescription = null,
                    tint = when (item.type) {
                        NotificationType.NEW_RELEASE -> ElectricBlue
                        NotificationType.DOWNLOAD_COMPLETED -> SuccessGreen
                        NotificationType.RECOMMENDED -> RatingGold
                        NotificationType.ACCOUNT -> Color(0xFF8B5CF6)
                        NotificationType.ANNOUNCEMENT -> Color(0xFFF59E0B)
                    },
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = item.message,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.timestamp,
                    color = TextTertiary,
                    fontSize = 10.sp
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(28.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Dismiss",
                    tint = TextTertiary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
