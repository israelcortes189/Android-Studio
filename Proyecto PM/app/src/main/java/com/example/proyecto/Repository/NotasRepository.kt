package com.example.proyecto.Repository

import com.example.proyecto.Models.Media
import com.example.proyecto.Models.Nota
import com.example.proyecto.Models.Tarea
import com.example.proyecto.data.NotasDataBaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class NotasRepository @Inject constructor(private val notasDatabaseDao: NotasDataBaseDao){
    suspend fun addNota(nota: Nota) = withContext(Dispatchers.IO) {
        notasDatabaseDao.insertNota(nota)
    }

    suspend fun updateNota(nota: Nota) = withContext(Dispatchers.IO) {
        notasDatabaseDao.updateNota(nota)
    }

    suspend fun deleteNota(nota: Nota) = withContext(Dispatchers.IO) {
        notasDatabaseDao.deleteNota(nota)
    }

    suspend fun deleteAllNotas() = withContext(Dispatchers.IO) {
        notasDatabaseDao.deleteAllNotas()
    }

    fun getAllNotas(): Flow<List<Nota>> =
        notasDatabaseDao.getNotas().flowOn(Dispatchers.IO).conflate()

    suspend fun getNotaById(id: Int): Nota? = withContext(Dispatchers.IO) {
        notasDatabaseDao.getNotaById(id)
    }

    suspend fun addTarea(tarea: Tarea, imagenUris: List<String>, videoUris: List<String>) = withContext(Dispatchers.IO) {
        val tareaId = notasDatabaseDao.insertTarea(tarea).toInt()
        imagenUris.forEach { uri ->
            notasDatabaseDao.insertMedia(Media(tareaId = tareaId, uri = uri, tipo = "imagen"))
        }
        videoUris.forEach { uri ->
            notasDatabaseDao.insertMedia(Media(tareaId = tareaId, uri = uri, tipo = "video"))
        }
    }

    suspend fun updateTarea(tarea: Tarea) = withContext(Dispatchers.IO) {
        notasDatabaseDao.updateTarea(tarea)
    }

    suspend fun deleteTarea(tarea: Tarea) = withContext(Dispatchers.IO) {
        notasDatabaseDao.deleteTarea(tarea)
    }

    suspend fun deleteAllTareas() = withContext(Dispatchers.IO) {
        notasDatabaseDao.deleteAllTareas()
    }

    fun getAllTareas(): Flow<List<Tarea>> = notasDatabaseDao.getTareas().flowOn(Dispatchers.IO).conflate()

    suspend fun getTareaById(id: Int): Tarea? = withContext(Dispatchers.IO) {
        notasDatabaseDao.getTareaById(id)
    }

    // Funciones para Media
    suspend fun insertMedia(media: Media) = withContext(Dispatchers.IO) {
        notasDatabaseDao.insertMedia(media)
    }

    fun getImagenes(tareaId: Int): List<Media> = notasDatabaseDao.getImagenes(tareaId)

    fun getVideos(tareaId: Int): List<Media> = notasDatabaseDao.getVideos(tareaId)

    suspend fun getMediaByTareaId(tareaId: Int): List<Media> = withContext(Dispatchers.IO) {
        notasDatabaseDao.getMediaByTareaId(tareaId)
    }

    suspend fun deleteMedia(media: Media) {
        notasDatabaseDao.deleteMedia(media) }
}


