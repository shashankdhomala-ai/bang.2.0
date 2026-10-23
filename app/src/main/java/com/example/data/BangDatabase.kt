package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Offline-first Room Database for BANG zero-knowledge encrypted messaging,
 * local thread caching, and store-and-forward packet queues.
 */
@Database(
    entities = [
        MessageEntity::class,
        ThreadEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class BangDatabase : RoomDatabase() {

    abstract fun messageDao(): MessageDao

    companion object {
        @Volatile
        private var INSTANCE: BangDatabase? = null

        private const val DATABASE_NAME = "bang_mesh_vault.db"

        fun getDatabase(context: Context): BangDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BangDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
