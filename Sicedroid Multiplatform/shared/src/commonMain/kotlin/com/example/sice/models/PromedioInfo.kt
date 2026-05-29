package com.example.sice.models

import kotlinx.serialization.Serializable
@Serializable
data class PromedioInfo(
    val promedioGral: Double? = null,
    val creditosAcumulados: Int? = null,
    val creditosPlan: Int? = null,
    val materiasCursadas: Int? = null,
    val materiasAprobadas: Int? = null,
    val avanceCreditos: Double? = null
)
@Serializable
data class CardexDto(
    val items: List<CardexItem> = emptyList(),
    val promedio: PromedioInfo? = null
)