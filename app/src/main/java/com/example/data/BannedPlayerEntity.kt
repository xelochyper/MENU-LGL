package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banned_players")
data class BannedPlayerEntity(
    @PrimaryKey val id: String,
    val username: String,
    val busType: String,
    val pingMs: Int,
    val reason: String,
    val bannedAt: Long = System.currentTimeMillis(),
    val adminName: String = "Admin_LGL"
)

@Entity(tableName = "cheat_presets")
data class CheatPresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val unlimitedMoney: Boolean,
    val unlockAllBuses: Boolean,
    val speedMultiplier: Float,
    val wallhackEnabled: Boolean,
    val heavyBusCollision: Boolean,
    val heavyBusForce: Float
)
