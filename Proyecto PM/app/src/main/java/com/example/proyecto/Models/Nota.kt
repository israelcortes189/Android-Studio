package com.example.proyecto.Models

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "notas")
data class Nota(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion : String,
    val fechaRecordatorio: LocalDateTime? = null
)
