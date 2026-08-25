package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BannedPlayerEntity
import com.example.data.PlayerOnline
import com.example.ui.theme.CyberCardSurface
import com.example.ui.theme.DangerRed
import com.example.ui.theme.LglGreenPrimary
import com.example.ui.theme.TextMuted

@Composable
fun AdminPanelTabContent(
    onlinePlayers: List<PlayerOnline>,
    bannedPlayers: List<BannedPlayerEntity>,
    onBanPlayer: (PlayerOnline, String) -> Unit,
    onUnbanPlayer: (String) -> Unit
) {
    var selectedPlayerToBan by remember { mutableStateOf<PlayerOnline?>(null) }
    var banReasonInput by remember { mutableStateOf("Penggunaan Cheating / Toxic In Room") }
    var showBanDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Admin Privilege Badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCardSurface)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin",
                        tint = LglGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "FITUR ADMIN ROOM MULTIPLAYER",
                            color = LglGreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        Text(
                            text = "Akses Penuh: Banned User, Kick, & Mute Akun",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Badge(containerColor = LglGreenPrimary) {
                    Text("HOST ADMIN", color = Color(0xFF381E72), fontWeight = FontWeight.Bold, fontSize = 9.sp)
                }
            }
        }

        // Active Player List
        Text(
            text = "DAFTAR PLAYER ONLINE DI ROOM (${onlinePlayers.size} PLAYER)",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            items(onlinePlayers) { player ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = CyberCardSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = player.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "[${player.id}]",
                                    color = LglGreenPrimary,
                                    fontSize = 10.sp
                                )
                            }
                            Text(
                                text = "Bus: ${player.busModel} | Ping: ${player.pingMs}ms",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        Button(
                            onClick = {
                                selectedPlayerToBan = player
                                showBanDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB4AB)),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("ban_user_btn_${player.id}")
                        ) {
                            Text(
                                text = "BAN USER",
                                color = Color(0xFF690005),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Banned Database List
        Text(
            text = "DAFTAR AKUN TER-BAN (SQLITE DATABASE: ${bannedPlayers.size})",
            color = DangerRed,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(110.dp)
        ) {
            items(bannedPlayers) { banned ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1015))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "🚫 ${banned.username} (${banned.id})",
                                color = DangerRed,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Alasan: ${banned.reason}",
                                color = Color.LightGray,
                                fontSize = 9.sp
                            )
                        }

                        IconButton(
                            onClick = { onUnbanPlayer(banned.id) },
                            modifier = Modifier.size(28.dp).testTag("unban_btn_${banned.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Restore,
                                contentDescription = "Unban",
                                tint = LglGreenPrimary
                            )
                        }
                    }
                }
            }
        }
    }

    // Ban Confirmation Dialog
    if (showBanDialog && selectedPlayerToBan != null) {
        val player = selectedPlayerToBan!!
        AlertDialog(
            onDismissRequest = { showBanDialog = false },
            title = {
                Text(
                    text = "BAN AKUN PLAYER ${player.name}?",
                    color = DangerRed,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Pilih atau ketik alasan pemblokiran akun:")
                    OutlinedTextField(
                        value = banReasonInput,
                        onValueChange = { banReasonInput = it },
                        label = { Text("Alasan Banned") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onBanPlayer(player, banReasonInput)
                        showBanDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("EKSEKUSI BAN", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBanDialog = false }) {
                    Text("BATAL", color = Color.White)
                }
            },
            containerColor = CyberCardSurface
        )
    }
}
