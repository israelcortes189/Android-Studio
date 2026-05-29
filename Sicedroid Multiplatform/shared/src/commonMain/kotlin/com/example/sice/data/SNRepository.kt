/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.sice.data

import com.example.sice.models.CalificacionFinalItem
import com.example.sice.models.CalificacionUnidadItem
import com.example.sice.models.CardexItem
import com.example.sice.models.CargaItem
import com.example.sice.models.ProfileStudent
import com.example.sice.models.PromedioInfo

/**
 * Repository interface para SICENET
 */
interface SNRepository {
    suspend fun acceso(m: String, p: String): String
    suspend fun profile(): ProfileStudent?
    fun hasSession(): Boolean
    fun logout()

    fun getCurrentMatricula(): String?
    fun setCurrentMatricula(matricula: String?)

    // Métodos remotos usados por MainRepository
    suspend fun cardex(lineamiento: Int): Pair<List<CardexItem>, PromedioInfo>?
    suspend fun cargaAcademica(): List<CargaItem>?
    suspend fun calificacionesPorUnidad(): List<CalificacionUnidadItem>?
    suspend fun calificacionFinal(modEducativo: Int): List<CalificacionFinalItem>?
}