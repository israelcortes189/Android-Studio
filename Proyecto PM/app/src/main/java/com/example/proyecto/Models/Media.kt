package com.example.proyecto.Models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "media",
    foreignKeys = [ForeignKey(
        entity = Tarea::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("tareaId"),
        onDelete = ForeignKey.CASCADE
    )]
)
data class Media(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val tareaId: Int,
    val uri: String,
    val tipo: String // "imagen" o "video"
)
