package com.example.sice.data.Entityes


import androidx.room.Entity

@Entity(tableName = "calificaciones_finales",
        primaryKeys = ["matricula","materia","grupo"])
data class CalificacionFinalEntity(
    val matricula: String,
    val materia: String,
    val grupo: String,
    val calif: Int,
    val acreditacion: String,
    val observaciones: String
)

