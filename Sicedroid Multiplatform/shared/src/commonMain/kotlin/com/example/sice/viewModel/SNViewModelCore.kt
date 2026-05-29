package com.example.sice.viewModel

import com.example.sice.data.MainRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.sice.models.ProfileStudent
import com.example.sice.models.CardexItem
import com.example.sice.models.CalificacionUnidadItem
import com.example.sice.models.CargaItem
import com.example.sice.models.CalificacionFinalItem
import com.example.sice.platform.Platform
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.asStateFlow
import kotlin.coroutines.CoroutineContext

sealed interface SNUiState {
    data class Success(val accesoLogin: String) : SNUiState
    data class Error(val message: String) : SNUiState
    object Loading : SNUiState
}

open class SNViewModelCore(
    val repository: MainRepository,
    coroutineContext: CoroutineContext = Dispatchers.Main
) {

    /** Inicialización pública que plataformas pueden sobreescribir */
    open fun start(matricula: String? = null) {
        loadProfile(matricula)
    }

    /** Cierre público que plataformas pueden sobreescribir */
    open fun shutdown() {
        clear()
    }

    // Exception handler para capturar errores no manejados en el scope
    private val handler = CoroutineExceptionHandler { _, throwable ->
        _snUiState.value = SNUiState.Error(throwable.message ?: "Unexpected error")
    }

    // Scope que puede ser cancelado por la plataforma
    protected val scope = CoroutineScope(coroutineContext + SupervisorJob() + handler)

    // Estados y flows
    protected val _snUiState = MutableStateFlow<SNUiState>(SNUiState.Loading)
    val snUiState: StateFlow<SNUiState> = _snUiState.asStateFlow()

    // isLoading observable y atómico
    private val _isLoading = MutableStateFlow(false)
    val isLoadingFlow: StateFlow<Boolean> = _isLoading.asStateFlow()
    var isLoading: Boolean
        get() = _isLoading.value
        protected set(value) {
            _isLoading.value = value
        }

    protected val _profileState = MutableStateFlow<ProfileStudent?>(null)
    val profileState: StateFlow<ProfileStudent?> = _profileState.asStateFlow()

    private val _cardexState = MutableStateFlow<List<CardexItem>>(emptyList())
    val cardexState: StateFlow<List<CardexItem>> = _cardexState.asStateFlow()

    private val _calificacionesState = MutableStateFlow<List<CalificacionUnidadItem>>(emptyList())
    val calificacionesState: StateFlow<List<CalificacionUnidadItem>> =
        _calificacionesState.asStateFlow()

    private val _cargaState = MutableStateFlow<List<CargaItem>>(emptyList())
    val cargaState: StateFlow<List<CargaItem>> = _cargaState.asStateFlow()

    private val _califFinalState = MutableStateFlow<List<CalificacionFinalItem>>(emptyList())
    val califFinalState: StateFlow<List<CalificacionFinalItem>> = _califFinalState.asStateFlow()

    // onSyncRequested es hook que plataformas pueden sobreescribir
    protected open fun onSyncRequested(tipo: String, matricula: String) { /* no-op */
    }

    init {
        // Si hay matrícula actual, coleccionamos el Flow local de perfil como fuente única de verdad
        repository.getCurrentMatricula()?.let { mat ->
            scope.launch {
                repository.local.getProfile(mat).collect { profile ->
                    _profileState.value = profile
                }
            }
        }
    }

    // Generic loader para listas (cardex, carga, calificaciones, etc.)
    protected fun <T> loadDataGeneric(
        tipo: String,
        stateFlow: MutableStateFlow<List<T>>,
        callRepo: suspend (String, Boolean) -> Result<List<T>?>
    ) {
        val mat = repository.getCurrentMatricula() ?: run {
            _snUiState.value = SNUiState.Error("No matricula")
            return
        }

        scope.launch {
            _snUiState.value = SNUiState.Loading
            isLoading = true
            try {
                val online = Platform.isOnline()
                val result = withContext(Dispatchers.IO) {
                    if (online) {
                        val r = callRepo(mat, true)
                        if (r.isSuccess) r else callRepo(mat, false)
                    } else {
                        callRepo(mat, false)
                    }
                }

                result.fold(onSuccess = { data ->
                    if (!data.isNullOrEmpty()) {
                        stateFlow.value = data
                        if (online) onSyncRequested(tipo, mat)
                        _snUiState.value = SNUiState.Success("${tipo}Loaded")
                    } else {
                        stateFlow.value = emptyList()
                        _snUiState.value = SNUiState.Error("Empty")
                    }
                }, onFailure = { err ->
                    _snUiState.value = SNUiState.Error(err.message ?: "Error")
                })
            } finally {
                isLoading = false
            }
        }
    }

    // Carga de perfil: delega a MainRepository (que debe persistir local si viene de remoto)
    fun loadProfile(matriculaParam: String? = null) {
        val mat = matriculaParam ?: repository.getCurrentMatricula() ?: run {
            _snUiState.value = SNUiState.Error("No matricula")
            return
        }

        scope.launch {
            isLoading = true
            _snUiState.value = SNUiState.Loading
            try {
                val online = Platform.isOnline()
                val result = withContext(Dispatchers.IO) { repository.getProfile(mat, online) }
                result.fold(onSuccess = { perfil ->
                    if (perfil != null) {
                        // Si MainRepository persiste local al obtener remoto, el Flow local ya emitirá.
                        // Aun así, actualizamos el state para reflejar inmediatamente el resultado confirmado.
                        _profileState.value = perfil
                        _snUiState.value = SNUiState.Success(if (online) "Online" else "Offline")
                        if (online) onSyncRequested("perfil", mat)
                    } else {
                        _snUiState.value = SNUiState.Error("Error")
                    }
                }, onFailure = { err ->
                    _snUiState.value = SNUiState.Error(err.message ?: "Error")
                })
            } finally {
                isLoading = false
            }
        }
    }

    // Wrappers para los loadDataGeneric
    fun loadCardex(lineamiento: Int = 1) =
        loadDataGeneric("cardex", _cardexState) { m, online ->
            repository.getCardex(
                m,
                lineamiento,
                online
            )
        }

    fun loadCalificacionesPorUnidad() =
        loadDataGeneric(
            "califUnidades",
            _calificacionesState
        ) { m, online -> repository.getCalificaciones(m, online) }

    fun loadCargaAcademica() =
        loadDataGeneric("carga", _cargaState) { m, online -> repository.getCarga(m, online) }

    fun loadCalificacionFinal(modEducativo: Int = 1) =
        loadDataGeneric(
            "califFinal",
            _califFinalState
        ) { m, online -> repository.getCalificacionFinal(m, modEducativo, online) }

    // dentro de SNViewModelCore
    fun clear() {
        try {
            _profileState.value = null
        } catch (_: Throwable) { /* no-op */ }
    }

    // en SNViewModelCore o SNViewModelDesktop
    fun logout() {
        scope.launch {
            try {
                // repository.logout() puede ser suspend; si no lo es, envuélvelo en withContext(Dispatchers.IO)
                repository.logout()
            } catch (t: Throwable) {
                // loggear si hace falta
            } finally {
                // forzar que la UI muestre login
                _profileState.value = null
            }
        }
    }
}