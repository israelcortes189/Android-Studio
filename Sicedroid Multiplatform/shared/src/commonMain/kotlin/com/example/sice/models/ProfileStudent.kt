package com.example.sice.models

import kotlinx.serialization.Serializable

@Serializable
data class ProfileStudent(
    val matricula: String,
    val nombre: String = "",
    val carrera: String = "",
    val semActual: Int = 0,
    val cdtosAcumulados: Int = 0
)
