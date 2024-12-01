package com.example.proyecto.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyecto.Models.Media
import com.example.proyecto.Models.Nota
import com.example.proyecto.Models.Tarea
import kotlinx.coroutines.flow.Flow

@Dao
interface NotasDataBaseDao {
    // Funciones para Notas
    @Query("SELECT * FROM notas")
    fun getNotas(): Flow<List<Nota>>

    @Query("SELECT * FROM notas where id= :id")
    suspend fun getNotaById(id: Int): Nota

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNota(nota: Nota)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNota(nota: Nota)

    @Delete
    suspend fun deleteNota(nota: Nota)

    @Query("DELETE FROM notas")
    suspend fun deleteAllNotas()

    // Funciones para Tareas
    @Query("SELECT * FROM tareas")
    fun getTareas(): Flow<List<Tarea>>

    @Query("SELECT * FROM tareas WHERE id = :id")
    suspend fun getTareaById(id: Int): Tarea

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTarea(tarea: Tarea): Long  // Devuelve el ID de la tarea insertada

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateTarea(tarea: Tarea)

    @Delete
    suspend fun deleteTarea(tarea: Tarea)

    @Query("DELETE FROM tareas")
    suspend fun deleteAllTareas()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedia(media: Media)

    @Query("SELECT * FROM media WHERE tareaId = :tareaId AND tipo = 'imagen'")
    fun getImagenes(tareaId: Int): List<Media>

    @Query("SELECT * FROM media WHERE tareaId = :tareaId AND tipo = 'video'")
    fun getVideos(tareaId: Int): List<Media>

    @Query("SELECT * FROM media WHERE tareaId = :tareaId")
    fun getMediaByTareaId(tareaId: Int): List<Media>

    // Nueva función para obtener el último ID de tarea insertado
    @Query("SELECT id FROM tareas ORDER BY id DESC LIMIT 1")
    suspend fun getLastInsertedTareaId(): Int

    @Delete
    suspend fun deleteMedia(media: Media)
}
