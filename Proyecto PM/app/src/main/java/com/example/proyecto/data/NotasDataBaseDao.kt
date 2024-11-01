package com.example.proyecto.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.proyecto.Models.Nota
import kotlinx.coroutines.flow.Flow

@Dao
interface NotasDataBaseDao {
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

}