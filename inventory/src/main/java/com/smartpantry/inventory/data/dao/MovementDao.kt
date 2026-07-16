package com.smartpantry.inventory.data.dao

import com.smartpantry.inventory.data.entity.MovementEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface MovementDao {
    @Insert
    suspend fun insertMovement(entity: MovementEntity): Long

    @Query("SELECT * FROM movements WHERE productId = :productId ORDER BY timestamp DESC")
    fun getMovements(productId: Long): Flow<List<MovementEntity>>

    @Query("SELECT * FROM movements WHERE productId = :productId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentMovements(productId: Long, limit: Int): Flow<List<MovementEntity>>
}