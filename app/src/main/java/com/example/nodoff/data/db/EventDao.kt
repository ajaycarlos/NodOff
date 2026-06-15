package com.example.nodoff.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface EventDao {
    @Query("SELECT * FROM sleep_events ORDER BY timestamp DESC")
    fun getAllEvents(): Flow<List<EventEntity>>

    @Insert
    suspend fun insertEvent(event: EventEntity)
}
