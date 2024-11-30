package com.example.proyecto.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tareas")
data class Tarea(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val descripcion: String,
    val imagenUri: String? = null, // Campo para la URI de la imagen
    val videoUri: String? = null, // Campo para la URI del video
)