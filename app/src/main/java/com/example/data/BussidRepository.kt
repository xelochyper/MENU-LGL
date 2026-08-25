package com.example.data

import kotlinx.coroutines.flow.Flow

class BussidRepository(private val dao: BussidDao) {

    val bannedPlayers: Flow<List<BannedPlayerEntity>> = dao.getAllBannedPlayers()
    val presets: Flow<List<CheatPresetEntity>> = dao.getCheatPresets()

    suspend fun banPlayer(player: BannedPlayerEntity) {
        dao.banPlayer(player)
    }

    suspend fun unbanPlayer(id: String) {
        dao.unbanPlayer(id)
    }

    suspend fun savePreset(preset: CheatPresetEntity) {
        dao.savePreset(preset)
    }
}
