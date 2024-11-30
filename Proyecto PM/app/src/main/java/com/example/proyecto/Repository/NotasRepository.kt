package com.example.proyecto.Repository

import com.example.proyecto.Models.Nota
import com.example.proyecto.Models.Tarea
import com.example.proyecto.data.NotasDataBaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NotasRepository @Inject constructor(private val notasDatabaseDao: NotasDataBaseDao){
     suspend fun addNota(nota: Nota) = notasDatabaseDao.insertNota(nota)
     suspend fun updateNota(nota: Nota) = notasDatabaseDao.updateNota(nota)
    suspend fun deleteNota(nota: Nota) = notasDatabaseDao.deleteNota(nota)
    suspend fun deleteAllNotas() = notasDatabaseDao.deleteAllNotas()

    fun getAllNotas(): Flow<List<Nota>>
    = notasDatabaseDao.getNotas().flowOn(Dispatchers.IO).conflate()

    // Funciones para gestionar tareas
    suspend fun addTarea(tarea: Tarea) = notasDatabaseDao.insertTarea(tarea) // Convierte Tarea a Nota
    suspend fun updateTarea(tarea: Tarea) = notasDatabaseDao.updateTarea(tarea)
    suspend fun deleteTarea(tarea: Tarea) = notasDatabaseDao.deleteTarea(tarea)
    suspend fun deleteAllTareas() = notasDatabaseDao.deleteAllTareas()

    fun getAllTareas(): Flow<List<Tarea>> =
         notasDatabaseDao.getTareas().flowOn(Dispatchers.IO).conflate()

    suspend fun getNotaById(id: Int): Nota? =
        notasDatabaseDao.getNotaById(id)

    suspend fun getTareaById(id: Int): Tarea? =
        notasDatabaseDao.getTareaById(id)
}