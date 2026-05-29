package com.example.sice.data

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
import com.example.sice.models.CalificacionFinalItem
import com.example.sice.models.CalificacionUnidadItem
import com.example.sice.models.CardexItem
import com.example.sice.models.CargaItem
import com.example.sice.models.ProfileStudent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomLocalRepository(
    private val profileDao: ProfileDao,
    private val cardexDao: CardexDao,
    private val cargaDao: CargaDao,
    private val calificacionUnidadDao: CalificacionUnidadDao,
    private val calificacionFinalDao: CalificacionFinalDao
) : LocalRepository {

    override suspend fun insertCardex(matricula: String, items: List<CardexItem>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val entities = items.map { item ->
                    CardexEntity(
                        matricula = matricula,
                        claveMateria = item.claveMateria,
                        claveOficial = item.claveOficial,
                        materia = item.materia,
                        creditos = item.creditos,
                        calificacion = item.calificacion,
                        acreditacion = item.acreditacion,
                        semestre = item.semestre,
                        periodo = item.periodo,
                        anio = item.anio
                    )
                }
                cardexDao.insertCardex(entities)
                Result.success(Unit)
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }
    }

    override fun getCardex(matricula: String): Flow<List<CardexItem>> =
        cardexDao.getCardexByMatricula(matricula).map { list ->
            list.map { entity ->
                CardexItem(
                    claveMateria = entity.claveMateria,
                    claveOficial = entity.claveOficial,
                    materia = entity.materia,
                    creditos = entity.creditos,
                    calificacion = entity.calificacion,
                    acreditacion = entity.acreditacion,
                    semestre = entity.semestre,
                    periodo = entity.periodo,
                    anio = entity.anio
                )
            }
        }

    override suspend fun insertCarga(matricula: String, items: List<CargaItem>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val entities = items.map { item ->
                    CargaEntity(
                        matricula = matricula,
                        claveOficial = item.clvOficial,
                        materia = item.materia,
                        grupo = item.grupo,
                        docente = item.docente,
                        creditos = item.creditos,
                        estadoMateria = item.estadoMateria,
                        observaciones = item.observaciones,
                        semipresencial = item.semipresencial,
                        lunes = item.lunes,
                        martes = item.martes,
                        miercoles = item.miercoles,
                        jueves = item.jueves,
                        viernes = item.viernes,
                        sabado = item.sabado
                    )
                }
                cargaDao.insertCarga(entities)
                Result.success(Unit)
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }
    }

    override fun getCarga(matricula: String): Flow<List<CargaItem>> =
        cargaDao.getCargaByMatricula(matricula).map { list ->
            list.map { entity ->
                CargaItem(
                    clvOficial = entity.claveOficial,
                    materia = entity.materia,
                    grupo = entity.grupo,
                    docente = entity.docente,
                    creditos = entity.creditos,
                    estadoMateria = entity.estadoMateria,
                    observaciones = entity.observaciones,
                    semipresencial = entity.semipresencial,
                    lunes = entity.lunes,
                    martes = entity.martes,
                    miercoles = entity.miercoles,
                    jueves = entity.jueves,
                    viernes = entity.viernes,
                    sabado = entity.sabado
                )
            }
        }

    override suspend fun insertCalificaciones(matricula: String, items: List<CalificacionUnidadItem>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val entities = items.map { item ->
                    CalificacionUnidadEntity(
                        matricula = matricula,
                        materia = item.materia,
                        grupo = item.grupo,
                        observaciones = item.observaciones,
                        unidadesActivas = item.unidadesActivas,
                        c1 = item.c1, c2 = item.c2, c3 = item.c3, c4 = item.c4,
                        c5 = item.c5, c6 = item.c6, c7 = item.c7, c8 = item.c8,
                        c9 = item.c9, c10 = item.c10, c11 = item.c11, c12 = item.c12,
                        c13 = item.c13
                    )
                }
                calificacionUnidadDao.insertCalificaciones(entities)
                Result.success(Unit)
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }
    }

    override fun getCalificaciones(matricula: String): Flow<List<CalificacionUnidadItem>> =
        calificacionUnidadDao.getCalificacionesByMatricula(matricula).map { list ->
            list.map { entity ->
                CalificacionUnidadItem(
                    materia = entity.materia,
                    grupo = entity.grupo,
                    observaciones = entity.observaciones,
                    unidadesActivas = entity.unidadesActivas,
                    c1 = entity.c1, c2 = entity.c2, c3 = entity.c3, c4 = entity.c4,
                    c5 = entity.c5, c6 = entity.c6, c7 = entity.c7, c8 = entity.c8,
                    c9 = entity.c9, c10 = entity.c10, c11 = entity.c11, c12 = entity.c12,
                    c13 = entity.c13
                )
            }
        }

    override suspend fun insertCalificacionFinal(matricula: String, items: List<CalificacionFinalItem>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val entities = items.map { item ->
                    CalificacionFinalEntity(
                        matricula = matricula,
                        materia = item.materia,
                        grupo = item.grupo,
                        calif = item.calif,
                        acreditacion = item.acreditacion,
                        observaciones = item.observaciones
                    )
                }
                calificacionFinalDao.insertCalificacionesFinales(entities)
                Result.success(Unit)
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }
    }

    override fun getCalificacionFinal(matricula: String): Flow<List<CalificacionFinalItem>> =
        calificacionFinalDao.getCalificacionesFinalesByMatricula(matricula).map { list ->
            list.map { entity ->
                CalificacionFinalItem(
                    materia = entity.materia,
                    grupo = entity.grupo,
                    calif = entity.calif,
                    acreditacion = entity.acreditacion,
                    observaciones = entity.observaciones
                )
            }
        }

    override fun getProfile(matricula: String): Flow<ProfileStudent?> =
        profileDao.getProfile(matricula).map { entity ->
            entity?.let {
                ProfileStudent(
                    matricula = it.matricula,
                    nombre = it.nombre,
                    carrera = it.carrera,
                    semActual = it.semActual,
                    cdtosAcumulados = it.cdtosAcumulados
                )
            }
        }

    override suspend fun insertProfile(profile: ProfileStudent): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val entity = ProfileEntity(
                    matricula = profile.matricula,
                    nombre = profile.nombre,
                    carrera = profile.carrera,
                    semActual = profile.semActual,
                    cdtosAcumulados = profile.cdtosAcumulados
                )
                profileDao.insertProfile(entity)
                Result.success(Unit)
            } catch (t: Throwable) {
                Result.failure(t)
            }
        }
    }
}



