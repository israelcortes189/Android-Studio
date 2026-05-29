package com.example.sice.ui.screens

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.sice.App
import com.example.sice.data.MainRepository
import com.example.sice.models.CalificacionFinalItem
import com.example.sice.models.CalificacionUnidadItem
import com.example.sice.models.CardexItem
import com.example.sice.models.CargaItem
import com.example.sice.models.ProfileStudent
import com.example.sice.platform.Platform
import com.example.sice.viewModel.SNUiState
import com.example.sice.viewModel.SNViewModelCore
import com.example.sice.workers.LocalWorker
import com.example.sice.workers.RemoteWorker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit


class SNViewModel(
    private val repository: MainRepository,
    private val appContext: Context
) : ViewModel() {

    // dentro de AndroidSNViewModel
    val sharedCore: SNViewModelCore
        get() = core

    // core se inicializa en init después de restaurar matrícula
    private lateinit var core: SNViewModelCore

    init {
        // 1) Restaurar matrícula en el repositorio si aún no está (hacerlo antes de crear core)
        val current = repository.getCurrentMatricula()
        if (current.isNullOrBlank()) {
            val saved = try {
                Platform.readSavedMatricula()
            } catch (_: Throwable) {
                null
            }
            if (!saved.isNullOrBlank()) {
                try {
                    repository.setCurrentMatricula(saved)
                } catch (_: Throwable) {
                }
            }
        }

        // 2) Crear core sabiendo que repository.getCurrentMatricula() puede devolver algo
        core = object :
            SNViewModelCore(repository, coroutineContext = viewModelScope.coroutineContext) {
            override fun onSyncRequested(tipo: String, matricula: String) {
                try {
                    Platform.scheduleSync(tipo, matricula)
                } catch (t: Throwable) {
                    Log.w("SNViewModelAndroid", "scheduleSync failed", t)
                }
            }
        }

        // 3) Ejecutar verificación de sesión y carga inicial
        checkSession()
    }

    private fun checkSession() {
        viewModelScope.launch {
            val hasSessionPref = try {
                Platform.readSavedMatricula() != null
            } catch (_: Throwable) {
                false
            }

            if (!core.repository.hasSession() && !hasSessionPref) {
                // No hay sesión en ninguna parte: limpiar estado y salir
                core.clear()
                return@launch
            }

            // Reconstruir matrícula en memoria si repository no la tiene
            val repoMat = core.repository.getCurrentMatricula()
            if (repoMat.isNullOrBlank()) {
                val saved = try {
                    Platform.readSavedMatricula()
                } catch (_: Throwable) {
                    null
                }
                if (!saved.isNullOrBlank()) {
                    try {
                        core.repository.setCurrentMatricula(saved)
                    } catch (_: Throwable) {
                    }
                }
            }
            // Dejar que el core haga la carga (intenta online si Platform.isOnline())
            core.loadProfile()
        }
    }
}
