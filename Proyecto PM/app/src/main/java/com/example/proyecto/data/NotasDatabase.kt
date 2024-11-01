package com.example.proyecto.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyecto.Models.Nota

@Database(entities=[Nota::class],
    version = 1, exportSchema = false
)
abstract class NotasDatabase : RoomDatabase() {
    abstract fun notaDao(): NotasDataBaseDao
}