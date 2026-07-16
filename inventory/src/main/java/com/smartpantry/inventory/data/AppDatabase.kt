package com.smartpantry.inventory.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.smartpantry.inventory.data.dao.MovementDao
import com.smartpantry.inventory.data.dao.ProductDao
import com.smartpantry.inventory.data.entity.MovementEntity
import com.smartpantry.inventory.data.entity.ProductEntity

@Database(
    entities = [ProductEntity::class, MovementEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun movementDao(): MovementDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_pantry.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}