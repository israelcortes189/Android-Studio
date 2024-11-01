package com.example.proyecto.Repository

import com.example.proyecto.Models.Nota
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
}