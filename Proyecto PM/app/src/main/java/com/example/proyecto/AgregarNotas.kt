package com.example.proyecto

import android.Manifest
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
import androidx.annotation.RequiresApi
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
import com.google.android.material.datepicker.MaterialDatePicker
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AgregarNotas(navController: NavHostController, notaViewModel: NotasViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    var estadoDeTexto by rememberSaveable { mutableStateOf("") }
    var estadoDeTexto2 by rememberSaveable { mutableStateOf("") }
    var fechaSeleccionada by rememberSaveable { mutableStateOf<LocalDateTime?>(null) }
    var mostrarDatePicker by remember { mutableStateOf(false) }
    var mostrarTimePicker by remember { mutableStateOf(false) }

    // Launcher para solicitar permiso de notificaciones
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

    // Verificación de permiso de exactas alarmas
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

    MenuLateral(navController = navController, drawerState = drawerState) {
        Scaffold(
            topBar = { TopBar(drawerState) },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                item {
                    // Título con el ícono de retorno
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
                            name = stringResource(R.string.agregar_notas),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f) // Para centrar el texto en el Row
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
                        value = estadoDeTexto,
                        onValueChange = { estadoDeTexto = it },
                        placeholder = { Text(stringResource(R.string.escribe_el_t_tulo_de_la_tarea)) },
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
                        value = estadoDeTexto2,
                        onValueChange = { estadoDeTexto2 = it },
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
                        Text(
                            text = fechaSeleccionada?.toString() ?: "Seleccionar Fecha y Hora"
                        )
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
                            if (estadoDeTexto.isNotBlank() && estadoDeTexto2.isNotBlank()) {
                                notaViewModel.addNota(
                                    Nota(
                                        titulo = estadoDeTexto,
                                        descripcion = estadoDeTexto2,
                                        fechaRecordatorio = fechaSeleccionada
                                    )
                                )
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
                        Text(text = "Agregar Nota")
                    }
                }
            }
        }
    }
}

@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (year: Int, month: Int, day: Int) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = android.app.DatePickerDialog(
        context,
        { _, selectedYear, selectedMonth, selectedDay ->
            onDateSelected(selectedYear, selectedMonth, selectedDay)
        }, year, month, day
    )

    datePickerDialog.setOnDismissListener {
        onDismissRequest()
    }

    datePickerDialog.show()
}

@Composable
fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onTimeSelected: (hour: Int, minute: Int) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = android.app.TimePickerDialog(
        context,
        { _, selectedHour, selectedMinute ->
            onTimeSelected(selectedHour, selectedMinute)
        }, hour, minute, true
    )

    timePickerDialog.setOnDismissListener {
        onDismissRequest()
    }
    timePickerDialog.show()
}







