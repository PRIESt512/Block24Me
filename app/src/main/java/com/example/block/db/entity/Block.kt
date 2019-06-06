package com.example.block.db.entity

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Block(@NonNull @PrimaryKey val url: String)