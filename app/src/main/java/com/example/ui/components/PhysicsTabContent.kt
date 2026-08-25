package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.ui.theme.CyberCardSurface
import com.example.ui.theme.LglGreenPrimary
import com.example.ui.theme.TextMuted

@Composable
fun PhysicsTabContent(
    modState: ModState,
    onToggleHeavyBus: () -> Unit,
    onUpdateCollisionForce: (Float) -> Unit,
    onTriggerImpactTest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "💥 MOD FISIKA BUS KERAS & TABRAKAN",
            color = LglGreenPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )

        // Heavy Bus Toggle Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = CyberCardSurface)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
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
                                .size(36.dp)
                                .background(
                                    if (modState.heavyBusEnabled) LglGreenPrimary.copy(alpha = 0.2f) else Color.DarkGray,
                                    shape = RoundedCornerShape(8.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (modState.heavyBusEnabled) LglGreenPrimary else TextMuted
                            )
                        }

                        Column {
                            Text(
                                text = "Fitur Bus Keras (Super Rigid)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (modState.heavyBusEnabled) "AKTIF - Bus user kebal, lawan terlempar!" else "MATI - Tabrakan standar",
                                color = if (modState.heavyBusEnabled) LglGreenPrimary else TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = modState.heavyBusEnabled,
                        onCheckedChange = { onToggleHeavyBus() },
                        colors = SwitchDefaults.colors(checkedThumbColor = LglGreenPrimary),
                        modifier = Modifier.testTag("physics_heavy_bus_switch")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Collision Force Slider
                Text(
                    text = "Daya Dorong Impak (Collision Launch Force): ${modState.collisionForceMultiplier.toInt()}x",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )

                Slider(
                    value = modState.collisionForceMultiplier,
                    onValueChange = onUpdateCollisionForce,
                    valueRange = 1.0f..50.0f,
                    steps = 49,
                    colors = SliderDefaults.colors(
                        thumbColor = LglGreenPrimary,
                        activeTrackColor = LglGreenPrimary,
                        inactiveTrackColor = Color.DarkGray
                    ),
                    modifier = Modifier.testTag("force_slider")
                )

                Text(
                    text = "Semakin tinggi force, semakin tinggi bus traffic / lawan akan melayang ke angkasa saat tersenggol!",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }

        // Test Launch Impact Button
        Button(
            onClick = onTriggerImpactTest,
            colors = ButtonDefaults.buttonColors(containerColor = LglGreenPrimary),
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
                .testTag("test_collision_impact_btn")
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FlashOn,
                    contentDescription = null,
                    tint = Color.Black
                )
                Text(
                    text = "TES TABRAK BUS TRAFFIC (LEMPAR KE ATAS)",
                    color = Color.Black,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }
        }

        // Counter stats
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total Bus Terlempar: ${modState.knockbackCount} Bus",
                color = TextMuted,
                fontSize = 11.sp
            )
            Text(
                text = "Bebas Rusak Bodi: YA (100%)",
                color = LglGreenPrimary,
                fontSize = 11.sp
            )
        }
    }
}
