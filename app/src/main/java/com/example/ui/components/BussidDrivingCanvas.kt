package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BusModel
import com.example.data.ModState
import com.example.ui.theme.CyberDarkBackground
import com.example.ui.theme.LglGreenPrimary
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.delay

@Composable
fun BussidDrivingCanvas(
    modState: ModState,
    selectedBus: BusModel,
    trafficLaunchY: Float,
    onTriggerCollision: () -> Unit,
    onSpeedUpdate: (Float) -> Unit,
    onToggleFloatingMenu: () -> Unit
) {
    var busX by remember { mutableFloatStateOf(0.5f) } // Normalized 0.0 to 1.0 (0.5 is lane center)
    var busSpeedKmH by remember { mutableFloatStateOf(0f) }
    var isAccelerating by remember { mutableStateOf(false) }
    var isBraking by remember { mutableStateOf(false) }
    var steerLeft by remember { mutableStateOf(false) }
    var steerRight by remember { mutableStateOf(false) }
    var roadOffset by remember { mutableFloatStateOf(0f) }

    // Driving physics loop
    LaunchedEffect(isAccelerating, isBraking, steerLeft, steerRight, modState.speedHackMultiplier) {
        while (true) {
            val maxSpeed = 180f * modState.speedHackMultiplier
            val accelPower = if (modState.nitroBoostEnabled) 12f * modState.speedHackMultiplier else 5f * modState.speedHackMultiplier

            if (isAccelerating) {
                busSpeedKmH = (busSpeedKmH + accelPower).coerceAtMost(maxSpeed)
            } else if (isBraking) {
                busSpeedKmH = (busSpeedKmH - 15f).coerceAtLeast(0f)
            } else {
                busSpeedKmH = (busSpeedKmH - 2f).coerceAtLeast(0f)
            }

            // Steering
            if (steerLeft) {
                busX = (busX - 0.02f).coerceAtLeast(if (modState.wallhackEnabled) -0.2f else 0.15f)
            }
            if (steerRight) {
                busX = (busX + 0.02f).coerceAtMost(if (modState.wallhackEnabled) 1.2f else 0.85f)
            }

            // Animate road stripes according to speed
            roadOffset = (roadOffset + busSpeedKmH / 10f) % 100f

            onSpeedUpdate(busSpeedKmH)
            delay(30)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberDarkBackground)
    ) {
        // 2D/3D Highway Canvas View
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .testTag("bussid_canvas")
        ) {
            val w = size.width
            val h = size.height

            // Sky & Horizon
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF0284C7)
                    ),
                    startY = 0f,
                    endY = h * 0.45f
                ),
                size = Size(w, h * 0.45f)
            )

            // Mountains on Sides (Wallhack target test)
            val mtnPath = Path().apply {
                moveTo(0f, h * 0.45f)
                lineTo(w * 0.2f, h * 0.25f)
                lineTo(w * 0.35f, h * 0.45f)
                lineTo(w * 0.6f, h * 0.2f)
                lineTo(w * 0.85f, h * 0.45f)
                lineTo(w, h * 0.3f)
                lineTo(w, h * 0.45f)
                close()
            }
            drawPath(
                path = mtnPath,
                color = if (modState.wallhackEnabled) Color(0xFF00FF66).copy(alpha = 0.25f) else Color(0xFF0F2942)
            )

            // Highway Asphalt Ground (Perspective Trapezoid)
            val roadPath = Path().apply {
                moveTo(w * 0.4f, h * 0.45f)
                lineTo(w * 0.6f, h * 0.45f)
                lineTo(w * 0.95f, h)
                lineTo(w * 0.05f, h)
                close()
            }
            drawPath(
                path = roadPath,
                color = Color(0xFF1E293B)
            )

            // Side Grass / Terrain
            val leftGrass = Path().apply {
                moveTo(0f, h * 0.45f)
                lineTo(w * 0.4f, h * 0.45f)
                lineTo(w * 0.05f, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = leftGrass,
                color = if (busX < 0.15f && modState.wallhackEnabled) Color(0xFF00FF66).copy(alpha = 0.4f) else Color(0xFF064E3B)
            )

            val rightGrass = Path().apply {
                moveTo(w * 0.6f, h * 0.45f)
                lineTo(w, h * 0.45f)
                lineTo(w, h)
                lineTo(w * 0.95f, h)
                close()
            }
            drawPath(
                path = rightGrass,
                color = if (busX > 0.85f && modState.wallhackEnabled) Color(0xFF00FF66).copy(alpha = 0.4f) else Color(0xFF064E3B)
            )

            // Animated Center Road Markings
            for (i in 0..8) {
                val progress = ((i * 50f + roadOffset) % 400f) / 400f
                val stripeY = h * 0.45f + progress * (h * 0.55f)
                val stripeWidth = 4f + progress * 24f
                val stripeHeight = 10f + progress * 40f
                val stripeX = w * 0.5f - stripeWidth / 2f

                drawRect(
                    color = Color.Yellow,
                    topLeft = Offset(stripeX, stripeY),
                    size = Size(stripeWidth, stripeHeight)
                )
            }

            // Traffic Bus Ahead (Target for "Bus Keras" collision test)
            val trafficYBase = h * 0.65f + trafficLaunchY
            val trafficWidth = w * 0.18f
            val trafficHeight = h * 0.12f
            val trafficX = w * 0.5f - trafficWidth / 2f

            // Draw Traffic Bus
            drawRoundRect(
                color = if (trafficLaunchY < 0f) Color(0xFFFF2A55) else Color(0xFFEAB308),
                topLeft = Offset(trafficX, trafficYBase),
                size = Size(trafficWidth, trafficHeight),
                cornerRadius = CornerRadius(12f, 12f)
            )
            // Windows
            drawRect(
                color = Color.Cyan,
                topLeft = Offset(trafficX + 10f, trafficYBase + 10f),
                size = Size(trafficWidth - 20f, trafficHeight * 0.35f)
            )

            // Launch Sky Trail Effect when "Bus Keras" triggers
            if (trafficLaunchY < -10f) {
                drawLine(
                    color = Color(0xFFFF2A55),
                    start = Offset(w * 0.5f, h * 0.65f),
                    end = Offset(w * 0.5f, trafficYBase + trafficHeight),
                    strokeWidth = 10f
                )
            }

            // User's Controlled Bus (Bottom center)
            val userBusWidth = w * 0.28f
            val userBusHeight = h * 0.18f
            val userBusX = busX * w - userBusWidth / 2f
            val userBusY = h * 0.76f

            // Wallhack Ghost Glow if Offroad
            if (modState.wallhackEnabled) {
                drawRoundRect(
                    color = Color(0xFF00FF66).copy(alpha = 0.3f),
                    topLeft = Offset(userBusX - 10f, userBusY - 10f),
                    size = Size(userBusWidth + 20f, userBusHeight + 20f),
                    cornerRadius = CornerRadius(20f, 20f)
                )
            }

            // User Bus Body
            drawRoundRect(
                color = selectedBus.primaryColor,
                topLeft = Offset(userBusX, userBusY),
                size = Size(userBusWidth, userBusHeight),
                cornerRadius = CornerRadius(18f, 18f)
            )

            // Bus Roof & Windshield
            drawRoundRect(
                color = selectedBus.secondaryColor,
                topLeft = Offset(userBusX + 12f, userBusY + 12f),
                size = Size(userBusWidth - 24f, userBusHeight * 0.35f),
                cornerRadius = CornerRadius(8f, 8f)
            )

            // Tail Lights
            drawCircle(
                color = Color.Red,
                radius = 12f,
                center = Offset(userBusX + 24f, userBusY + userBusHeight - 16f)
            )
            drawCircle(
                color = Color.Red,
                radius = 12f,
                center = Offset(userBusX + userBusWidth - 24f, userBusY + userBusHeight - 16f)
            )

            // LGL Mod Watermark on Canvas
            drawRect(
                color = Color.Black.copy(alpha = 0.5f),
                topLeft = Offset(20f, 20f),
                size = Size(360f, 110f)
            )
        }

        // HUD Top Banner Overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, start = 12.dp, end = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mod Badge
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.border(1.dp, LglGreenPrimary, RoundedCornerShape(8.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("LGL MOD MENU v3.8", color = LglGreenPrimary, fontWeight = FontWeight.Black, fontSize = 11.sp)
                        Text("| BUSSID", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }

                // Money HUD
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Rp ${String.format("%,d", modState.moneyBalanceRp)}",
                        color = Color(0xFFFACC15),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Active Cheat Pills
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (modState.wallhackEnabled) {
                    CheatBadge("GHOST WALLHACK")
                }
                if (modState.heavyBusEnabled) {
                    CheatBadge("BUS KERAS (${modState.collisionForceMultiplier.toInt()}x)")
                }
                if (modState.speedHackMultiplier > 1f) {
                    CheatBadge("SPEED HACK ${String.format("%.1f", modState.speedHackMultiplier)}x")
                }
            }
        }

        // Driving Cockpit Controls HUD at Bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.95f))
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                // Left Controls: Steering Buttons & Basuri Horn
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .size(62.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { steerLeft = true },
                                        onDragEnd = { steerLeft = false },
                                        onDragCancel = { steerLeft = false },
                                        onDrag = { _, _ -> }
                                    )
                                }
                                .testTag("steer_left_btn"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Steer Left", tint = Color.White)
                        }

                        Button(
                            onClick = { },
                            modifier = Modifier
                                .size(62.dp)
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = { steerRight = true },
                                        onDragEnd = { steerRight = false },
                                        onDragCancel = { steerRight = false },
                                        onDrag = { _, _ -> }
                                    )
                                }
                                .testTag("steer_right_btn"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Steer Right", tint = Color.White)
                        }
                    }

                    // Basuri Horn Button
                    Button(
                        onClick = { },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.width(134.dp).testTag("telolet_horn_btn")
                    ) {
                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("TELOLET BASURI", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Center Gauge: Speedometer & Launch Force Test
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Speedometer Display
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                            .border(2.dp, LglGreenPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${busSpeedKmH.toInt()}",
                                color = LglGreenPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp
                            )
                            Text("KM/H", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Trigger Collision "Bus Keras" Launch Button
                    Button(
                        onClick = onTriggerCollision,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF2A55)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("ram_traffic_btn")
                    ) {
                        Text("TABRAK BUS TRAFFIC", color = Color.White, fontWeight = FontWeight.Black, fontSize = 9.sp)
                    }
                }

                // Right Controls: Throttle Gas & Brake
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = { },
                        modifier = Modifier
                            .size(62.dp)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { isBraking = true },
                                    onDragEnd = { isBraking = false },
                                    onDragCancel = { isBraking = false },
                                    onDrag = { _, _ -> }
                                )
                            }
                            .testTag("brake_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("REM", color = Color.White, fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { },
                        modifier = Modifier
                            .size(68.dp)
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = { isAccelerating = true },
                                    onDragEnd = { isAccelerating = false },
                                    onDragCancel = { isAccelerating = false },
                                    onDrag = { _, _ -> }
                                )
                            }
                            .testTag("gas_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = LglGreenPrimary),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("GAS", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CheatBadge(text: String) {
    Box(
        modifier = Modifier
            .background(LglGreenPrimary.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
            .border(1.dp, LglGreenPrimary, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text = text, color = LglGreenPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}
