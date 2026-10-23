package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.MeshPulseStory
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberCardSurface
import com.example.ui.theme.CyberElectricCyan
import com.example.ui.theme.CyberHyperPink
import com.example.ui.theme.CyberKineticEmerald
import com.example.ui.theme.CyberNeonAmber
import com.example.ui.theme.CyberSlateBg
import com.example.ui.theme.CyberTextMuted
import com.example.ui.theme.CyberTextPrimary
import com.example.ui.theme.CyberTextSecondary
import kotlinx.coroutines.delay

/**
 * Component A: Full-Screen Mesh Story Viewer Modal
 *
 * Implements high-fidelity Instagram-style story mechanics customized for offline mesh networks:
 * - Segmented top progress bars (auto-advancing or tap to advance/rewind)
 * - User handle, author avatar, timestamp, and routing badge ("⚡ 2 Hops via Stranger Bob")
 * - Interactive overlay stickers: Polls with live vote tallies, Voice Notes with audio playback, "Add Yours" prompts
 * - Bottom direct reply input bar over mesh with emoji reactions
 */
@Composable
fun MeshStoryViewerModal(
    story: MeshPulseStory,
    allStories: List<MeshPulseStory>,
    onClose: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onVotePoll: (String, Int) -> Unit = { _, _ -> },
    onSendReply: (String, String) -> Unit = { _, _ -> }
) {
    val currentIndex = remember(story, allStories) {
        allStories.indexOfFirst { it.id == story.id }.coerceAtLeast(0)
    }

    var isPaused by remember { mutableStateOf(false) }
    var replyText by remember { mutableStateOf("") }
    var toastNotice by remember { mutableStateOf<String?>(null) }
    var isVoicePlaying by remember { mutableStateOf(false) }

    // Ephemeral toast timer
    LaunchedEffect(toastNotice) {
        if (toastNotice != null) {
            delay(2200)
            toastNotice = null
        }
    }

    // Auto-advance progress timer (5 seconds per story)
    var progress by remember(story.id) { mutableFloatStateOf(0f) }
    LaunchedEffect(story.id, isPaused) {
        progress = 0f
        val stepMs = 50L
        val totalMs = 5000L
        val increment = stepMs.toFloat() / totalMs.toFloat()
        while (progress < 1f) {
            if (!isPaused) {
                delay(stepMs)
                progress += increment
            } else {
                delay(100)
            }
        }
        if (!isPaused) {
            onNext()
        }
    }

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .testTag("mesh_story_viewer_modal")
        ) {
            // Background Dynamic Gradient Surface
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(story.bgGradientStartHex),
                                Color(story.bgGradientEndHex),
                                CyberSlateBg
                            )
                        )
                    )
            )

            // Left / Right Tap zones for Prev / Next story navigation
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onPrev()
                        }
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            onNext()
                        }
                )
            }

            // Foreground Content Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // -------------------------------------------------------------
                // 1. TOP BAR: Segmented Progress + User Metadata Header
                // -------------------------------------------------------------
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Segmented Progress Bars
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        allStories.forEachIndexed { idx, _ ->
                            val segmentProgress = when {
                                idx < currentIndex -> 1f
                                idx == currentIndex -> progress.coerceIn(0f, 1f)
                                else -> 0f
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(3.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color.White.copy(alpha = 0.25f))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(segmentProgress)
                                        .height(3.dp)
                                        .background(Color.White)
                                )
                            }
                        }
                    }

                    // Author Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Avatar with Story Ring
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        brush = if (story.isLocalMesh) {
                                            Brush.linearGradient(listOf(CyberNeonAmber, Color(0xFFFFD54F)))
                                        } else {
                                            Brush.linearGradient(listOf(CyberHyperPink, CyberElectricCyan))
                                        },
                                        shape = CircleShape
                                    )
                                    .padding(2.5.dp)
                                    .clip(CircleShape)
                                    .background(CyberCardSurface),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = story.avatarInitials,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Black,
                                        color = if (story.isLocalMesh) CyberNeonAmber else CyberHyperPink,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = story.authorName,
                                        style = MaterialTheme.typography.titleSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 13.5.sp
                                        )
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "• ${story.timestampText}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White.copy(alpha = 0.7f),
                                            fontSize = 10.sp
                                        )
                                    )
                                }

                                Spacer(modifier = Modifier.height(2.dp))

                                // Routing Badge ("⚡ 2 Hops via Stranger Bob")
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(
                                                if (story.isLocalMesh) CyberNeonAmber.copy(alpha = 0.2f)
                                                else CyberElectricCyan.copy(alpha = 0.2f)
                                            )
                                            .border(
                                                0.5.dp,
                                                if (story.isLocalMesh) CyberNeonAmber else CyberElectricCyan,
                                                RoundedCornerShape(4.dp)
                                            )
                                            .padding(horizontal = 6.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = story.hopsBadge,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp,
                                                color = if (story.isLocalMesh) CyberNeonAmber else CyberElectricCyan
                                            )
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = story.authorHandle,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.5.sp,
                                            color = CyberTextSecondary
                                        )
                                    )
                                }
                            }
                        }

                        // Close Modal Button
                        IconButton(
                            onClick = onClose,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.45f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // -------------------------------------------------------------
                // 2. CENTER PIECE: Media Caption & Interactive Overlay Stickers
                // -------------------------------------------------------------
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Story Caption Card
                    Surface(
                        color = Color(0xCC0D1420),
                        shape = RoundedCornerShape(14.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, CyberCardBorder),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = story.caption,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 15.sp,
                                lineHeight = 22.sp
                            ),
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    // INTERACTIVE STICKER 1: Live Poll Sticker
                    if (story.pollQuestion != null) {
                        val totalVotes = story.pollVotesA + story.pollVotesB
                        val pctA = if (totalVotes > 0) (story.pollVotesA * 100 / totalVotes) else 50
                        val pctB = if (totalVotes > 0) (100 - pctA) else 50

                        Surface(
                            color = Color(0xE6101B2C),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberHyperPink.copy(alpha = 0.7f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("poll_sticker")
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Poll,
                                        contentDescription = null,
                                        tint = CyberHyperPink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "MESH COMMUNITY POLL",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Black,
                                            color = CyberHyperPink,
                                            fontSize = 10.sp,
                                            letterSpacing = 0.8.sp
                                        )
                                    )
                                }

                                Text(
                                    text = story.pollQuestion,
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 13.5.sp
                                    ),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                // Option A
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF16253B))
                                        .border(
                                            width = if (story.userVotedOption == 0) 1.5.dp else 0.5.dp,
                                            color = if (story.userVotedOption == 0) CyberKineticEmerald else Color(0xFF263B59),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            isPaused = true
                                            onVotePoll(story.id, 0)
                                            toastNotice = "Vote recorded over BLE mesh!"
                                        }
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(pctA / 100f)
                                            .height(40.dp)
                                            .background(CyberElectricCyan.copy(alpha = 0.22f))
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = story.pollOptionA ?: "Yes",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 12.sp
                                            )
                                        )
                                        Text(
                                            text = "$pctA%",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Black,
                                                color = CyberElectricCyan,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Option B
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Color(0xFF16253B))
                                        .border(
                                            width = if (story.userVotedOption == 1) 1.5.dp else 0.5.dp,
                                            color = if (story.userVotedOption == 1) CyberKineticEmerald else Color(0xFF263B59),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            isPaused = true
                                            onVotePoll(story.id, 1)
                                            toastNotice = "Vote recorded over BLE mesh!"
                                        }
                                        .padding(horizontal = 12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(pctB / 100f)
                                            .height(40.dp)
                                            .background(CyberHyperPink.copy(alpha = 0.22f))
                                    )
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = story.pollOptionB ?: "No",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 12.sp
                                            )
                                        )
                                        Text(
                                            text = "$pctB%",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Black,
                                                color = CyberHyperPink,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // INTERACTIVE STICKER 2: Voice Note Sticker
                    if (story.hasVoiceNote) {
                        Surface(
                            color = Color(0xE60E1826),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberNeonAmber.copy(alpha = 0.8f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("voice_note_sticker")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Play / Pause Button
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CyberNeonAmber)
                                        .clickable {
                                            isVoicePlaying = !isVoicePlaying
                                            toastNotice = if (isVoicePlaying) "Playing 6kbps mesh voice packet" else "Voice paused"
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = if (isVoicePlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play voice note",
                                        tint = Color.Black,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(10.dp))

                                // Audio Waveform Bars Simulation
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val barHeights = listOf(14, 22, 10, 28, 18, 32, 24, 16, 26, 12, 30, 20, 15, 25, 10, 18)
                                    barHeights.forEachIndexed { i, h ->
                                        Box(
                                            modifier = Modifier
                                                .width(3.dp)
                                                .height(h.dp)
                                                .clip(RoundedCornerShape(2.dp))
                                                .background(
                                                    if (isVoicePlaying && i < 8) CyberKineticEmerald else CyberNeonAmber.copy(alpha = 0.7f)
                                                )
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Text(
                                    text = "0:${story.voiceDurationSec.toString().padStart(2, '0')}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = CyberNeonAmber
                                    )
                                )
                            }
                        }
                    }

                    // INTERACTIVE STICKER 3: "Add Yours" Photo Prompt Sticker
                    if (story.addYoursPrompt != null) {
                        Surface(
                            color = Color(0xEE160B24),
                            shape = RoundedCornerShape(16.dp),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, CyberHyperPink),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("add_yours_sticker")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(CyberHyperPink.copy(alpha = 0.2f))
                                            .border(1.dp, CyberHyperPink, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.CameraAlt,
                                            contentDescription = null,
                                            tint = CyberHyperPink,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "ADD YOURS",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 9.sp,
                                                color = CyberHyperPink
                                            )
                                        )
                                        Text(
                                            text = story.addYoursPrompt,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = Color.White
                                            )
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(CyberHyperPink)
                                        .clickable {
                                            toastNotice = "Opened Mesh Camera: Capture Pulse photo to contribute!"
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "CONTRIBUTE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // -------------------------------------------------------------
                // 3. BOTTOM BAR: Direct Mesh Message Input & Reaction Bar
                // -------------------------------------------------------------
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Ephemeral Toast Alert
                    AnimatedVisibility(
                        visible = toastNotice != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF091F14))
                                .border(1.dp, CyberKineticEmerald, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = toastNotice ?: "",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyberKineticEmerald
                                )
                            )
                        }
                    }

                    // Quick Reactions Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val reactions = listOf("🔥", "⚡", "🛡️", "❤️", "🚨", "📍")
                        reactions.forEach { emoji ->
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.5f))
                                    .border(0.5.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                                    .clickable {
                                        toastNotice = "Sent $emoji reaction to ${story.authorHandle} over mesh!"
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 16.sp)
                            }
                        }
                    }

                    // Reply Input Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = replyText,
                            onValueChange = {
                                replyText = it
                                isPaused = it.isNotEmpty()
                            },
                            placeholder = {
                                Text(
                                    text = "Reply directly over mesh...",
                                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                    color = CyberTextSecondary
                                )
                            },
                            shape = RoundedCornerShape(22.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xEE121824),
                                unfocusedContainerColor = Color(0xCC121824),
                                focusedBorderColor = CyberElectricCyan,
                                unfocusedBorderColor = CyberCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("story_reply_input"),
                            singleLine = true
                        )

                        // Send Button
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    if (replyText.isNotBlank()) CyberElectricCyan
                                    else Color(0xFF1E2838)
                                )
                                .clickable {
                                    if (replyText.isNotBlank()) {
                                        onSendReply(story.id, replyText)
                                        toastNotice = "Reply sent to ${story.authorHandle} (2 Hops)"
                                        replyText = ""
                                        isPaused = false
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Send",
                                tint = if (replyText.isNotBlank()) Color.Black else CyberTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
