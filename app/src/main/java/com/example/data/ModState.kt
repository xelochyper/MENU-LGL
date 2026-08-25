package com.example.data

data class ModState(
    // Currency Cheats
    val unlimitedMoney: Boolean = true,
    val moneyBalanceRp: Long = 999_999_999,
    val gemsBalance: Int = 888_888,

    // Vehicle Cheats
    val unlockAllBuses: Boolean = true,
    val selectedBusId: String = "bus_jb5",
    val customLiveryName: String = "LGL Cyber Neon Livery",
    val rainbowLiveryGlow: Boolean = true,
    val unlimitedFuel: Boolean = true,
    val noEngineDamage: Boolean = true,

    // Speed & Motion Cheats
    val speedHackMultiplier: Float = 5.0f, // 1x to 50x speed
    val nitroBoostEnabled: Boolean = true,
    val maxSpeedKmH: Int = 850,
    val currentSpeedKmH: Float = 0.0f,

    // Physics & Wallhack
    val wallhackEnabled: Boolean = true, // Pass through objects/buildings
    val flyHackEnabled: Boolean = false,
    val flyAltitudeMeters: Float = 10.0f,

    // Heavy Bus / Traffic Launch ("Bus Keras")
    val heavyBusEnabled: Boolean = true, // Super heavy bounce force
    val collisionForceMultiplier: Float = 15.0f, // Bus lain terlempar tinggi!
    val knockbackCount: Int = 0,

    // Teleportation
    val currentX: Float = -6.2145f,
    val currentY: Float = 106.9521f,
    val currentZ: Float = 18.5f,
    val currentLocationName: String = "Terminal Pulo Gebang",

    // Overlay Floating Menu
    val isFloatingMenuOpen: Boolean = true,
    val activeMenuTab: ModMenuTab = ModMenuTab.CHEATS,
    val floatingXOffsetDp: Int = 16,
    val floatingYOffsetDp: Int = 80,

    // Server Admin Session
    val onlineRoomName: String = "BUSSID_SERVER_ID_ROOM_90",
    val isAdmin: Boolean = true,
    val adminNoticeText: String = "LGL MOD MENU ACTIVE - FULL ADMIN ACCESS"
)

enum class ModMenuTab {
    CHEATS,       // Speed, Wallhack, Heavy Bus, Flying
    GARAGE,       // Unlock All Buses, Money Injector
    TELEPORT,     // Instant Location Teleport Map
    PHYSICS,      // Super Heavy Collision & Speed Tuning
    ADMIN_PANEL,  // Banned User Accounts & Online Lobby Manager
    SIMULATOR     // Driving HUD & Canvas Control
}

data class PlayerOnline(
    val id: String,
    val name: String,
    val busModel: String,
    val pingMs: Int,
    val currentSpeedKmH: Int,
    val isVIP: Boolean = false,
    val ipAddress: String = "182.253.11.89"
)

object DefaultOnlinePlayers {
    val initialList = listOf(
        PlayerOnline("ID-7821", "Budi_Suroboyo_Driver", "Avante H9", 24, 110),
        PlayerOnline("ID-9012", "Randi_Jabar_Oleng", "Truck Canter", 42, 145),
        PlayerOnline("ID-3341", "Cheater_FakeMod_99", "Unlicensed Bus", 150, 420),
        PlayerOnline("ID-5510", "SinarJaya_Mania", "Jetbus 5 HDD", 18, 95),
        PlayerOnline("ID-2290", "Troller_Bussid_X", "SR3 Double Decker", 89, 210),
        PlayerOnline("ID-4412", "Rosalia_Sultan", "Tourismo Mercedes", 31, 105),
        PlayerOnline("ID-6678", "Spammer_Bot_01", "Unknown Vehicle", 310, 0)
    )
}
