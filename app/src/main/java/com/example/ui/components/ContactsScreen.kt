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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MeshPeerNode
import com.example.model.TransportType
import com.example.ui.BangUiState
import com.example.ui.theme.BangCharcoalBorder
import com.example.ui.theme.BangCharcoalSurface
import com.example.ui.theme.BangElectricKineticLime
import com.example.ui.theme.BangObsidianBg
import com.example.ui.theme.BangStatusOfflineMesh
import com.example.ui.theme.BangStatusOnline
import com.example.ui.theme.BangStatusSos

data class ContactDirectoryItem(
    val id: String,
    val name: String,
    val username: String,
    val distanceText: String,
    val statusText: String,
    val isOnline: Boolean,
    val isNearby: Boolean,
    val isGroup: Boolean = false,
    val keyFingerprint: String = "ed25519:7a4f..9b12",
    val avatarInitials: String = name.take(2).uppercase()
)

/**
 * Screen 6: Contacts & Node Directory Screen
 * Matches Phone 6 of the production design layout.
 */
@Composable
fun ContactsScreen(
    uiState: BangUiState,
    onOpenChat: (String) -> Unit = {},
    onScanQr: () -> Unit = {},
    onAddContact: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") } // All, Online, Nearby, Groups
    var contactForDetail by remember { mutableStateOf<ContactDirectoryItem?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newContactHandle by remember { mutableStateOf("") }
    var newContactName by remember { mutableStateOf("") }

    val baseContacts = remember {
        listOf(
            ContactDirectoryItem(
                id = "peer-alex-01",
                name = "Alex",
                username = "@alex",
                distanceText = "2m",
                statusText = "Nearby",
                isOnline = true,
                isNearby = true,
                keyFingerprint = "ed25519:4a8c..3e91",
                avatarInitials = "AL"
            ),
            ContactDirectoryItem(
                id = "peer-sarah-02",
                name = "Sarah",
                username = "@sarah",
                distanceText = "5m",
                statusText = "Nearby",
                isOnline = true,
                isNearby = true,
                keyFingerprint = "ed25519:9e21..7f04",
                avatarInitials = "SA"
            ),
            ContactDirectoryItem(
                id = "peer-rohan-03",
                name = "Rohan",
                username = "@rohan",
                distanceText = "12m",
                statusText = "Offline",
                isOnline = false,
                isNearby = false,
                keyFingerprint = "ed25519:3b7a..11cc",
                avatarInitials = "RO"
            ),
            ContactDirectoryItem(
                id = "group-team-bang",
                name = "Team BANG",
                username = "@team_bang",
                distanceText = "3h",
                statusText = "Group",
                isOnline = true,
                isNearby = true,
                isGroup = true,
                keyFingerprint = "multi-sig:90fd..44aa",
                avatarInitials = "TB"
            ),
            ContactDirectoryItem(
                id = "peer-priya-04",
                name = "Priya",
                username = "@priya",
                distanceText = "1d",
                statusText = "Offline",
                isOnline = false,
                isNearby = false,
                keyFingerprint = "ed25519:1c55..88ee",
                avatarInitials = "PR"
            ),
            ContactDirectoryItem(
                id = "thread_charlie",
                name = "Charlie (Relay Node)",
                username = "@charlie_03",
                distanceText = "8m",
                statusText = "Relaying",
                isOnline = true,
                isNearby = true,
                keyFingerprint = "ed25519:88aa..22bc",
                avatarInitials = "CH"
            )
        )
    }

    val filteredContacts = remember(baseContacts, searchQuery, selectedFilter) {
        baseContacts.filter { contact ->
            val matchesSearch = searchQuery.isBlank() ||
                    contact.name.contains(searchQuery, ignoreCase = true) ||
                    contact.username.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                "Online" -> contact.isOnline
                "Nearby" -> contact.isNearby
                "Groups" -> contact.isGroup
                else -> true
            }

            matchesSearch && matchesFilter
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BangObsidianBg)
            .testTag("contacts_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Top Title Header
            Text(
                text = "Contacts",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            )

            // 2. Search Input: "Search by username"
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = "Search by username",
                            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                            color = Color(0xFF64748B)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color(0xFF64748B),
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("contacts_search_input"),
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BangCharcoalSurface,
                        unfocusedContainerColor = BangCharcoalSurface,
                        focusedBorderColor = BangElectricKineticLime.copy(alpha = 0.8f),
                        unfocusedBorderColor = BangCharcoalBorder,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    singleLine = true
                )
            }

            // 3. Filter Segmented Chips: [ All | Online | Nearby | Groups ]
            val filterOptions = listOf("All", "Online", "Nearby", "Groups")
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filterOptions) { filter ->
                    val isSelected = selectedFilter == filter
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) BangElectricKineticLime else BangCharcoalSurface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) BangElectricKineticLime else BangCharcoalBorder
                        ),
                        modifier = Modifier
                            .clickable { selectedFilter = filter }
                            .testTag("filter_chip_$filter")
                    ) {
                        Text(
                            text = filter,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isSelected) Color(0xFF070B11) else Color(0xFF94A3B8)
                            ),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
                        )
                    }
                }
            }

            // 4. Contact List Rows
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredContacts, key = { it.id }) { contact ->
                    ContactRowItem(
                        contact = contact,
                        onClick = { contactForDetail = contact },
                        onMessage = { onOpenChat(contact.id) }
                    )
                }
            }
        }

        // 5. Bottom Floating Action Buttons: [ Scan QR ] and [ + Add Contact ]
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onScanQr,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = BangCharcoalSurface,
                    contentColor = Color.White
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_scan_qr_contacts")
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = BangElectricKineticLime,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Scan QR",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                )
            }

            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BangElectricKineticLime,
                    contentColor = Color(0xFF070B11)
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("btn_add_contact_dialog")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color(0xFF070B11),
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Add Contact",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                )
            }
        }
    }

    // Detail Dialog
    if (contactForDetail != null) {
        val item = contactForDetail!!
        AlertDialog(
            onDismissRequest = { contactForDetail = null },
            containerColor = BangCharcoalSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (item.isGroup) BangElectricKineticLime else Color(0xFF1E2A3A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.avatarInitials,
                            fontWeight = FontWeight.Bold,
                            color = if (item.isGroup) Color(0xFF070B11) else Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = item.username,
                            style = MaterialTheme.typography.bodySmall.copy(color = BangElectricKineticLime)
                        )
                    }
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "PUBLIC KEY FINGERPRINT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF94A3B8)
                        )
                    )
                    Text(
                        text = item.keyFingerprint,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFE2E8F0)
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Status: ${item.statusText}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text("Proximity: ${item.distanceText}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val id = item.id
                        contactForDetail = null
                        onOpenChat(id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BangElectricKineticLime)
                ) {
                    Text("Chat Now", color = Color(0xFF070B11), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { contactForDetail = null }) {
                    Text("Close", color = Color(0xFF94A3B8))
                }
            }
        )
    }

    // Add Contact Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = BangCharcoalSurface,
            title = {
                Text(
                    text = "Add Mesh Contact",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Enter a @handle or import an Ed25519 peer public key:",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color(0xFF94A3B8))
                    )
                    OutlinedTextField(
                        value = newContactName,
                        onValueChange = { newContactName = it },
                        label = { Text("Display Name") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newContactHandle,
                        onValueChange = { newContactHandle = it },
                        label = { Text("Username Handle (@handle)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAddDialog = false
                        onAddContact()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BangElectricKineticLime)
                ) {
                    Text("Add", color = Color(0xFF070B11), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}

@Composable
private fun ContactRowItem(
    contact: ContactDirectoryItem,
    onClick: () -> Unit,
    onMessage: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = BangCharcoalSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, BangCharcoalBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("contact_item_${contact.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(if (contact.isGroup) Color(0xFF192E20) else Color(0xFF162130))
                    .border(
                        1.dp,
                        if (contact.isGroup) BangElectricKineticLime else Color(0xFF263342),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (contact.isGroup) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = BangElectricKineticLime,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text(
                        text = contact.avatarInitials,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Contact Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${contact.distanceText} • ${contact.statusText}",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        color = Color(0xFF8B949E)
                    )
                )
            }

            // Live Status Indicator Dot
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        if (contact.isOnline) BangStatusOnline else Color(0xFF475569)
                    )
            )
        }
    }
}
