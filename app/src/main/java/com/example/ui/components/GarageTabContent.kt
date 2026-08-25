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
import com.example.data.BusModel
import com.example.data.ModState
import com.example.ui.theme.CyberCardSurface
import com.example.ui.theme.LglGreenPrimary
import com.example.ui.theme.TextMuted

@Composable
fun GarageTabContent(
    modState: ModState,
    busCatalog: List<BusModel>,
    onToggleUnlockAll: () -> Unit,
    onSelectBus: (String) -> Unit,
    onInjectMoney: (Long) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Unlock Status Header
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
                    Column {
                        Text(
                            text = "STATUS GARASI BUSSID",
                            color = LglGreenPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = if (modState.unlockAllBuses) "🔓 SEMUA BUS & MOD UNLOCKED" else "🔒 BEBERAPA BUS TERKUNCI",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }

                    Button(
                        onClick = onToggleUnlockAll,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (modState.unlockAllBuses) LglGreenPrimary else Color.Gray
                        ),
                        modifier = Modifier.testTag("unlock_all_buses_btn")
                    ) {
                        Text(
                            text = if (modState.unlockAllBuses) "ALL UNLOCKED" else "UNLOCK ALL",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Money Injection Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onInjectMoney(100_000_000L) },
                        modifier = Modifier.weight(1f).testTag("add_100m_btn")
                    ) {
                        Text("+Rp 100 Juta", color = LglGreenPrimary, fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = { onInjectMoney(1_000_000_000L) },
                        modifier = Modifier.weight(1f).testTag("add_1b_btn")
                    ) {
                        Text("+Rp 1 Miliar", color = LglGreenPrimary, fontSize = 11.sp)
                    }
                }
            }
        }

        Text(
            text = "KATEDRAL ARMADA BUS (GARASI UNLOCKED)",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
        )

        // Bus Catalog List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
        ) {
            items(busCatalog) { bus ->
                val isSelected = bus.id == modState.selectedBusId
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectBus(bus.id) }
                        .border(
                            width = if (isSelected) 1.5.dp else 0.dp,
                            color = if (isSelected) LglGreenPrimary else Color.Transparent,
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
                            // Color Badge representing bus livery
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .background(bus.primaryColor, shape = RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DirectionsBus,
                                    contentDescription = null,
                                    tint = bus.secondaryColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = bus.name,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "[${bus.category}]",
                                        color = LglGreenPrimary,
                                        fontSize = 10.sp
                                    )
                                }
                                Text(
                                    text = "Livery: ${bus.liveryName}",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "Top Speed: ${bus.baseSpeedKmH} km/h | Power: ${bus.chassisPowerHp} HP",
                                    color = Color.Gray,
                                    fontSize = 9.sp
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Selected",
                                tint = LglGreenPrimary
                            )
                        } else {
                            Text(
                                text = "PAKAI",
                                color = TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
