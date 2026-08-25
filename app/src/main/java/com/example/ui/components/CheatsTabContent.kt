package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ModState
import com.example.ui.theme.CyberCardSurface
import com.example.ui.theme.LglGreenPrimary
import com.example.ui.theme.TextMuted

@Composable
fun CheatsTabContent(
    modState: ModState,
    onToggleMoney: () -> Unit,
    onToggleWallhack: () -> Unit,
    onToggleHeavyBus: () -> Unit,
    onUpdateSpeedMultiplier: (Float) -> Unit,
    onToggleNitro: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            text = "⚡ UTAMA / MAIN CHEATS LGL",
            color = LglGreenPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )

        // Unlimited Money Quick Switch
        CheatToggleRow(
            title = "Uang Tak Terbatas (Rp 999M)",
            subtitle = "Suntik Kas & Gem tanpa batas",
            icon = Icons.Default.AttachMoney,
            isChecked = modState.unlimitedMoney,
            onCheckedChange = { onToggleMoney() },
            testTag = "toggle_money"
        )

        // Wallhack / Noclip Switch
        CheatToggleRow(
            title = "Wallhack / Menembus Objek",
            subtitle = "Lewati gunung, tebing & bodi bus lain",
            icon = Icons.Default.VisibilityOff,
            isChecked = modState.wallhackEnabled,
            onCheckedChange = { onToggleWallhack() },
            testTag = "toggle_wallhack"
        )

        // Heavy Bus ("Bus Keras") Switch
        CheatToggleRow(
            title = "Bus Keras (Super Heavy Collision)",
            subtitle = "Saat ditabrak, bus lain terlempar tinggi",
            icon = Icons.Default.ElectricBolt,
            isChecked = modState.heavyBusEnabled,
            onCheckedChange = { onToggleHeavyBus() },
            testTag = "toggle_heavy_bus"
        )

        // Speed Hack Multiplier Slider
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
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Speed",
                            tint = LglGreenPrimary
                        )
                        Text(
                            text = "Speed Hack Kecepatan",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    Text(
                        text = "${String.format("%.1f", modState.speedHackMultiplier)}x (${modState.maxSpeedKmH} km/h)",
                        color = LglGreenPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Slider(
                    value = modState.speedHackMultiplier,
                    onValueChange = onUpdateSpeedMultiplier,
                    valueRange = 1.0f..10.0f,
                    steps = 18,
                    colors = SliderDefaults.colors(
                        thumbColor = LglGreenPrimary,
                        activeTrackColor = LglGreenPrimary,
                        inactiveTrackColor = Color.DarkGray
                    ),
                    modifier = Modifier.testTag("speed_slider")
                )
            }
        }

        // Nitro Booster Toggle
        CheatToggleRow(
            title = "Nitro Boost / Turbo Acceleration",
            subtitle = "Akselerasi instan 0 - 500 km/h dalam 1 detik",
            icon = Icons.Default.RocketLaunch,
            isChecked = modState.nitroBoostEnabled,
            onCheckedChange = { onToggleNitro() },
            testTag = "toggle_nitro"
        )
    }
}

@Composable
fun CheatToggleRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = CyberCardSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
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
                            if (isChecked) LglGreenPrimary.copy(alpha = 0.2f) else Color.DarkGray.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isChecked) LglGreenPrimary else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    Text(
                        text = title,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = subtitle,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = LglGreenPrimary,
                    checkedTrackColor = LglGreenPrimary.copy(alpha = 0.4f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.DarkGray
                ),
                modifier = Modifier.testTag(testTag)
            )
        }
    }
}
