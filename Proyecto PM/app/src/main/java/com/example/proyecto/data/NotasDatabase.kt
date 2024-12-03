package com.example.proyecto.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.proyecto.Converters
import com.example.proyecto.Models.Media
import com.example.proyecto.Models.Nota
import com.example.proyecto.Models.Tarea

@Database(entities = [Nota::class, Tarea::class, Media::class], version = 6, exportSchema = false)
@TypeConverters(Converters::class)
abstract class NotasDatabase : RoomDatabase() {
    abstract fun notaDao(): NotasDataBaseDao
}