package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class LglViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = BussidRepository(db.bussidDao())

    private val _modState = MutableStateFlow(ModState())
    val modState: StateFlow<ModState> = _modState.asStateFlow()

    private val _onlinePlayers = MutableStateFlow(DefaultOnlinePlayers.initialList)
    val onlinePlayers: StateFlow<List<PlayerOnline>> = _onlinePlayers.asStateFlow()

    val bannedPlayers: StateFlow<List<BannedPlayerEntity>> = repository.bannedPlayers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val busCatalog: List<BusModel> = DefaultBusses.list
    val teleportLocations: List<TeleportLocation> = DefaultLocations.list

    // Driving Simulation Canvas State
    private val _busPositionX = MutableStateFlow(200f)
    val busPositionX: StateFlow<Float> = _busPositionX.asStateFlow()

    private val _busPositionY = MutableStateFlow(400f)
    val busPositionY: StateFlow<Float> = _busPositionY.asStateFlow()

    private val _busRotation = MutableStateFlow(0f)
    val busRotation: StateFlow<Float> = _busRotation.asStateFlow()

    // Traffic Buses in driving simulation
    private val _trafficBusLaunchY = MutableStateFlow(0f)
    val trafficBusLaunchY: StateFlow<Float> = _trafficBusLaunchY.asStateFlow()

    private val _trafficLaunchVelocity = MutableStateFlow(0f)
    val trafficLaunchVelocity: StateFlow<Float> = _trafficLaunchVelocity.asStateFlow()

    private val _lastActionMessage = MutableStateFlow("Mod Menu LGL BUSSID Ready")
    val lastActionMessage: StateFlow<String> = _lastActionMessage.asStateFlow()

    init {
        // Pre-populate DB with an example banned player for demonstration if empty
        viewModelScope.launch {
            repository.bannedPlayers.firstOrNull()?.let { list ->
                if (list.isEmpty()) {
                    repository.banPlayer(
                        BannedPlayerEntity(
                            id = "ID-99001",
                            username = "Toxic_Spammer_99",
                            busType = "Stock Bus",
                            pingMs = 420,
                            reason = "Wallhack Abuse & Spam In Room Chat",
                            adminName = "LGL_Admin"
                        )
                    )
                }
            }
        }
    }

    fun setMessage(msg: String) {
        _lastActionMessage.value = msg
    }

    fun toggleUnlimitedMoney() {
        val current = _modState.value.unlimitedMoney
        val newMoney = if (!current) 999_999_999L else 125_000L
        _modState.update {
            it.copy(
                unlimitedMoney = !current,
                moneyBalanceRp = newMoney
            )
        }
        setMessage(if (!current) "⚡ Unlimited Money Injected: Rp 999,999,999" else "Money Cheat Deactivated")
    }

    fun addMoney(amount: Long) {
        _modState.update {
            it.copy(moneyBalanceRp = it.moneyBalanceRp + amount)
        }
        setMessage("➕ Injected +Rp ${String.format("%,d", amount)}")
    }

    fun toggleUnlockAllBuses() {
        val current = _modState.value.unlockAllBuses
        _modState.update { it.copy(unlockAllBuses = !current) }
        setMessage(if (!current) "🚌 All BUSSID Buses & Mods Unlocked (100% Free)" else "Bus Unlock Cheat Toggled")
    }

    fun selectBus(busId: String) {
        val bus = busCatalog.find { it.id == busId }
        _modState.update { it.copy(selectedBusId = busId) }
        setMessage("🚌 Selected Vehicle: ${bus?.name ?: busId}")
    }

    fun updateSpeedHack(speedMultiplier: Float) {
        _modState.update {
            it.copy(
                speedHackMultiplier = speedMultiplier,
                maxSpeedKmH = (180 * speedMultiplier).toInt()
            )
        }
        setMessage("⚡ Speed Hack Multiplier Set to ${String.format("%.1f", speedMultiplier)}x (${(180 * speedMultiplier).toInt()} km/h)")
    }

    fun toggleWallhack() {
        val current = _modState.value.wallhackEnabled
        _modState.update { it.copy(wallhackEnabled = !current) }
        setMessage(if (!current) "👻 Wallhack / Noclip ENABLED - Pass Through Mountains & Buildings" else "👻 Wallhack DISABLED")
    }

    fun toggleHeavyBus() {
        val current = _modState.value.heavyBusEnabled
        _modState.update { it.copy(heavyBusEnabled = !current) }
        setMessage(if (!current) "💥 Bus Keras ACTIVATED - Traffic Collision Launch Enabled!" else "💥 Bus Keras Normal Force")
    }

    fun updateCollisionForce(force: Float) {
        _modState.update { it.copy(collisionForceMultiplier = force) }
        setMessage("💥 Impact Launch Force set to ${force.toInt()}x Multiplier")
    }

    fun toggleNitro() {
        val current = _modState.value.nitroBoostEnabled
        _modState.update { it.copy(nitroBoostEnabled = !current) }
        setMessage(if (!current) "🚀 Nitro Turbo Boost ON" else "🚀 Nitro Turbo Boost OFF")
    }

    fun teleportTo(location: TeleportLocation) {
        _modState.update {
            it.copy(
                currentX = location.coordX,
                currentY = location.coordY,
                currentZ = location.coordZ,
                currentLocationName = location.name
            )
        }
        setMessage("📍 Teleported to ${location.name} (${location.region})")
    }

    fun triggerTrafficBusCollision() {
        val force = _modState.value.collisionForceMultiplier
        if (_modState.value.heavyBusEnabled) {
            _trafficLaunchVelocity.value = -80f * (force / 10f)
            _modState.update { it.copy(knockbackCount = it.knockbackCount + 1) }
            setMessage("💥 BOOM! Traffic Bus Terlempar High Launch into Sky! (Force: ${force.toInt()}x)")
        } else {
            setMessage("💥 Collided with Traffic Bus (Normal impact force)")
        }
    }

    fun updateTrafficAnimation() {
        if (_trafficLaunchVelocity.value != 0f || _trafficBusLaunchY.value < 0f) {
            _trafficBusLaunchY.value += _trafficLaunchVelocity.value
            _trafficLaunchVelocity.value += 4f // Gravity bringing bus down
            if (_trafficBusLaunchY.value >= 0f) {
                _trafficBusLaunchY.value = 0f
                _trafficLaunchVelocity.value = 0f
            }
        }
    }

    fun banUser(player: PlayerOnline, reason: String) {
        viewModelScope.launch {
            repository.banPlayer(
                BannedPlayerEntity(
                    id = player.id,
                    username = player.name,
                    busType = player.busModel,
                    pingMs = player.pingMs,
                    reason = reason,
                    adminName = "Admin_LGL"
                )
            )
            _onlinePlayers.update { list -> list.filter { it.id != player.id } }
            setMessage("🔨 ADMIN ACTION: Banned ${player.name} (${player.id}) - $reason")
        }
    }

    fun unbanUser(playerId: String) {
        viewModelScope.launch {
            repository.unbanPlayer(playerId)
            setMessage("✅ Admin Unbanned Account $playerId")
        }
    }

    fun toggleFloatingMenu() {
        _modState.update { it.copy(isFloatingMenuOpen = !it.isFloatingMenuOpen) }
    }

    fun setActiveTab(tab: ModMenuTab) {
        _modState.update { it.copy(activeMenuTab = tab) }
    }

    fun updateSpeedReading(speed: Float) {
        _modState.update { it.copy(currentSpeedKmH = speed) }
    }
}
