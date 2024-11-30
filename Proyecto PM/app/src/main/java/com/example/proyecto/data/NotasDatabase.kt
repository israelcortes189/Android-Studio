package com.example.proyecto.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyecto.Models.Nota
import com.example.proyecto.Models.Tarea

@Database(entities=[Nota::class, Tarea::class],
    version = 2, exportSchema = false
)
abstract class NotasDatabase : RoomDatabase() {
    abstract fun notaDao(): NotasDataBaseDao
}