package com.example.sice.data

import com.example.sice.models.CalificacionFinalItem
import com.example.sice.models.CalificacionUnidadItem
import com.example.sice.models.CardexItem
import com.example.sice.models.CargaItem
import com.example.sice.models.ProfileStudent
import kotlinx.coroutines.flow.firstOrNull

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepository(
    val local: LocalRepository,
    val remote: SNRepository
) {

    fun getCurrentMatricula(): String? = remote.getCurrentMatricula()
    fun setCurrentMatricula(matricula: String?) = remote.setCurrentMatricula(matricula)

    suspend fun acceso(m: String, p: String): Result<String> = try {
        Result.success(remote.acceso(m, p))
    } catch (t: Throwable) {
        Result.failure(t)
    }

    fun hasSession(): Boolean = remote.hasSession()
    fun logout() = remote.logout()

    // -------------------------
    // Profile
    // -------------------------
    suspend fun getProfile(matricula: String, online: Boolean): Result<ProfileStudent?> = try {
        if (online) {
            val remoteProfile = try { remote.profile() } catch (t: Throwable) {
                println("DEBUG remote.profile exception: ${t.message}")
                null
            }
            println("DEBUG remote.profile() = $remoteProfile")
            remoteProfile?.let {
                // persistir en IO y comprobar resultado
                val res = withContext(Dispatchers.IO) { local.insertProfile(it) }
                println("DEBUG local.insertProfile result = ${res.isSuccess}")
            }
        }
        val profile = withContext(Dispatchers.IO) { local.getProfile(matricula).firstOrNull() }
        Result.success(profile)
    } catch (t: Throwable) {
        Result.failure(t)
    }

    // -------------------------
// Cardex (misma normalización y estilo que getCarga)
// -------------------------
    suspend fun getCardex(matricula: String, lineamiento: Int, online: Boolean): Result<List<CardexItem>?> = try {
        if (online) {
            val remoteList = try {
                // remote.cardex puede devolver Pair, List u otro; normalizamos a List<CardexItem>?
                val raw = remote.cardex(lineamiento)
                when (raw) {
                    null -> null
                    is Pair<*, *> -> (raw.first as? List<*>)?.filterIsInstance<CardexItem>()
                    is List<*> -> raw.filterIsInstance<CardexItem>()
                    else -> null
                }
            } catch (t: Throwable) {
                println("DEBUG remote.cardex exception: ${t.message}")
                null
            }

            println("DEBUG remote.cardex size = ${remoteList?.size ?: "null"} for matricula=$matricula")

            remoteList?.let {
                val insertRes = withContext(Dispatchers.IO) { local.insertCardex(matricula, it) }
                println("DEBUG local.insertCardex result = ${insertRes.isSuccess}; persisted=${it.size}; matricula=$matricula")
            }
        }

        val list = withContext(Dispatchers.IO) { local.getCardex(matricula).firstOrNull() ?: emptyList() }
        println("DEBUG local cardex size = ${list.size} for matricula=$matricula")
        Result.success(list)
    } catch (t: Throwable) {
        Result.failure(t)
    }


    // -------------------------
    // Carga Academica
    // -------------------------
    suspend fun getCarga(matricula: String, online: Boolean): Result<List<CargaItem>?> = try {
        if (online) {
            val remoteList = try { remote.cargaAcademica() } catch (t: Throwable) {
                println("DEBUG remote.cargaAcademica exception: ${t.message}")
                null
            }
            println("DEBUG remote.cargaAcademica size = ${remoteList?.size ?: "null"} for matricula=$matricula")
            remoteList?.let {
                val insertRes = withContext(Dispatchers.IO) { local.insertCarga(matricula, it) }
                println("DEBUG local.insertCarga result = ${insertRes.isSuccess}; persisted=${it.size}; matricula=$matricula")
            }
        }

        val list = withContext(Dispatchers.IO) { local.getCarga(matricula).firstOrNull() ?: emptyList() }
        println("DEBUG local carga size = ${list.size} for matricula=$matricula")
        Result.success(list)
    } catch (t: Throwable) {
        Result.failure(t)
    }

    // -------------------------
    // Calificaciones por unidad
    // -------------------------
    suspend fun getCalificaciones(matricula: String, online: Boolean): Result<List<CalificacionUnidadItem>?> = try {
        if (online) {
            val remoteList = try { remote.calificacionesPorUnidad() } catch (t: Throwable) {
                println("DEBUG remote.calificacionesPorUnidad exception: ${t.message}")
                null
            }
            println("DEBUG remote.calificacionesPorUnidad size = ${remoteList?.size ?: "null"} for matricula=$matricula")
            remoteList?.let {
                val insertRes = withContext(Dispatchers.IO) { local.insertCalificaciones(matricula, it) }
                println("DEBUG local.insertCalificaciones result = ${insertRes.isSuccess}; persisted=${it.size}; matricula=$matricula")
            }
        }

        val list = withContext(Dispatchers.IO) { local.getCalificaciones(matricula).firstOrNull() ?: emptyList() }
        println("DEBUG local calificaciones size = ${list.size} for matricula=$matricula")
        Result.success(list)
    } catch (t: Throwable) {
        Result.failure(t)
    }

    // -------------------------
    // Calificacion final
    // -------------------------
    suspend fun getCalificacionFinal(matricula: String, modEducativo: Int, online: Boolean): Result<List<CalificacionFinalItem>?> = try {
        if (online) {
            val remoteList = try { remote.calificacionFinal(modEducativo) } catch (t: Throwable) {
                println("DEBUG remote.calificacionFinal exception: ${t.message}")
                null
            }
            println("DEBUG remote.calificacionFinal size = ${remoteList?.size ?: "null"} for matricula=$matricula")
            remoteList?.let {
                val insertRes = withContext(Dispatchers.IO) { local.insertCalificacionFinal(matricula, it) }
                println("DEBUG local.insertCalificacionFinal result = ${insertRes.isSuccess}; persisted=${it.size}; matricula=$matricula")
            }
        }

        val list = withContext(Dispatchers.IO) { local.getCalificacionFinal(matricula).firstOrNull() ?: emptyList() }
        println("DEBUG local califFinal size = ${list.size} for matricula=$matricula")
        Result.success(list)
    } catch (t: Throwable) {
        Result.failure(t)
    }
}