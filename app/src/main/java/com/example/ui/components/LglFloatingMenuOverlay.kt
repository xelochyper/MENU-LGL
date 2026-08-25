package com.example.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.CyberDarkBackground
import com.example.ui.theme.CyberHeaderSurface
import com.example.ui.theme.LglGreenPrimary
import com.example.ui.theme.TextMuted

@Composable
fun LglFloatingMenuOverlay(
    modState: ModState,
    busCatalog: List<BusModel>,
    teleportLocations: List<TeleportLocation>,
    onlinePlayers: List<PlayerOnline>,
    bannedPlayers: List<BannedPlayerEntity>,
    lastMessage: String,
    onClose: () -> Unit,
    onSelectTab: (ModMenuTab) -> Unit,
    onToggleMoney: () -> Unit,
    onToggleWallhack: () -> Unit,
    onToggleHeavyBus: () -> Unit,
    onUpdateSpeedMultiplier: (Float) -> Unit,
    onToggleNitro: () -> Unit,
    onToggleUnlockAllBuses: () -> Unit,
    onSelectBus: (String) -> Unit,
    onInjectMoney: (Long) -> Unit,
    onTeleportTo: (TeleportLocation) -> Unit,
    onUpdateCollisionForce: (Float) -> Unit,
    onTriggerImpactTest: () -> Unit,
    onBanPlayer: (PlayerOnline, String) -> Unit,
    onUnbanPlayer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(360.dp)
            .wrapContentHeight()
            .border(
                width = 1.5.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(LglGreenPrimary, Color(0xFF49454F))
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .testTag("lgl_menu_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CyberDarkBackground)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Iconic Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CyberHeaderSurface)
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .background(LglGreenPrimary, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("BSI", color = Color(0xFF381E72), fontWeight = FontWeight.Black, fontSize = 11.sp)
                        }

                        Column {
                            Text(
                                text = "BSI CONTROL HUB v3.8",
                                color = Color(0xFFE6E1E5),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Server: ID-7724 • Connected",
                                color = LglGreenPrimary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("close_lgl_menu_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close Menu",
                            tint = Color(0xFFCAC4D0)
                        )
                    }
                }
            }

            // Navigation Tab Bar
            ScrollableTabRow(
                selectedTabIndex = modState.activeMenuTab.ordinal,
                containerColor = Color(0xFF1C1B1F),
                contentColor = LglGreenPrimary,
                edgePadding = 8.dp,
                divider = {}
            ) {
                ModMenuTab.entries.forEach { tab ->
                    val isSelected = modState.activeMenuTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = { onSelectTab(tab) },
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}"),
                        text = {
                            Text(
                                text = when (tab) {
                                    ModMenuTab.CHEATS -> "⚡ CHEATS"
                                    ModMenuTab.GARAGE -> "🚌 GARASI"
                                    ModMenuTab.TELEPORT -> "📍 TELEPORT"
                                    ModMenuTab.PHYSICS -> "💥 FISIKA"
                                    ModMenuTab.ADMIN_PANEL -> "🔨 ADMIN"
                                    ModMenuTab.SIMULATOR -> "🎮 SIMULASI"
                                },
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) LglGreenPrimary else TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    )
                }
            }

            // Content per Tab
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 380.dp)
            ) {
                when (modState.activeMenuTab) {
                    ModMenuTab.CHEATS -> {
                        CheatsTabContent(
                            modState = modState,
                            onToggleMoney = onToggleMoney,
                            onToggleWallhack = onToggleWallhack,
                            onToggleHeavyBus = onToggleHeavyBus,
                            onUpdateSpeedMultiplier = onUpdateSpeedMultiplier,
                            onToggleNitro = onToggleNitro
                        )
                    }
                    ModMenuTab.GARAGE -> {
                        GarageTabContent(
                            modState = modState,
                            busCatalog = busCatalog,
                            onToggleUnlockAll = onToggleUnlockAllBuses,
                            onSelectBus = onSelectBus,
                            onInjectMoney = onInjectMoney
                        )
                    }
                    ModMenuTab.TELEPORT -> {
                        TeleportTabContent(
                            modState = modState,
                            locations = teleportLocations,
                            onTeleportTo = onTeleportTo
                        )
                    }
                    ModMenuTab.PHYSICS -> {
                        PhysicsTabContent(
                            modState = modState,
                            onToggleHeavyBus = onToggleHeavyBus,
                            onUpdateCollisionForce = onUpdateCollisionForce,
                            onTriggerImpactTest = onTriggerImpactTest
                        )
                    }
                    ModMenuTab.ADMIN_PANEL -> {
                        AdminPanelTabContent(
                            onlinePlayers = onlinePlayers,
                            bannedPlayers = bannedPlayers,
                            onBanPlayer = onBanPlayer,
                            onUnbanPlayer = onUnbanPlayer
                        )
                    }
                    ModMenuTab.SIMULATOR -> {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsMotorsports,
                                contentDescription = null,
                                tint = LglGreenPrimary,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "MODE DUDUK DRIVER & SIMULASI JALAN",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Layar simulasi mengemudi di latar belakang sudah aktif! Anda dapat menguji kecepatan speed hack, wallhack menembus tebing, dan melempar bus traffic secara langsung.",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                            Button(
                                onClick = onClose,
                                colors = ButtonDefaults.buttonColors(containerColor = LglGreenPrimary),
                                modifier = Modifier.fillMaxWidth().testTag("drive_now_btn")
                            ) {
                                Text("MASUK MODE MENGEMUDI (MIMIN)", color = Color(0xFF381E72), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Bottom Toast Bar displaying live feedback
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.9f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(LglGreenPrimary)
                    )
                    Text(
                        text = lastMessage,
                        color = Color.LightGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
