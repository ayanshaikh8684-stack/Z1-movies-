package com.example.ui.screens.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AdminLoginView(
    onLoginSubmit: (String, String) -> Unit,
    onQuickRoleSelect: (UserRole) -> Unit,
    onBackToApp: () -> Unit,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("admin@z1movies.com") }
    var password by remember { mutableStateOf("admin123") }
    var selectedRole by remember { mutableStateOf(UserRole.SUPER_ADMIN) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Back Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackToApp) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to App",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Z1 Movies Cinema Admin",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Shield / Security Emblem
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.AdminPanelSettings,
                contentDescription = null,
                tint = NeonGold,
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Content Management Portal",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Authorized access for licensed titles & cloud streaming",
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Role-Based Access Quick Switcher (For immediate testing)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CinemaSurfaceElevated,
            border = BorderStroke(1.dp, Color(0xFF26324D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Security,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Role-Based Access Control (RBAC)",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "Select an authorized role or test with predefined access credentials:",
                    color = TextTertiary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 4.dp, bottom = 10.dp)
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        UserRole.SUPER_ADMIN to "Super Admin (Full)",
                        UserRole.ADMIN to "Admin (Publish)",
                        UserRole.EDITOR to "Editor (Drafts)",
                        UserRole.USER to "Standard User"
                    ).forEach { (role, label) ->
                        val isSelected = selectedRole == role
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedRole = role
                                when (role) {
                                    UserRole.SUPER_ADMIN -> {
                                        username = "superadmin@z1movies.com"
                                        password = "superadmin123"
                                    }
                                    UserRole.ADMIN -> {
                                        username = "admin@z1movies.com"
                                        password = "admin123"
                                    }
                                    UserRole.EDITOR -> {
                                        username = "editor@z1movies.com"
                                        password = "editor123"
                                    }
                                    UserRole.USER -> {
                                        username = "user@z1movies.com"
                                        password = "user123"
                                    }
                                }
                                onQuickRoleSelect(role)
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = when (role) {
                                    UserRole.SUPER_ADMIN -> NeonGold.copy(alpha = 0.2f)
                                    UserRole.ADMIN -> ElectricBlue.copy(alpha = 0.2f)
                                    UserRole.EDITOR -> Color(0xFF10B981).copy(alpha = 0.2f)
                                    UserRole.USER -> Color.White.copy(alpha = 0.1f)
                                },
                                selectedLabelColor = when (role) {
                                    UserRole.SUPER_ADMIN -> NeonGold
                                    UserRole.ADMIN -> ElectricBlue
                                    UserRole.EDITOR -> Color(0xFF10B981)
                                    UserRole.USER -> TextPrimary
                                }
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) {
                                    when (role) {
                                        UserRole.SUPER_ADMIN -> NeonGold
                                        UserRole.ADMIN -> ElectricBlue
                                        UserRole.EDITOR -> Color(0xFF10B981)
                                        UserRole.USER -> Color.White.copy(alpha = 0.3f)
                                    }
                                } else Color(0xFF334155)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Permission preview box
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "Granted Permissions for ${selectedRole.name}:",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = if (selectedRole.canAddMovie()) NeonGreen else Color.Gray,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Add / Upload movies: ${selectedRole.canAddMovie()}", color = TextSecondary, fontSize = 11.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = if (selectedRole.canPublishMovie()) NeonGreen else Color.Gray,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Publish Live to App: ${selectedRole.canPublishMovie()}", color = TextSecondary, fontSize = 11.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = if (selectedRole.canDeleteMovie()) NeonGreen else Color.Gray,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Delete Catalog Titles: ${selectedRole.canDeleteMovie()}", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Input Credentials Form
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CinemaSurfaceElevated,
            border = BorderStroke(1.dp, Color(0xFF26324D)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Admin Credentials",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Email / Username") },
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = null, tint = ElectricBlue)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = CinemaSurface,
                        unfocusedContainerColor = CinemaSurface
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password / Secret Key") },
                    visualTransformation = PasswordVisualTransformation(),
                    leadingIcon = {
                        Icon(Icons.Filled.Key, contentDescription = null, tint = ElectricBlue)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = Color(0xFF334155),
                        focusedContainerColor = CinemaSurface,
                        unfocusedContainerColor = CinemaSurface
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { onLoginSubmit(username, password) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sign In to Admin Portal",
                        color = Color.Black,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onBackToApp,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF334155))
        ) {
            Text(
                text = "Cancel & Return to Streaming App",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
    }
}
