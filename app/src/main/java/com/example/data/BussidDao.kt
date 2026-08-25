package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BussidDao {
    @Query("SELECT * FROM banned_players ORDER BY bannedAt DESC")
    fun getAllBannedPlayers(): Flow<List<BannedPlayerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun banPlayer(player: BannedPlayerEntity)

    @Query("DELETE FROM banned_players WHERE id = :playerId")
    suspend fun unbanPlayer(playerId: String)

    @Query("SELECT * FROM cheat_presets ORDER BY id DESC")
    fun getCheatPresets(): Flow<List<CheatPresetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePreset(preset: CheatPresetEntity)
}
