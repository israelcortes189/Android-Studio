package com.example.proyecto.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto.Models.Media
import com.example.proyecto.Models.Nota
import com.example.proyecto.Models.Tarea
import com.example.proyecto.Repository.NotasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotasViewModel @Inject constructor(
    private val repo: NotasRepository
) : ViewModel() {

    private val _listaNotas = MutableStateFlow<List<Nota>>(emptyList())
    val listaNotas = _listaNotas.asStateFlow()

    private val _listaTareas = MutableStateFlow<List<Tarea>>(emptyList())
    val listaTareas = _listaTareas.asStateFlow()

    // StateFlows para imágenes y videos
    private val _imagenes = MutableStateFlow<List<Media>>(emptyList())
    val imagenes = _imagenes.asStateFlow()

    private val _videos = MutableStateFlow<List<Media>>(emptyList())
    val videos = _videos.asStateFlow()

    init {
        // Observa las notas desde el repositorio
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllNotas().distinctUntilChanged().collect { lista ->
                if (lista.isNotEmpty()) {
                    Log.d("Lectura", "Lista vacía")
                }
                _listaNotas.value = lista
            }
        }

        // Observa las tareas desde el repositorio
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllTareas().distinctUntilChanged().collect { lista ->
                if (lista.isNotEmpty()) {
                    Log.d("Lectura", "Lista vacía")
                }
                _listaTareas.value = lista
            }
        }
    }

    fun addNota(nota: Nota) = viewModelScope.launch {
        repo.addNota(nota)
    }

    fun removeNota(nota: Nota) = viewModelScope.launch {
        repo.deleteNota(nota)
    }

    fun removeAllNotas() = viewModelScope.launch {
        repo.deleteAllNotas()
    }

    fun updateNota(nota: Nota) = viewModelScope.launch {
        repo.updateNota(nota)
    }

    fun getAllNotas() = viewModelScope.launch {
        repo.getAllNotas()
    }

    // Funciones para gestionar tareas y medios
    fun addTarea(tarea: Tarea, imagenUris: List<String>, videoUris: List<String>) = viewModelScope.launch {
        repo.addTarea(tarea, imagenUris, videoUris)
    }

    fun removeTarea(tarea: Tarea) = viewModelScope.launch {
        repo.deleteTarea(tarea)
    }

    fun removeAllTareas() = viewModelScope.launch {
        repo.deleteAllTareas()
    }

    fun updateTarea(tarea: Tarea) = viewModelScope.launch {
        repo.updateTarea(tarea)
    }

    fun getAllTareas() = viewModelScope.launch {
        repo.getAllTareas()
    }

    suspend fun getNotaById(id: Int): Nota? {
        return repo.getNotaById(id)
    }

    suspend fun getTareaById(id: Int): Tarea? {
        return repo.getTareaById(id)
    }

    // Funciones para gestionar Media
    fun getImagenes(tareaId: Int) = viewModelScope.launch {
        val imagenes = repo.getImagenes(tareaId)
        _imagenes.value = imagenes
    }

    fun getVideos(tareaId: Int) = viewModelScope.launch {
        val videos = repo.getVideos(tareaId)
        _videos.value = videos
    }

    suspend fun getMediaByTareaId(tareaId: Int): List<Media> {
        return repo.getMediaByTareaId(tareaId)
    }

    fun removeMedia(media: Media) = viewModelScope.launch(Dispatchers.IO) {
        repo.deleteMedia(media)
    }

    // Función para insertar un medio en la base de datos
    fun insertMedia(media: Media) = viewModelScope.launch(Dispatchers.IO) {
        repo.insertMedia(media) }
}
