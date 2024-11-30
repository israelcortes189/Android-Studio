package com.example.proyecto.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyecto.Models.Nota
import com.example.proyecto.Models.Tarea
import kotlinx.coroutines.flow.Flow

@Dao
interface NotasDataBaseDao {
    //Funciones Notas
    @Query("SELECT * FROM notas")
    fun getNotas() : Flow<List<Nota>>

    @Query("SELECT * FROM notas where id= :id")
    suspend fun getNotaById(id: Int): Nota

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNota(nota:Nota)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNota(nota:Nota)

    @Delete
    suspend fun deleteNota(nota:Nota)

    @Query("DELETE FROM notas")
    suspend fun deleteAllNotas()

    //Funciones para Tareas
    @Query("SELECT * FROM tareas")
    fun getTareas(): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE id = :id")
    suspend fun getTareaById(id: Int): Tarea

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarea(tarea: Tarea)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTarea(tarea: Tarea)

    @Delete
    suspend fun deleteTarea(tarea: Tarea)

    @Query("DELETE FROM tareas")
    suspend fun deleteAllTareas()
}