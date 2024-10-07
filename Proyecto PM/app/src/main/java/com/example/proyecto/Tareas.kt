package com.example.proyecto

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController

val verdeClaro = Color(0xFFE0E99D)

@Composable
fun Tareas(navController: NavHostController) {
        Scaffold(
            modifier = Modifier.fillMaxSize().background(Color.White)
        ) { innerPadding ->
            Column(
                modifier = Modifier.padding(innerPadding).fillMaxSize()
            ) {
                texto("Tareas", Modifier.padding(bottom = 16.dp))
                cuadroDeBusqueda()
                lista()
                Button(onClick = {navController.navigate(route = Rutas.AgregarTareas.ruta)

                }, modifier = Modifier.fillMaxWidth().
                padding(horizontal = 30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1567A6),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Agregar Tarea")
                }
            }
        }
}

@Composable
fun texto(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier,
        fontSize = 45.sp,
        fontWeight = FontWeight.Bold
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cuadroDeBusqueda() {
    var buscar by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        TextField(
            value = buscar,
            onValueChange = { buscar = it },
            placeholder = { Text("Buscar...") },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.LightGray,
                focusedIndicatorColor = Color.Blue,
                unfocusedIndicatorColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Composable
fun lista() {
    val tareas = listOf(
        "Titulo",
        "Titulo",
    )
    Column {
        for (tarea in tareas) {
            cuadroDeTareas(tarea)
            Text(text = "Completar antes de:   08/10/2024",
                modifier = Modifier.padding(horizontal = 50.dp),
                fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun cuadroDeTareas(tarea: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 40.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = verdeClaro)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ){
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar tarea",
                modifier = Modifier.size(35.dp).align(Alignment.BottomEnd),
                tint = Color.Gray,
            )
            Text(
                text = tarea,
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar tarea",
                modifier = Modifier.size(35.dp).align(Alignment.TopEnd),
                tint = Color.Red,
            )
        }
    }
}