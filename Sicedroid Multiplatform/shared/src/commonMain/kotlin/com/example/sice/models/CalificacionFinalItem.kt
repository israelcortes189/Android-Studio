package com.example.sice.models

import kotlinx.serialization.Serializable

@Serializable
data class CalificacionFinalItem(
    val calif: Int,
    val acreditacion: String,
    val grupo: String,
    val materia: String,
    val observaciones: String
)

