package com.example.block.db.entity

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Block::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun blockDao(): BlockDao

    companion object {
        @Volatile
        private var INSTANSE: AppDatabase? = null

        fun instanceDatabase(context: Context): AppDatabase {
            val tempInstance = INSTANSE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java, "blockUrl"
                ).build()
                INSTANSE = instance
                return instance
            }
        }

        fun getDatabase(): AppDatabase {
            if (INSTANSE != null) {
                return INSTANSE!!
            } else {
                throw Exception("Необходимо выполнить инициализацию БД")
            }
        }
    }
}