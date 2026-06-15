package com.example.nodoff.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [EventEntity::class], version = 1, exportSchema = false)
abstract class NodOffDatabase : RoomDatabase() {
    abstract fun eventDao(): EventDao

    companion object {
        @Volatile
        private var INSTANCE: NodOffDatabase? = null

        fun getDatabase(context: Context): NodOffDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NodOffDatabase::class.java,
                    "nodoff_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
