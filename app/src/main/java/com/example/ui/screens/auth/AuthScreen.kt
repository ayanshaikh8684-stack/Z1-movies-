package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CinemaBlack
import com.example.ui.theme.CinemaDarkBackground
import com.example.ui.theme.CinemaSurfaceElevated
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

enum class AuthMode {
    LOGIN,
    SIGN_UP,
    FORGOT_PASSWORD,
    OTP_VERIFICATION
}

@Composable
fun AuthScreen(
    onDismiss: () -> Unit,
    onSuccessLogin: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(AuthMode.LOGIN) }
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinemaDarkBackground)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        // Back Button
        IconButton(
            onClick = {
                if (mode != AuthMode.LOGIN) {
                    mode = AuthMode.LOGIN
                } else {
                    onDismiss()
                }
            },
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

        Spacer(modifier = Modifier.height(24.dp))

        // App Branding Logo
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(ElectricBlue, Color(0xFF7C4DFF))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = null,
                    tint = CinemaBlack,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Z1",
                    color = ElectricBlue,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = " MOVIES",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dynamic Header according to Mode
        when (mode) {
            AuthMode.LOGIN -> {
                Text("Welcome Back", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Sign in to continue watching licensed movies", color = TextSecondary, fontSize = 13.sp)
            }
            AuthMode.SIGN_UP -> {
                Text("Create Account", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Join Z1 Movies to save watchlists & sync across devices", color = TextSecondary, fontSize = 13.sp)
            }
            AuthMode.FORGOT_PASSWORD -> {
                Text("Reset Password", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Enter your email or mobile number to receive a verification OTP", color = TextSecondary, fontSize = 13.sp)
            }
            AuthMode.OTP_VERIFICATION -> {
                Text("Verify OTP", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Enter the 4-digit code sent to $emailOrPhone", color = TextSecondary, fontSize = 13.sp)
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Forms based on Mode
        if (mode == AuthMode.SIGN_UP) {
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = ElectricBlue) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = authTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (mode != AuthMode.OTP_VERIFICATION) {
            OutlinedTextField(
                value = emailOrPhone,
                onValueChange = { emailOrPhone = it },
                label = { Text("Email or Mobile Number") },
                leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = ElectricBlue) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = authTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (mode == AuthMode.LOGIN || mode == AuthMode.SIGN_UP) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = ElectricBlue) },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = authTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (mode == AuthMode.OTP_VERIFICATION) {
            OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 4) otpCode = it },
                label = { Text("4-Digit OTP Code") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = authTextFieldColors(),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (mode == AuthMode.LOGIN) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { mode = AuthMode.FORGOT_PASSWORD }) {
                    Text("Forgot Password?", color = ElectricBlue, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Main Submit Button
        Button(
            onClick = {
                when (mode) {
                    AuthMode.LOGIN -> onSuccessLogin(emailOrPhone.ifBlank { "User" })
                    AuthMode.SIGN_UP -> onSuccessLogin(fullName.ifBlank { "New Member" })
                    AuthMode.FORGOT_PASSWORD -> mode = AuthMode.OTP_VERIFICATION
                    AuthMode.OTP_VERIFICATION -> onSuccessLogin("Verified Member")
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricBlue,
                contentColor = CinemaBlack
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
        ) {
            Text(
                text = when (mode) {
                    AuthMode.LOGIN -> "Sign In"
                    AuthMode.SIGN_UP -> "Create Free Account"
                    AuthMode.FORGOT_PASSWORD -> "Send Verification Code"
                    AuthMode.OTP_VERIFICATION -> "Verify & Sign In"
                },
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        // Secondary social login options (Google UI)
        if (mode == AuthMode.LOGIN || mode == AuthMode.SIGN_UP) {
            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFF26324D)))
                Text("  OR  ", color = TextSecondary, fontSize = 12.sp)
                Box(modifier = Modifier.weight(1f).height(1.dp).background(Color(0xFF26324D)))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Google Login UI
            OutlinedButton(
                onClick = { onSuccessLogin("Google User") },
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26324D)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
            ) {
                Text("Continue with Google", color = TextPrimary, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle Login / Sign up link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (mode == AuthMode.LOGIN) {
                Text("Don't have an account? ", color = TextSecondary, fontSize = 13.sp)
                Text(
                    text = "Sign Up",
                    color = ElectricBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { mode = AuthMode.SIGN_UP }
                )
            } else if (mode == AuthMode.SIGN_UP || mode == AuthMode.FORGOT_PASSWORD || mode == AuthMode.OTP_VERIFICATION) {
                Text("Already have an account? ", color = TextSecondary, fontSize = 13.sp)
                Text(
                    text = "Sign In",
                    color = ElectricBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { mode = AuthMode.LOGIN }
                )
            }
        }
    }
}

@Composable
fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ElectricBlue,
    unfocusedBorderColor = Color(0xFF26324D),
    focusedContainerColor = CinemaSurfaceElevated,
    unfocusedContainerColor = CinemaSurfaceElevated,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedLabelColor = ElectricBlue,
    unfocusedLabelColor = TextSecondary
)
