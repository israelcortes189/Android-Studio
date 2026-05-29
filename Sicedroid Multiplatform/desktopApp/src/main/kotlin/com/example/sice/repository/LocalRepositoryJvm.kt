package com.example.sice.repository
import com.example.sice.data.LocalRepository
import com.example.sice.model.*
import com.example.sice.models.CalificacionFinalItem
import com.example.sice.models.CalificacionUnidadItem
import com.example.sice.models.CardexItem
import com.example.sice.models.CargaItem
import com.example.sice.models.ProfileStudent
import io.ktor.http.ContentType.Application.Json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import com.example.sice.models.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

@Serializable
data class CardexDto(val items: List<CardexItem> = emptyList(), val promedio: PromedioInfo? = null)

@Serializable
data class CargaDto(val items: List<CargaItem> = emptyList())

@Serializable
data class CalifUnidadDto(val items: List<CalificacionUnidadItem> = emptyList())

@Serializable
data class CalifFinalDto(val items: List<CalificacionFinalItem> = emptyList())


class LocalRepositoryJvm(
    private val storageDir: File = File(System.getProperty("user.home"), ".sice_desktop"),
    private val json: Json = Json { encodeDefaults = true; ignoreUnknownKeys = true }
) : LocalRepository {

    private val mutex = Mutex()
    private val profileFlows = mutableMapOf<String, MutableStateFlow<ProfileStudent?>>()
    private val cardexFlows = mutableMapOf<String, MutableStateFlow<List<CardexItem>>>()
    private val cargaFlows = mutableMapOf<String, MutableStateFlow<List<CargaItem>>>()
    private val califUnidadFlows = mutableMapOf<String, MutableStateFlow<List<CalificacionUnidadItem>>>()
    private val califFinalFlows = mutableMapOf<String, MutableStateFlow<List<CalificacionFinalItem>>>()

    init {
        if (!storageDir.exists()) storageDir.mkdirs()
    }

    private fun fileForProfile(matricula: String) = File(storageDir, "profile_$matricula.json")
    private fun fileForCardex(matricula: String) = File(storageDir, "cardex_$matricula.json")
    private fun fileForCarga(matricula: String) = File(storageDir, "carga_$matricula.json")
    private fun fileForCalifUnidad(matricula: String) = File(storageDir, "calif_unidad_$matricula.json")
    private fun fileForCalifFinal(matricula: String) = File(storageDir, "calif_final_$matricula.json")

    // ---------- Profile ----------
    override fun getProfile(matricula: String): Flow<ProfileStudent?> {
        val flow = profileFlows.getOrPut(matricula) {
            MutableStateFlow(readProfileFromDisk(matricula))
        }
        return flow.asStateFlow()
    }

    override suspend fun insertProfile(profile: ProfileStudent): Result<Unit> {
        return withContext(Dispatchers.IO) {
            mutex.withLock {
                val matricula = profile.matricula
                val dto = ProfileStudent(
                    matricula = matricula,
                    nombre = profile.nombre,
                    carrera = profile.carrera,
                    semActual = profile.semActual,
                    cdtosAcumulados = profile.cdtosAcumulados
                )
                val target = fileForProfile(matricula)
                val tmp = File(target.parentFile, "${target.name}.tmp")
                try {
                    val text = json.encodeToString(dto)
                    tmp.writeText(text)
                    try {
                        Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
                    } catch (e: Exception) {
                        Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
                    }
                    // actualizar StateFlow solo después de escritura exitosa
                    profileFlows.getOrPut(matricula) { MutableStateFlow(null) }.value = profile
                    Result.success(Unit)
                } catch (t: Throwable) {
                    try { if (tmp.exists()) tmp.delete() } catch (_: Throwable) {}
                    Result.failure(t)
                }
            }
        }
    }

    private fun readProfileFromDisk(matricula: String): ProfileStudent? {
        val file = fileForProfile(matricula)
        return if (file.exists()) {
            try {
                val dto = json.decodeFromString<ProfileStudent>(file.readText())
                ProfileStudent(
                    matricula = dto.matricula,
                    nombre = dto.nombre ?: "",
                    carrera = dto.carrera ?: "",
                    semActual = dto.semActual ?: 0,
                    cdtosAcumulados = dto.cdtosAcumulados ?: 0
                )
            } catch (t: Throwable) {
                null
            }
        } else null
    }


    override suspend fun insertCardex(matricula: String, items: List<CardexItem>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            mutex.withLock {
                val target = fileForCardex(matricula)
                val tmp = File(target.parentFile, "${target.name}.tmp")
                try {
                    // Serializar DTO y escribir archivo temporal
                    val dto = CardexDto(items)
                    val text = json.encodeToString(dto)
                    tmp.writeText(text)

                    // Mover atómicamente al archivo destino
                    try {
                        Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
                    } catch (e: Exception) {
                        Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
                    }

                    // Leer desde disco (fuente de verdad) y sincronizar flow
                    val diskItems = try {
                        val diskDto = json.decodeFromString<CardexDto>(target.readText())
                        diskDto.items
                    } catch (t: Throwable) {
                        // fallback: usar lo que acabamos de escribir
                        items
                    }

                    val flow = cardexFlows.getOrPut(matricula) { MutableStateFlow(emptyList()) }
                    flow.value = diskItems

                    // Log de verificación
                    println("DEBUG LocalRepositoryJvm: wrote file, diskSize=${diskItems.size}, flowSize=${flow.value.size} for matricula=$matricula")

                    Result.success(Unit)
                } catch (t: Throwable) {
                    try { if (tmp.exists()) tmp.delete() } catch (_: Throwable) {}
                    println("DEBUG LocalRepositoryJvm insertCardex failed: ${t.message}")
                    Result.failure(t)
                }
            }
        }
    }


    override fun getCardex(matricula: String): Flow<List<CardexItem>> {
        val flow = cardexFlows.getOrPut(matricula) { MutableStateFlow(readCardexFromDisk(matricula)) }
        return flow.asStateFlow()
    }

    private fun readCardexFromDisk(matricula: String): List<CardexItem> {
        val file = fileForCardex(matricula)
        return if (file.exists()) {
            try {
                val dto = json.decodeFromString<CardexDto>(file.readText())
                dto.items
            } catch (t: Throwable) {
                emptyList()
            }
        } else emptyList()
    }

    // ---------- Carga ----------
    override suspend fun insertCarga(matricula: String, items: List<CargaItem>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            mutex.withLock {
                val target = fileForCarga(matricula)
                val tmp = File(target.parentFile, "${target.name}.tmp")
                try {
                    val dto = CargaDto(items)
                    tmp.writeText(json.encodeToString(dto))
                    try {
                        Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
                    } catch (e: Exception) {
                        Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
                    }
                    cargaFlows.getOrPut(matricula) { MutableStateFlow(emptyList()) }.value = items
                    Result.success(Unit)
                } catch (t: Throwable) {
                    try { if (tmp.exists()) tmp.delete() } catch (_: Throwable) {}
                    Result.failure(t)
                }
            }
        }
    }

    override fun getCarga(matricula: String): Flow<List<CargaItem>> {
        val flow = cargaFlows.getOrPut(matricula) { MutableStateFlow(readCargaFromDisk(matricula)) }
        return flow.asStateFlow()
    }

    private fun readCargaFromDisk(matricula: String): List<CargaItem> {
        val file = fileForCarga(matricula)
        return if (file.exists()) {
            try {
                val dto = json.decodeFromString<CargaDto>(file.readText())
                dto.items
            } catch (t: Throwable) {
                emptyList()
            }
        } else emptyList()
    }

    // ---------- Calificaciones por unidad ----------
    override suspend fun insertCalificaciones(matricula: String, items: List<CalificacionUnidadItem>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            mutex.withLock {
                val target = fileForCalifUnidad(matricula)
                val tmp = File(target.parentFile, "${target.name}.tmp")
                try {
                    val dto = CalifUnidadDto(items)
                    tmp.writeText(json.encodeToString(dto))
                    try {
                        Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
                    } catch (e: Exception) {
                        Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
                    }
                    califUnidadFlows.getOrPut(matricula) { MutableStateFlow(emptyList()) }.value = items
                    Result.success(Unit)
                } catch (t: Throwable) {
                    try { if (tmp.exists()) tmp.delete() } catch (_: Throwable) {}
                    Result.failure(t)
                }
            }
        }
    }

    override fun getCalificaciones(matricula: String): Flow<List<CalificacionUnidadItem>> {
        val flow = califUnidadFlows.getOrPut(matricula) { MutableStateFlow(readCalifUnidadFromDisk(matricula)) }
        return flow.asStateFlow()
    }

    private fun readCalifUnidadFromDisk(matricula: String): List<CalificacionUnidadItem> {
        val file = fileForCalifUnidad(matricula)
        return if (file.exists()) {
            try {
                val dto = json.decodeFromString<CalifUnidadDto>(file.readText())
                dto.items
            } catch (t: Throwable) {
                emptyList()
            }
        } else emptyList()
    }

    // ---------- Calificaciones finales ----------
    override suspend fun insertCalificacionFinal(matricula: String, items: List<CalificacionFinalItem>): Result<Unit> {
        return withContext(Dispatchers.IO) {
            mutex.withLock {
                val target = fileForCalifFinal(matricula)
                val tmp = File(target.parentFile, "${target.name}.tmp")
                try {
                    val dto = CalifFinalDto(items)
                    tmp.writeText(json.encodeToString(dto))
                    try {
                        Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
                    } catch (e: Exception) {
                        Files.move(tmp.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING)
                    }
                    califFinalFlows.getOrPut(matricula) { MutableStateFlow(emptyList()) }.value = items
                    Result.success(Unit)
                } catch (t: Throwable) {
                    try { if (tmp.exists()) tmp.delete() } catch (_: Throwable) {}
                    Result.failure(t)
                }
            }
        }
    }

    override fun getCalificacionFinal(matricula: String): Flow<List<CalificacionFinalItem>> {
        val flow = califFinalFlows.getOrPut(matricula) { MutableStateFlow(readCalifFinalFromDisk(matricula)) }
        return flow.asStateFlow()
    }

    private fun readCalifFinalFromDisk(matricula: String): List<CalificacionFinalItem> {
        val file = fileForCalifFinal(matricula)
        return if (file.exists()) {
            try {
                val dto = json.decodeFromString<CalifFinalDto>(file.readText())
                dto.items
            } catch (t: Throwable) {
                emptyList()
            }
        } else emptyList()
    }
}