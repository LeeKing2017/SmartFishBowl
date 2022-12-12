package com.example.smartfishbowl.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [BowlData::class], version = 1)
abstract class BowlDatabase: RoomDatabase() {
    abstract fun bowlDao(): BowlDao

    companion object{
        private var database: BowlDatabase? = null
        private const val ROOM_DB = "room.db"

        fun getDatabase(context: Context): BowlDatabase{
            if (database == null){
                database = Room.databaseBuilder(
                    context.applicationContext, BowlDatabase::class.java, ROOM_DB)
                    .allowMainThreadQueries()
                    .build()
            }
            return database!!
        }
    }
}