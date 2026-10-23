package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BangCharcoalBorder
import com.example.ui.theme.BangCharcoalSurface
import com.example.ui.theme.BangElectricKineticLime
import com.example.ui.theme.BangObsidianBg

/**
 * Screen 2: Login / Sign-Up Screen
 * Matches Phone 2 of the production design layout.
 */
@Composable
fun BangAuthScreen(
    onAuthSuccess: (username: String) -> Unit,
    onBackToWelcome: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isSignUpMode by remember { mutableStateOf(false) }
    var usernameOrEmail by remember { mutableStateOf("uma@bang.mesh") }
    var password by remember { mutableStateOf("MeshKey2026!") }
    var recoveryContact by remember { mutableStateOf("+1 555-019-2834") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var statusMessage by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BangObsidianBg)
            .testTag("bang_auth_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Brand Title & Subtitle (Phone 2)
            Text(
                text = "BANG",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 38.sp,
                    letterSpacing = 2.sp,
                    color = BangElectricKineticLime
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Real People. Real Messages.\nNo Internet Needed.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFFCBD5E1),
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Input 1: Username or Email
            OutlinedTextField(
                value = usernameOrEmail,
                onValueChange = { usernameOrEmail = it },
                placeholder = {
                    Text(
                        text = if (isSignUpMode) "Choose @handle or Email" else "Username or Email",
                        color = Color(0xFF64748B)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BangCharcoalSurface,
                    unfocusedContainerColor = BangCharcoalSurface,
                    focusedBorderColor = BangElectricKineticLime,
                    unfocusedBorderColor = BangCharcoalBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_input_username")
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Input 2: Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Password", color = Color(0xFF64748B)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = "Toggle password",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = BangCharcoalSurface,
                    unfocusedContainerColor = BangCharcoalSurface,
                    focusedBorderColor = BangElectricKineticLime,
                    unfocusedBorderColor = BangCharcoalBorder,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("auth_input_password")
            )

            // Optional Sign-Up Recovery Anchor
            AnimatedVisibility(visible = isSignUpMode) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = recoveryContact,
                        onValueChange = { recoveryContact = it },
                        placeholder = { Text("Security Email / Mobile (Recovery Anchor)", color = Color(0xFF64748B), fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = Color(0xFF64748B),
                                modifier = Modifier.size(18.dp)
                            )
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = BangCharcoalSurface,
                            unfocusedContainerColor = BangCharcoalSurface,
                            focusedBorderColor = BangElectricKineticLime,
                            unfocusedBorderColor = BangCharcoalBorder,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = "Anchor strictly reclaims @username handle; cryptographic messages remain end-to-end encrypted.",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF64748B), fontSize = 10.sp),
                        modifier = Modifier.padding(start = 6.dp, top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Primary Log In / Create Account button in Electric Lime
            Button(
                onClick = {
                    statusMessage = "Authenticated cryptographic node session"
                    onAuthSuccess(usernameOrEmail)
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BangElectricKineticLime,
                    contentColor = Color(0xFF070B11)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("auth_btn_submit")
            ) {
                Text(
                    text = if (isSignUpMode) "Sign Up" else "Log In",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Forgot Password Link
            Text(
                text = "Forgot password?",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = BangElectricKineticLime,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp
                ),
                modifier = Modifier
                    .clickable { statusMessage = "Key recovery can be restored via your local Ed25519 seed phrase" }
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Divider: ─── or ───
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = BangCharcoalBorder)
                Text(
                    text = "  or  ",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF64748B))
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = BangCharcoalBorder)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Social Shortcuts: "Continue with Google" & "Continue with Apple"
            OutlinedButton(
                onClick = {
                    statusMessage = "Connected via Google Identity"
                    onAuthSuccess("uma_google")
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = BangCharcoalSurface,
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("G  ", fontWeight = FontWeight.Black, color = Color(0xFFEA4335))
                Text("Continue with Google", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedButton(
                onClick = {
                    statusMessage = "Connected via Apple ID"
                    onAuthSuccess("uma_apple")
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = BangCharcoalSurface,
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("  ", fontWeight = FontWeight.Black, color = Color.White)
                Text("Continue with Apple", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Switch between Log In and Sign Up
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isSignUpMode) "Already have an account? " else "Don't have an account? ",
                    style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                )
                Text(
                    text = if (isSignUpMode) "Log in" else "Sign up",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BangElectricKineticLime,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .clickable { isSignUpMode = !isSignUpMode }
                        .padding(4.dp)
                )
            }

            if (statusMessage != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = statusMessage ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BangElectricKineticLime,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.5.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
