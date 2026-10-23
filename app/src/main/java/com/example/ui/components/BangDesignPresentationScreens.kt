package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BangElectricBlue
import com.example.ui.theme.BangEmeraldMesh
import com.example.ui.theme.BangGlassBorder
import com.example.ui.theme.BangGlassBorderPurple
import com.example.ui.theme.BangGlassSurface
import com.example.ui.theme.BangNeonPurple
import com.example.ui.theme.BangObsidian
import com.example.ui.theme.BangPurpleDeep
import com.example.ui.theme.BangSurface
import com.example.ui.theme.BangSurfaceVariant
import com.example.ui.theme.BangTextMuted
import com.example.ui.theme.BangTextPrimary
import com.example.ui.theme.BangTextSecondary

/**
 * Screen 1: Mobile Login Screen
 * High-fidelity presentation matching user prompt specifications
 */
@Composable
fun BangLoginScreen(
    onLoginClick: (String, String) -> Unit = { _, _ -> },
    onNavigateToSignUp: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var usernameOrEmail by remember { mutableStateOf("alex.rivera@mesh.bang") }
    var password by remember { mutableStateOf("••••••••••••") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loginFeedback by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BangObsidian)
            .testTag("bang_login_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(36.dp))

            // Mesh status indicator pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(BangGlassSurface)
                    .border(1.dp, BangGlassBorder, RoundedCornerShape(16.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(BangEmeraldMesh)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DECENTRALIZED MESH PROTOCOL",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = BangEmeraldMesh,
                        letterSpacing = 1.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App Logo Icon
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF070F1A))
                    .border(1.5.dp, BangElectricBlue.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.example.R.drawable.app_logo),
                    contentDescription = "BANG App Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Top Header: Bold stylish typography logo "BANG" + subtitle "Connect with your network"
            Text(
                text = "BANG",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 44.sp,
                    letterSpacing = 4.sp,
                    color = BangTextPrimary
                )
            )

            Text(
                text = "Connect with your network",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BangTextSecondary,
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(top = 4.dp, bottom = 36.dp)
            )

            // Glassmorphic Form Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BangGlassBorder, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Input 1: "Username or Email" (with user icon)
                    Text(
                        text = "USERNAME OR EMAIL",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BangElectricBlue
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    BangInputField(
                        value = usernameOrEmail,
                        onValueChange = { usernameOrEmail = it },
                        placeholder = "Username or Email",
                        leadingIcon = Icons.Default.Person,
                        modifier = Modifier.testTag("login_input_username")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Input 2: "Password" (with lock icon & show/hide eye icon)
                    Text(
                        text = "PASSWORD",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BangElectricBlue
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    BangInputField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Password",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        isPasswordVisible = isPasswordVisible,
                        onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                        modifier = Modifier.testTag("login_input_password")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Text Link: "Forgot Password?" below inputs
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Forgot Password?",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = BangElectricBlue,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 12.sp
                            ),
                            modifier = Modifier
                                .clickable {
                                    loginFeedback = "Recovery token can be restored via local Ed25519 seed phrase"
                                    onForgotPasswordClick()
                                }
                                .padding(vertical = 4.dp)
                                .testTag("login_link_forgot_password")
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Primary Button: "Log In" (Vibrant Blue/Purple Gradient with glowing neon border)
                    BangGradientButton(
                        text = "Log In",
                        onClick = {
                            loginFeedback = "Logged into Ed25519 node session"
                            onLoginClick(usernameOrEmail, password)
                        },
                        modifier = Modifier.testTag("login_button_primary")
                    )
                }
            }

            if (loginFeedback != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = loginFeedback ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BangEmeraldMesh,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Secondary Actions & Links: Navigate to sign up
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Don't have a mesh identity? ",
                    style = MaterialTheme.typography.bodySmall.copy(color = BangTextSecondary)
                )
                Text(
                    text = "Create Account",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BangNeonPurple,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .clickable { onNavigateToSignUp() }
                        .padding(4.dp)
                        .testTag("login_link_signup")
                )
            }
        }
    }
}

/**
 * Screen 2: Account Sign-Up Screen
 * High-fidelity presentation matching user prompt specifications
 */
@Composable
fun BangSignUpScreen(
    onSignUpClick: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onNavigateToLogin: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var recoveryEmailOrMobile by remember { mutableStateOf("alex.rivera@icloud.com") }
    var fullName by remember { mutableStateOf("Alex Rivera") }
    var usernameHandle by remember { mutableStateOf("@alex_rivera") }
    var password by remember { mutableStateOf("P2pPassphrase2026!") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var signUpFeedback by remember { mutableStateOf<String?>(null) }
    var agreedToTerms by remember { mutableStateOf(true) }
    var activeLegalDoc by remember { mutableStateOf<LegalDocumentType?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BangObsidian)
            .testTag("bang_signup_screen")
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

            // App Logo Icon
            Box(
                modifier = Modifier
                    .size(84.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF070F1A))
                    .border(1.5.dp, BangElectricBlue.copy(alpha = 0.8f), RoundedCornerShape(20.dp)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = com.example.R.drawable.app_logo),
                    contentDescription = "BANG App Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(20.dp))
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Top Header: Bold stylish typography logo "BANG" + subtitle "Connect with your network"
            Text(
                text = "BANG",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Black,
                    fontSize = 44.sp,
                    letterSpacing = 4.sp,
                    color = BangTextPrimary
                )
            )

            Text(
                text = "Connect with your network",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = BangTextSecondary,
                    fontSize = 14.sp
                ),
                modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
            )

            // Glassmorphic Card Container
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, BangGlassBorderPurple, RoundedCornerShape(20.dp))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Input 1: "Security Email or Mobile (for Recovery)" (with shield icon)
                    Text(
                        text = "RECOVERY CONTACT (ZERO-KNOWLEDGE)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BangElectricBlue
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    BangInputField(
                        value = recoveryEmailOrMobile,
                        onValueChange = { recoveryEmailOrMobile = it },
                        placeholder = "Security Email or Mobile (for Recovery)",
                        leadingIcon = Icons.Default.Shield,
                        modifier = Modifier.testTag("signup_input_recovery")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Input 2: "Full Name" (with ID icon)
                    Text(
                        text = "FULL NAME",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BangElectricBlue
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    BangInputField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        placeholder = "Full Name",
                        leadingIcon = Icons.Default.Badge,
                        modifier = Modifier.testTag("signup_input_fullname")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Input 3: "Username (@handle)" (with @ icon)
                    Text(
                        text = "USERNAME HANDLE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BangElectricBlue
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    BangInputField(
                        value = usernameHandle,
                        onValueChange = { usernameHandle = it },
                        placeholder = "Username (@handle)",
                        leadingIcon = Icons.Default.AlternateEmail,
                        modifier = Modifier.testTag("signup_input_username")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Input 4: "Password" (with lock icon)
                    Text(
                        text = "NODE PASSPHRASE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BangElectricBlue
                        ),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    BangInputField(
                        value = password,
                        onValueChange = { password = it },
                        placeholder = "Password",
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        isPasswordVisible = isPasswordVisible,
                        onTogglePasswordVisibility = { isPasswordVisible = !isPasswordVisible },
                        modifier = Modifier.testTag("signup_input_password")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Subtle Checkbox Link during Sign-Up Flow: "By continuing, you agree to BANG's Terms of Service and Privacy Policy"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Checkbox(
                            checked = agreedToTerms,
                            onCheckedChange = { agreedToTerms = it },
                            modifier = Modifier.testTag("signup_checkbox_agree_terms"),
                            colors = androidx.compose.material3.CheckboxDefaults.colors(
                                checkedColor = BangElectricBlue,
                                uncheckedColor = BangTextMuted,
                                checkmarkColor = BangSurface
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "By continuing, you agree to BANG’s ",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BangTextSecondary,
                                    fontSize = 10.5.sp
                                )
                            )
                            Text(
                                text = "Terms of Service",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BangElectricBlue,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                                ),
                                modifier = Modifier
                                    .clickable { activeLegalDoc = LegalDocumentType.TERMS_OF_SERVICE }
                                    .testTag("signup_link_terms")
                            )
                            Text(
                                text = " and ",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BangTextSecondary,
                                    fontSize = 10.5.sp
                                )
                            )
                            Text(
                                text = "Privacy Policy",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = BangEmeraldMesh,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline
                                ),
                                modifier = Modifier
                                    .clickable { activeLegalDoc = LegalDocumentType.PRIVACY_POLICY }
                                    .testTag("signup_link_privacy")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Primary Button: "Create Account" (Vibrant Blue/Purple Gradient)
                    BangGradientButton(
                        text = "Create Account",
                        onClick = {
                            if (!agreedToTerms) {
                                signUpFeedback = "Please accept the Terms of Service and Privacy Policy to continue."
                                return@BangGradientButton
                            }
                            signUpFeedback = "Generating Ed25519 & Curve25519 keypairs on StrongBox..."
                            onSignUpClick(recoveryEmailOrMobile, fullName, usernameHandle, password)
                        },
                        modifier = Modifier.testTag("signup_button_primary")
                    )
                }
            }

            if (signUpFeedback != null) {
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = signUpFeedback ?: "",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BangEmeraldMesh,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Secondary Actions & Links
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodySmall.copy(color = BangTextSecondary)
                )
                Text(
                    text = "Log In",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = BangElectricBlue,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .clickable { onNavigateToLogin() }
                        .padding(4.dp)
                        .testTag("signup_link_login")
                )
            }
        }
    }

    // Modal Viewer for Terms of Service and Privacy Policy during Sign-Up
    activeLegalDoc?.let { docType ->
        BangLegalModal(
            documentType = docType,
            onDismiss = { activeLegalDoc = null }
        )
    }
}

/**
 * Screen 3: Post-Onboarding Contact Sync Modal
 * High-fidelity presentation matching user prompt specifications
 */
@Composable
fun BangContactSyncModal(
    onSyncClick: () -> Unit = {},
    onSkipClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isSyncing by remember { mutableStateOf(false) }
    var syncCompleted by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BangObsidian.copy(alpha = 0.88f))
            .clickable(enabled = false) {}
            .testTag("bang_contact_sync_modal"),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .fillMaxWidth()
                .border(1.5.dp, BangGlassBorder, RoundedCornerShape(24.dp))
                .shadow(elevation = 20.dp, spotColor = BangElectricBlue)
        ) {
            Column(
                modifier = Modifier.padding(26.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Icon: 👥 Glowing neon group contacts vector icon
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    BangElectricBlue.copy(alpha = 0.25f),
                                    Color.Transparent
                                )
                            )
                        )
                        .border(1.5.dp, BangElectricBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Contacts Group",
                        tint = BangElectricBlue,
                        modifier = Modifier.size(42.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title: "Find Your Friends"
                Text(
                    text = "Find Your Friends",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BangTextPrimary,
                        fontSize = 22.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Body Text: "BANG checks your contacts to suggest people you know. Your address book is securely hashed using SHA-256 locally on device."
                Text(
                    text = "BANG checks your contacts to suggest people you know. Your address book is securely hashed using SHA-256 locally on device.",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = BangTextSecondary,
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Cryptographic badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BangGlassSurface)
                        .border(1.dp, BangEmeraldMesh.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = BangEmeraldMesh,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Zero-Knowledge Local Hashing (SHA-256)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            color = BangEmeraldMesh
                        )
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                // Primary Button: "Sync Contacts & Find Friends" (Vibrant Blue/Purple Gradient)
                BangGradientButton(
                    text = if (isSyncing) "Hashing & Matching Contacts..." else if (syncCompleted) "✓ 12 Friends Found in Range" else "Sync Contacts & Find Friends",
                    onClick = {
                        isSyncing = true
                        syncCompleted = false
                        onSyncClick()
                    },
                    modifier = Modifier.testTag("contact_sync_button_primary")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Secondary Text Button: "Skip for Now"
                TextButton(
                    onClick = onSkipClick,
                    modifier = Modifier.testTag("contact_sync_button_skip")
                ) {
                    Text(
                        text = "Skip for Now",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BangTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }
    }
}

/**
 * Screen 4: Direct Peer Discovery Pop-Up Modal
 * Bottom sheet overlaying dark chat screen matching user prompt specifications
 */
@Composable
fun BangDirectPeerDiscoveryModal(
    displayName: String = "Alex Rivera",
    usernameHandle: String = "@alex_rivera",
    statusBadge: String = "● Direct BLE Node Verified",
    keyInfo: String = "Node ID: 8f9a...32c1 (Curve25519 Verified)",
    onConnectClick: () -> Unit = {},
    onDismissClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var isConnected by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BangObsidian.copy(alpha = 0.85f))
            .clickable { onDismissClick() }
            .testTag("bang_peer_discovery_modal"),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Bottom Sheet Card
        Card(
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            colors = CardDefaults.cardColors(containerColor = BangSurfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = false) {} // Prevent click-through
                .border(
                    width = 1.5.dp,
                    brush = Brush.verticalGradient(
                        listOf(BangElectricBlue, BangPurpleDeep)
                    ),
                    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
                )
                .shadow(elevation = 28.dp, spotColor = BangElectricBlue)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Grab Bar
                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(BangTextMuted)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Close icon button at top right
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "PEER DISCOVERY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = BangElectricBlue,
                            letterSpacing = 1.sp
                        )
                    )
                    IconButton(
                        onClick = onDismissClick,
                        modifier = Modifier.size(32.dp).testTag("peer_discovery_dismiss_icon")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = BangTextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Profile Card Layout:
                // Large circular user profile photo with glowing border
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(BangElectricBlue.copy(alpha = 0.3f), BangNeonPurple.copy(alpha = 0.3f))
                            )
                        )
                        .border(
                            width = 2.5.dp,
                            brush = Brush.sweepGradient(
                                listOf(BangElectricBlue, BangNeonPurple, BangEmeraldMesh, BangElectricBlue)
                            ),
                            shape = CircleShape
                        )
                        .shadow(elevation = 12.dp, spotColor = BangElectricBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Alex Rivera Avatar",
                        tint = BangTextPrimary,
                        modifier = Modifier.size(54.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Display Name: "Alex Rivera"
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = BangTextPrimary,
                        fontSize = 22.sp
                    )
                )

                // Username Handle: "@alex_rivera"
                Text(
                    text = usernameHandle,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = BangNeonPurple,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    ),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Status Badge: "● Direct BLE Node Verified" (Emerald Green)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(BangEmeraldMesh.copy(alpha = 0.12f))
                        .border(1.dp, BangEmeraldMesh.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(BangEmeraldMesh)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = statusBadge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = BangEmeraldMesh,
                            fontSize = 11.5.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Key Info: "Node ID: 8f9a...32c1 (Curve25519 Verified)"
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = BangObsidian),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, BangGlassBorder, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = BangElectricBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = keyInfo,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = BangTextSecondary
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Button: "⚡ Connect & Add to Mesh Contacts" (Vibrant Blue/Purple Gradient)
                BangGradientButton(
                    text = if (isConnected) "✓ Added to Mesh Contacts" else "⚡ Connect & Add to Mesh Contacts",
                    onClick = {
                        isConnected = true
                        onConnectClick()
                    },
                    leadingIcon = Icons.Default.Bolt,
                    modifier = Modifier.testTag("peer_discovery_button_connect")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Secondary Text: "Dismiss"
                TextButton(
                    onClick = onDismissClick,
                    modifier = Modifier.testTag("peer_discovery_button_dismiss")
                ) {
                    Text(
                        text = "Dismiss",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = BangTextSecondary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * Reusable Stacked clean, translucent light-gray rounded input field
 */
@Composable
fun BangInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onTogglePasswordVisibility: () -> Unit = {}
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(
                text = placeholder,
                color = BangTextMuted,
                fontSize = 13.5.sp
            )
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = BangElectricBlue,
                modifier = Modifier.size(19.dp)
            )
        },
        trailingIcon = {
            if (isPassword) {
                IconButton(
                    onClick = onTogglePasswordVisibility,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "Hide password" else "Show password",
                        tint = BangTextSecondary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = BangTextPrimary,
            unfocusedTextColor = BangTextPrimary,
            focusedContainerColor = BangGlassSurface,
            unfocusedContainerColor = BangGlassSurface,
            focusedBorderColor = BangElectricBlue,
            unfocusedBorderColor = BangGlassBorder,
            cursorColor = BangElectricBlue
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Reusable Prominent, full-width vibrant gradient primary action button
 * with rounded corners and glowing neon border
 */
@Composable
fun BangGradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null
) {
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(BangElectricBlue, BangNeonPurple)
    )

    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color.White
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = BangElectricBlue
            )
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(BangElectricBlue, BangNeonPurple)
                ),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(gradientBrush),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp,
                        color = Color.White
                    )
                )
            }
        }
    }
}
