package com.example.sice.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.sice.models.*
import com.example.sice.viewModel.SNUiState
import com.example.sice.viewModel.SNViewModelCore
import kotlinx.coroutines.launch

@Composable
fun TopBarShared(title: String, onMenuClick: (() -> Unit)? = null) {
    CenterAlignedTopAppBar(
        title = { Text(text = title, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        navigationIcon = {
            if (onMenuClick != null) {
                IconButton(onClick = onMenuClick) {
                    Text(text = "☰", style = MaterialTheme.typography.titleMedium)
                }
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}


/**
 * Simple lateral menu for Compose MPP.
 * - items: list of Pair<title, onClick>
 * - onLogout: logout action
 * - content: main content area
 */
@Composable
fun MenuLateralShared(
    items: List<Pair<String, () -> Unit>>,
    onLogout: () -> Unit,
    content: @Composable () -> Unit
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val maxW = maxWidth
        val isCompact = maxW < 600.dp

        if (isCompact) {
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            ModalNavigationDrawer(
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet {
                        DrawerContent(
                            items = items,
                            onLogout = {
                                // cerrar drawer y ejecutar logout
                                scope.launch { drawerState.close() }
                                onLogout()
                            },
                            closeDrawer = { scope.launch { drawerState.close() } }
                        )
                    }
                }
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    TopBarShared(
                        title = "", // el título lo pones desde Scaffold
                        onMenuClick = { scope.launch { drawerState.open() } }
                    )
                    Box(modifier = Modifier.fillMaxSize()) { content() }
                }
            }
        } else {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .width(160.dp)
                        .fillMaxHeight()
                        .padding(8.dp)
                ) {
                    DrawerContent(items = items, onLogout = onLogout, closeDrawer = null)
                }
                Divider(modifier = Modifier.width(1.dp).fillMaxHeight())
                Box(modifier = Modifier.weight(1f).padding(12.dp)) { content() }
            }
        }
    }
}

@Composable
private fun DrawerContent(
    items: List<Pair<String, () -> Unit>>,
    onLogout: () -> Unit,
    closeDrawer: (() -> Unit)? // null en panel permanente
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Menú",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
        )
        Divider()
        LazyColumn(modifier = Modifier.weight(1f)) {
            items(items) { pair ->
                val (title, action) = pair
                TextButton(
                    onClick = {
                        // ejecutar acción y cerrar drawer si aplica
                        action()
                        closeDrawer?.invoke()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = title,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Start,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        Divider()
        TextButton(
            onClick = {
                onLogout()
                closeDrawer?.invoke()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                "Cerrar sesión",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


/* ---------------------------
   Login
   --------------------------- */

@Composable
fun LoginScreenShared(viewModel: SNViewModelCore, onLoginSuccess: () -> Unit = {}) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val uiState by viewModel.snUiState.collectAsState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Iniciar sesión", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = usuario,
            onValueChange = { usuario = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )
        Spacer(Modifier.height(16.dp))
        Button(onClick = {
            scope.launch {
                val res = viewModel.repository.acceso(usuario, password)
                res.fold(onSuccess = { v ->
                    if (v == "OK") {
                        val mat = viewModel.repository.getCurrentMatricula()
                        if (!mat.isNullOrBlank()) {
                            viewModel.repository.setCurrentMatricula(mat)
                            viewModel.loadProfile(mat)
                        }
                        onLoginSuccess()
                    }
                }, onFailure = {
                    // opcional: emitir evento o actualizar snUiState desde repository/core
                })
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text("Entrar")
        }

        Spacer(Modifier.height(16.dp))

        when (uiState) {
            is com.example.sice.viewModel.SNUiState.Loading -> CircularProgressIndicator()
            is com.example.sice.viewModel.SNUiState.Error -> Text(
                (uiState as com.example.sice.viewModel.SNUiState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
            is com.example.sice.viewModel.SNUiState.Success -> { /* no-op */ }
        }
    }
}

/* ---------------------------
   Perfil
   --------------------------- */

@Composable
fun PerfilScreenShared(viewModel: SNViewModelCore) {
    val profile by viewModel.profileState.collectAsState()
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Perfil", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        if (profile == null) {
            Text("No hay perfil disponible", style = MaterialTheme.typography.bodyMedium)
        } else {
            Text("Matrícula: ${profile!!.matricula}", style = MaterialTheme.typography.bodyMedium)
            Text("Nombre: ${profile!!.nombre}", style = MaterialTheme.typography.bodyMedium)
            Text("Carrera: ${profile!!.carrera}", style = MaterialTheme.typography.bodyMedium)
            Text("Semestre actual: ${profile!!.semActual}", style = MaterialTheme.typography.bodyMedium)
            Text("Créditos acumulados: ${profile!!.cdtosAcumulados}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

/* ---------------------------
   Kardex
   --------------------------- */

@Composable
fun KardexShared(viewModel: SNViewModelCore) {
    val cardex by viewModel.cardexState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("Kárdex", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        if (cardex.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.TopCenter) {
                Text("No hay registros", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn {
                items(cardex) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = item.materia ?: "—", style = MaterialTheme.typography.bodyLarge)
                                Text(text = item.calificacion?.toString() ?: "—", style = MaterialTheme.typography.bodyMedium)
                            }
                            Spacer(Modifier.height(6.dp))
                            Text("Clave: ${item.claveMateria ?: "—"}  •  Oficial: ${item.claveOficial ?: "—"}", style = MaterialTheme.typography.bodySmall)
                            Text("Créditos: ${item.creditos ?: "—"}  •  Acreditación: ${item.acreditacion ?: "—"}", style = MaterialTheme.typography.bodySmall)
                            Text("Semestre: ${item.semestre ?: "—"}  •  ${item.periodo ?: ""} ${item.anio ?: ""}", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
        }
    }
}

/* ---------------------------
   Carga Académica
   --------------------------- */

@Composable
fun CargaShared(viewModel: SNViewModelCore) {
    val carga by viewModel.cargaState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("Carga Académica", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        if (carga.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.TopCenter) {
                Text("No hay registros de carga académica.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn {
                items(carga) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(item.materia ?: "—", style = MaterialTheme.typography.bodyLarge)
                                    Text("Grupo: ${item.grupo ?: "—"} • Clave: ${item.clvOficial ?: "—"}", style = MaterialTheme.typography.bodySmall)
                                }
                                Text("${item.creditos ?: "—"} cr", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Docente: ${item.docente ?: "—"}", style = MaterialTheme.typography.bodySmall)
                            if (!item.observaciones.isNullOrBlank()) {
                                Text("Observaciones: ${item.observaciones}", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(Modifier.height(8.dp))
                            Text("Horario", style = MaterialTheme.typography.bodySmall)
                            Spacer(Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                DayCellShared("Lun", item.lunes)
                                DayCellShared("Mar", item.martes)
                                DayCellShared("Mié", item.miercoles)
                                DayCellShared("Jue", item.jueves)
                                DayCellShared("Vie", item.viernes)
                                DayCellShared("Sáb", item.sabado)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DayCellShared(day: String, value: String?) {
    Column(modifier = Modifier.width(56.dp).padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(day, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Spacer(Modifier.height(4.dp))
        Text(value?.takeIf { it.isNotBlank() } ?: "—", style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
    }
}

/* ---------------------------
   Calificaciones por unidad
   --------------------------- */

@Composable
fun CalificacionesUnidadShared(viewModel: SNViewModelCore) {
    val calificaciones by viewModel.calificacionesState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("Calificaciones por Unidad", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        if (calificaciones.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.TopCenter) {
                Text("No hay calificaciones registradas.", style = MaterialTheme.typography.bodyMedium)
            }
        } else {
            LazyColumn {
                items(calificaciones) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(text = item.materia, style = MaterialTheme.typography.bodyLarge)
                                    Text(text = "Grupo: ${item.grupo} • Observaciones: ${item.observaciones ?: "—"}", style = MaterialTheme.typography.bodySmall)
                                }
                                Text(text = "Unidades: ${item.unidadesActivas}", style = MaterialTheme.typography.bodySmall)
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                UnitCellShared("C1", item.c1)
                                UnitCellShared("C2", item.c2)
                                UnitCellShared("C3", item.c3)
                                UnitCellShared("C4", item.c4)
                                UnitCellShared("C5", item.c5)
                                UnitCellShared("C6", item.c6)
                                UnitCellShared("C7", item.c7)
                                UnitCellShared("C8", item.c8)
                                UnitCellShared("C9", item.c9)
                                UnitCellShared("C10", item.c10)
                                UnitCellShared("C11", item.c11)
                                UnitCellShared("C12", item.c12)
                                UnitCellShared("C13", item.c13)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UnitCellShared(label: String, value: String?) {
    Column(modifier = Modifier.width(48.dp).padding(4.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        Spacer(Modifier.height(4.dp))
        Text(
            text = value?.takeIf { it.isNotBlank() } ?: "—",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/* ---------------------------
   Calificación final
   --------------------------- */

@Composable
fun CalificacionFinalShared(viewModel: SNViewModelCore) {
    val califFinal by viewModel.califFinalState.collectAsState()
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("Calificaciones Finales", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(8.dp))
        if (califFinal.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    "No hay calificaciones finales registradas.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn {
                items(califFinal) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.materia.ifBlank { "—" },
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "Grupo: ${item.grupo.ifBlank { "—" }}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = item.calif?.toString() ?: "—",
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                    Text(
                                        text = item.acreditacion.ifBlank { "—" },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }
                            Spacer(Modifier.height(8.dp))
                            if (!item.observaciones.isNullOrBlank()) {
                                Text(
                                    text = "Observaciones: ${item.observaciones}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Spacer(Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}