package com.example.sice.data.datbase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sice.data.Entityes.CalificacionFinalEntity
import com.example.sice.data.Entityes.CalificacionUnidadEntity
import com.example.sice.data.Entityes.CardexEntity
import com.example.sice.data.Entityes.CargaEntity
import com.example.sice.data.Entityes.ProfileEntity
import com.example.sice.data.dao.CalificacionFinalDao
import com.example.sice.data.dao.CalificacionUnidadDao
import com.example.sice.data.dao.CardexDao
import com.example.sice.data.dao.CargaDao
import com.example.sice.data.dao.ProfileDao

/**
 * Clase de base de datos con patrón Singleton.
 */
@Database(
    entities = [ProfileEntity::class, CardexEntity::class, CargaEntity::class, CalificacionUnidadEntity::class, CalificacionFinalEntity::class],
    version = 1, //
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun profileDao(): ProfileDao
    abstract fun cardexDao(): CardexDao
    abstract fun cargaDao(): CargaDao
    abstract fun CalificacionUnidadDao(): CalificacionUnidadDao
    abstract fun CalificacionFinalDao(): CalificacionFinalDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sicenet_database"
                )
                    .fallbackToDestructiveMigration() // destruye y recrea si cambia el esquema
                    .build()
                    .also { Instance = it }
            }
        }
    }
}

