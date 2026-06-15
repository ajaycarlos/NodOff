package com.example.nodoff.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val actionType: String
)
