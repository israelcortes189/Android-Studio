package com.example.sice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.sice.ui.CalificacionFinalShared
import com.example.sice.ui.CalificacionesUnidadShared
import com.example.sice.ui.CargaShared
import com.example.sice.ui.KardexShared
import com.example.sice.ui.LoginScreenShared
import com.example.sice.ui.MenuLateralShared
import com.example.sice.ui.PerfilScreenShared
import com.example.sice.ui.TopBarShared
import com.example.sice.viewModel.SNViewModelCore
import kotlinx.coroutines.launch

// Simple enum para navegación
private enum class Screen { HOME, KARDEX, CARGA, CALIFS_UNIDAD, CALIF_FINAL }

@Composable
fun App(viewModel: SNViewModelCore) {
    val scope = rememberCoroutineScope()

    val snUiState by viewModel.snUiState.collectAsState()
    val profile by viewModel.profileState.collectAsState()
    val isLoading by viewModel.isLoadingFlow.collectAsState()

    var screen by remember { mutableStateOf(Screen.HOME) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snUiState) {
        when (snUiState) {
            is com.example.sice.viewModel.SNUiState.Error -> {
                val msg = (snUiState as com.example.sice.viewModel.SNUiState.Error).message
                snackbarHostState.showSnackbar(msg)
            }

            is com.example.sice.viewModel.SNUiState.Success -> {
                val msg = (snUiState as com.example.sice.viewModel.SNUiState.Success).accesoLogin
                snackbarHostState.showSnackbar(msg)
            }

            else -> {}
        }
    }

    // Si el profile cambia a no-null, aseguramos que la UI muestre HOME
    LaunchedEffect(profile) {
        if (profile != null) {
            screen = Screen.HOME
        }
    }

    LaunchedEffect(Unit) {
        try {
            viewModel.start()
        } catch (t: Throwable) {
            snackbarHostState.showSnackbar("Error al iniciar: ${t.message ?: "desconocido"}")
        }
    }

    MaterialTheme {
        Scaffold(
            topBar = {
                TopBarShared(
                    title = when (screen) {
                        Screen.HOME -> "Inicio"
                        Screen.KARDEX -> "Kárdex"
                        Screen.CARGA -> "Carga Académica"
                        Screen.CALIFS_UNIDAD -> "Calificaciones por Unidad"
                        Screen.CALIF_FINAL -> "Calificaciones Finales"
                    },
                    onMenuClick = null
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
        ) { innerPadding ->
            // Si no hay perfil, mostrar login
            if (profile == null) {
                Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                    LoginScreenShared(viewModel = viewModel, onLoginSuccess = {
                        // No dependemos de este callback para navegar; App observa profile.
                        // Pero podemos pedir explícitamente la carga del perfil.
                        scope.launch { viewModel.loadProfile() }
                        // screen = Screen.HOME // opcional, App observará profile y lo pondrá en HOME
                    })
                }
                return@Scaffold
            }

            // Si hay perfil, mostrar UI principal (Home contiene Perfil)
            Row(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
                MenuLateralShared(
                    items = listOf(
                        "Inicio" to { screen = Screen.HOME },
                        "Kárdex" to {
                            screen = Screen.KARDEX
                            scope.launch { viewModel.loadCardex() }
                        },
                        "Carga" to {
                            screen = Screen.CARGA
                            scope.launch { viewModel.loadCargaAcademica() }
                        },
                        "Calificaciones Unidad" to {
                            screen = Screen.CALIFS_UNIDAD
                            scope.launch { viewModel.loadCalificacionesPorUnidad() }
                        },
                        "Calificación Final" to {
                            screen = Screen.CALIF_FINAL
                            scope.launch { viewModel.loadCalificacionFinal() }
                        }
                    ),
                    onLogout = {
                        scope.launch {
                            try {
                                viewModel.logout()
                                screen = Screen.HOME
                            } catch (t: Throwable) { /* manejar error */ }
                        }
                    }
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        when (screen) {
                            Screen.HOME -> {
                                Column(modifier = Modifier.fillMaxSize()) {
                                    // Aquí se muestra el perfil dentro del Home
                                    PerfilScreenShared(viewModel)
                                }
                            }

                            Screen.KARDEX -> KardexShared(viewModel)
                            Screen.CARGA -> CargaShared(viewModel)
                            Screen.CALIFS_UNIDAD -> CalificacionesUnidadShared(viewModel)
                            Screen.CALIF_FINAL -> CalificacionFinalShared(viewModel)
                        }

                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                        }
                    }
                }
            }
        }
    }
}


