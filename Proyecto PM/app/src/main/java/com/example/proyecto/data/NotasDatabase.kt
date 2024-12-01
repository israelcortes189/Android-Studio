package com.example.proyecto.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyecto.Models.Media
import com.example.proyecto.Models.Nota
import com.example.proyecto.Models.Tarea

@Database(entities = [Nota::class, Tarea::class, Media::class], version = 5, exportSchema = false)
abstract class NotasDatabase : RoomDatabase() {
    abstract fun notaDao(): NotasDataBaseDao
}