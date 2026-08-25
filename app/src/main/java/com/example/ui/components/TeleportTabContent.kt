package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ModState
import com.example.data.TeleportLocation
import com.example.ui.theme.CyberCardSurface
import com.example.ui.theme.LglGreenPrimary
import com.example.ui.theme.TextMuted

@Composable
fun TeleportTabContent(
    modState: ModState,
    locations: List<TeleportLocation>,
    onTeleportTo: (TeleportLocation) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Active Location HUD Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCardSurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = "Location",
                        tint = LglGreenPrimary
                    )
                    Text(
                        text = "LOKASI SAAT INI (GPS MOD OVERLAY)",
                        color = LglGreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = modState.currentLocationName,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Text(
                    text = "Koordinat X: ${modState.currentX} | Y: ${modState.currentY} | Z: ${modState.currentZ}m",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }

        Text(
            text = "PILIH TERMINAL / RUTE TELEPORTASI (1-TAP INSTANT)",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )

        // Locations List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            items(locations) { loc ->
                val isCurrent = loc.name == modState.currentLocationName
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onTeleportTo(loc) }
                        .border(
                            width = if (isCurrent) 1.5.dp else 0.dp,
                            color = if (isCurrent) LglGreenPrimary else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        ),
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
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(
                                        if (isCurrent) LglGreenPrimary.copy(alpha = 0.2f) else Color.DarkGray.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Place,
                                    contentDescription = null,
                                    tint = if (isCurrent) LglGreenPrimary else TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = loc.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "${loc.region} - ${loc.description}",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Button(
                            onClick = { onTeleportTo(loc) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCurrent) Color.DarkGray else LglGreenPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("teleport_btn_${loc.id}")
                        ) {
                            Text(
                                text = if (isCurrent) "LOKASI INI" else "TELEPORT",
                                color = if (isCurrent) Color.White else Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
