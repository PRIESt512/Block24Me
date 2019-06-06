package com.example.block.db.entity

import androidx.room.RoomDatabase

abstract class AppDatabase : RoomDatabase() {
    abstract fun blockDao(): BlockDao
}