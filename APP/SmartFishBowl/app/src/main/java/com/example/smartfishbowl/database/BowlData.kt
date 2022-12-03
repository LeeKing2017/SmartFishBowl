package com.example.smartfishbowl.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bowl")
class BowlData (
    @PrimaryKey var bowlId: String,
    @ColumnInfo var userId: String,
)