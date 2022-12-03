package com.example.smartfishbowl.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query

@Dao
interface BowlDao {
    @Query("SELECT * FROM bowl")
    fun getAllId(): List<BowlData>

    @Insert(onConflict = REPLACE)
    fun insertBowl(bowlData: BowlData)

    @Delete
    fun deleteBowl(bowlData: BowlData)

    @Query("DELETE FROM bowl")
    fun deleteAll()
}