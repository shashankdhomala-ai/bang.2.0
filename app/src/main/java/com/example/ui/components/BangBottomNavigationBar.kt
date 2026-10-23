package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BangCharcoalBorder
import com.example.ui.theme.BangCharcoalSurface
import com.example.ui.theme.BangElectricKineticLime

/**
 * 5-Tab Production Bottom Navigation Bar for BANG
 * Exactly matches the bottom bar shown across Screens 3, 5, 6, 8, 9 in the design reference:
 * - Tab 0: Chats (ChatBubble icon)
 * - Tab 1: Contacts (People icon)
 * - Tab 2: Mesh (Mesh/Radar icon)
 * - Tab 3: AI (Lightning Bolt icon)
 * - Tab 4: Profile (Person icon)
 *
 * Active tab: Electric Kinetic Lime (#99FF00)
 * Inactive tab: Muted Slate (#64748B)
 */
@Composable
fun BangBottomNavigationBar(
    selectedTab: Int,
    unreadTotal: Int,
    activeSosAlerts: Int,
    onSelectTab: (Int) -> Unit,
    onOpenScanModal: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val activeColor = BangElectricKineticLime
    val inactiveColor = Color(0xFF64748B)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, bottom = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        SurfaceNavPill(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tab 0: Chats
                NavTabItem(
                    icon = Icons.Default.ChatBubble,
                    label = "Chats",
                    isSelected = selectedTab == 0,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    badgeCount = unreadTotal,
                    onClick = { onSelectTab(0) },
                    testTag = "nav_tab_chats"
                )

                // Tab 1: Contacts
                NavTabItem(
                    icon = Icons.Default.People,
                    label = "Contacts",
                    isSelected = selectedTab == 1,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    onClick = { onSelectTab(1) },
                    testTag = "nav_tab_contacts"
                )

                // Tab 2: Mesh
                NavTabItem(
                    icon = Icons.Default.Hub,
                    label = "Mesh",
                    isSelected = selectedTab == 2,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    onClick = { onSelectTab(2) },
                    testTag = "nav_tab_mesh"
                )

                // Tab 3: AI
                NavTabItem(
                    icon = Icons.Default.Bolt,
                    label = "AI",
                    isSelected = selectedTab == 3,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    onClick = { onSelectTab(3) },
                    testTag = "nav_tab_ai"
                )

                // Tab 4: Profile
                NavTabItem(
                    icon = Icons.Default.Person,
                    label = "Profile",
                    isSelected = selectedTab == 4,
                    activeColor = activeColor,
                    inactiveColor = inactiveColor,
                    onClick = { onSelectTab(4) },
                    testTag = "nav_tab_profile"
                )
            }
        }
    }
}

@Composable
private fun SurfaceNavPill(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(28.dp))
            .background(BangCharcoalSurface)
            .border(1.dp, BangCharcoalBorder, RoundedCornerShape(28.dp)),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.NavTabItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    badgeCount: Int = 0,
    onClick: () -> Unit,
    testTag: String
) {
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) activeColor else inactiveColor,
        label = "navTabColor"
    )

    Column(
        modifier = Modifier
            .weight(1f)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) { onClick() }
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge(
                        containerColor = activeColor,
                        contentColor = Color(0xFF070B11)
                    ) {
                        Text(
                            text = if (badgeCount > 99) "99+" else "$badgeCount",
                            fontWeight = FontWeight.Black,
                            fontSize = 8.5.sp
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = animatedColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 11.sp,
                color = animatedColor
            )
        )
    }
}
