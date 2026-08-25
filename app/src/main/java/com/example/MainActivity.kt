package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.LglViewModel
import com.example.ui.components.BussidDrivingCanvas
import com.example.ui.components.LglFloatingButton
import com.example.ui.components.LglFloatingMenuOverlay
import com.example.ui.theme.LglModMenuTheme
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {

    private val viewModel: LglViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            LglModMenuTheme {
                val modState by viewModel.modState.collectAsStateWithLifecycle()
                val onlinePlayers by viewModel.onlinePlayers.collectAsStateWithLifecycle()
                val bannedPlayers by viewModel.bannedPlayers.collectAsStateWithLifecycle()
                val lastMessage by viewModel.lastActionMessage.collectAsStateWithLifecycle()
                val trafficLaunchY by viewModel.trafficBusLaunchY.collectAsStateWithLifecycle()

                val selectedBus = viewModel.busCatalog.find { it.id == modState.selectedBusId }
                    ?: viewModel.busCatalog.first()

                // Draggable positions for floating button and overlay window
                var offsetX by remember { mutableFloatStateOf(16f) }
                var offsetY by remember { mutableFloatStateOf(120f) }

                LaunchedEffect(Unit) {
                    while (true) {
                        viewModel.updateTrafficAnimation()
                        kotlinx.coroutines.delay(20)
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // 1. Live Driving Simulator Canvas View (BUSSID Environment)
                        BussidDrivingCanvas(
                            modState = modState,
                            selectedBus = selectedBus,
                            trafficLaunchY = trafficLaunchY,
                            onTriggerCollision = { viewModel.triggerTrafficBusCollision() },
                            onSpeedUpdate = { viewModel.updateSpeedReading(it) },
                            onToggleFloatingMenu = { viewModel.toggleFloatingMenu() }
                        )

                        // 2. Floating LGL Logo Badge Button (Draggable)
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                                .pointerInput(Unit) {
                                    detectDragGestures { change, dragAmount ->
                                        change.consume()
                                        offsetX += dragAmount.x
                                        offsetY += dragAmount.y
                                    }
                                }
                        ) {
                            LglFloatingButton(
                                isOpen = modState.isFloatingMenuOpen,
                                onClick = { viewModel.toggleFloatingMenu() }
                            )
                        }

                        // 3. Floating Mod Menu Panel Overlay
                        AnimatedVisibility(
                            visible = modState.isFloatingMenuOpen,
                            enter = fadeIn() + slideInVertically { it / 2 },
                            exit = fadeOut() + slideOutVertically { it / 2 },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            LglFloatingMenuOverlay(
                                modState = modState,
                                busCatalog = viewModel.busCatalog,
                                teleportLocations = viewModel.teleportLocations,
                                onlinePlayers = onlinePlayers,
                                bannedPlayers = bannedPlayers,
                                lastMessage = lastMessage,
                                onClose = { viewModel.toggleFloatingMenu() },
                                onSelectTab = { viewModel.setActiveTab(it) },
                                onToggleMoney = { viewModel.toggleUnlimitedMoney() },
                                onToggleWallhack = { viewModel.toggleWallhack() },
                                onToggleHeavyBus = { viewModel.toggleHeavyBus() },
                                onUpdateSpeedMultiplier = { viewModel.updateSpeedHack(it) },
                                onToggleNitro = { viewModel.toggleNitro() },
                                onToggleUnlockAllBuses = { viewModel.toggleUnlockAllBuses() },
                                onSelectBus = { viewModel.selectBus(it) },
                                onInjectMoney = { viewModel.addMoney(it) },
                                onTeleportTo = { viewModel.teleportTo(it) },
                                onUpdateCollisionForce = { viewModel.updateCollisionForce(it) },
                                onTriggerImpactTest = { viewModel.triggerTrafficBusCollision() },
                                onBanPlayer = { player, reason -> viewModel.banUser(player, reason) },
                                onUnbanPlayer = { id -> viewModel.unbanUser(id) },
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
