package com.example.proyecto

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.proyecto.Componentes.MenuLateral
import com.example.proyecto.Componentes.TopBar
import com.example.proyecto.Models.Nota
import com.example.proyecto.screens.NotasViewModel
import java.time.LocalDateTime

@SuppressLint("NewApi")
@Composable
fun EditarNota(navController: NavHostController, notaViewModel: NotasViewModel, id: Int) {
    var nota by remember { mutableStateOf<Nota?>(null) }
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    var fechaSeleccionada by rememberSaveable { mutableStateOf<LocalDateTime?>(null) }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mostrarTimePicker by remember { mutableStateOf(false) }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                mostrarDatePicker = true
            } else {
                Toast.makeText(
                    navController.context,
                    "Permiso de notificaciones denegado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    )

    val context = LocalContext.current
    val activity = context as? Activity
    val exactAlarmPermissionGranted = remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                alarmManager.canScheduleExactAlarms()
            } else {
                true
            }
        )
    }

    LaunchedEffect(id) {
        val fetchedNota = notaViewModel.getNotaById(id)
        nota = fetchedNota
        titulo = fetchedNota?.titulo ?: ""
        contenido = fetchedNota?.descripcion ?: ""
        fechaSeleccionada = fetchedNota?.fechaRecordatorio
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    MenuLateral(navController = navController, drawerState = drawerState) {
        Scaffold(
            topBar = {
                TopBar(drawerState)
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.regresar),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { navController.popBackStack() },
                            tint = Color.Gray
                        )
                        texto(
                            name = "Editar Nota",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                    texto(
                        name = stringResource(R.string.titulo),
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextField(
                        value = titulo,
                        onValueChange = { newTitle -> titulo = newTitle },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(50.dp))
                    texto(
                        name = stringResource(R.string.descripcion),
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextField(
                        value = contenido,
                        onValueChange = { newContent -> contenido = newContent },
                        placeholder = { Text(stringResource(R.string.descripcion_de_la_tarea)) },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    texto(
                        name = "Fecha y Hora de Recordatorio",
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = {
                            val notificationPermissionCheck = ContextCompat.checkSelfPermission(
                                navController.context, Manifest.permission.POST_NOTIFICATIONS
                            )
                            if (notificationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            } else if (exactAlarmPermissionGranted.value) {
                                mostrarDatePicker = true
                            } else {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                                    activity?.startActivity(intent)
                                } else {
                                    mostrarDatePicker = true
                                }
                            }
                        },
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        Text(text = fechaSeleccionada?.toString() ?: "Seleccionar Fecha y Hora")
                    }

                    if (mostrarDatePicker) {
                        DatePickerDialog(
                            onDismissRequest = { mostrarDatePicker = false },
                            onDateSelected = { year, month, day ->
                                mostrarTimePicker = true
                                fechaSeleccionada = LocalDateTime.of(year, month + 1, day, 0, 0)
                            }
                        )
                    }

                    if (mostrarTimePicker) {
                        TimePickerDialog(
                            onDismissRequest = { mostrarTimePicker = false },
                            onTimeSelected = { hour, minute ->
                                fechaSeleccionada = fechaSeleccionada?.withHour(hour)?.withMinute(minute)
                                mostrarTimePicker = false
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                    Button(
                        onClick = {
                            nota?.let {
                                val updatedNota = it.copy(
                                    titulo = titulo,
                                    descripcion = contenido,
                                    fechaRecordatorio = fechaSeleccionada
                                )
                                // Primero eliminar la nota existente para cancelar cualquier alarma
                                notaViewModel.removeRecordatorio(it)
                                // Luego actualizar la nota
                                notaViewModel.updateNota(updatedNota)
                            }
                            navController.navigate(route = Rutas.Notas.ruta)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1567A6),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Editar")
                    }
                }
            }
        }
    }
}
