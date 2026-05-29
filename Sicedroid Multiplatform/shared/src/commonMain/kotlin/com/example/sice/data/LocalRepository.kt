package com.example.sice.data

import com.example.sice.models.CalificacionFinalItem
import com.example.sice.models.CalificacionUnidadItem
import com.example.sice.models.CardexItem
import com.example.sice.models.CargaItem
import com.example.sice.models.ProfileStudent
import kotlinx.coroutines.flow.Flow

interface LocalRepository {
    fun getCardex(matricula: String): Flow<List<CardexItem>>
    suspend fun insertCardex(matricula: String, items: List<CardexItem>): Result<Unit>

    fun getCarga(matricula: String): Flow<List<CargaItem>>
    suspend fun insertCarga(matricula: String, items: List<CargaItem>): Result<Unit>

    fun getCalificaciones(matricula: String): Flow<List<CalificacionUnidadItem>>
    suspend fun insertCalificaciones(matricula: String, items: List<CalificacionUnidadItem>): Result<Unit>

    fun getCalificacionFinal(matricula: String): Flow<List<CalificacionFinalItem>>
    suspend fun insertCalificacionFinal(matricula: String, items: List<CalificacionFinalItem>): Result<Unit>

    // Perfil
    fun getProfile(matricula: String): Flow<ProfileStudent?>
    suspend fun insertProfile(profile: ProfileStudent): Result<Unit>
}
