package com.example.block.db.entity

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BlockDao {

    @Query("SELECT * FROM block")
    fun getAll(): List<Block>

    @Insert
    fun insert(vararg blocks: Block)

    @Query("DELETE FROM block")
    fun deleteAll()
}