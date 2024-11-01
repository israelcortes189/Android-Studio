package com.example.proyecto.screens

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    init {
        //listaNotas.addAll(NotaDateSource().loadNotas())
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllNotas().distinctUntilChanged()
                .collect() { lista ->
                    if (lista.isNotEmpty()) {
                        Log.d("Lectura", "Lista vacia")
                    }
                    _listaNotas.value = lista
                }
        }

        // Observa las tareas desde el repositorio
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllTareas().distinctUntilChanged()
                .collect() { lista ->
                    if (lista.isNotEmpty()) {
                        Log.d("Lectura", "Lista vacia")
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

    // Funciones para gestionar tareas
    fun addTarea(tarea: Tarea) = viewModelScope.launch {
        repo.addTarea(tarea)
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

    fun getAllTotas() = viewModelScope.launch {
        repo.getAllTareas()
    }
}