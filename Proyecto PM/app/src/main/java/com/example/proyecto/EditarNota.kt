package com.example.proyecto

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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.proyecto.Componentes.MenuLateral
import com.example.proyecto.Componentes.TopBar
import com.example.proyecto.Models.Nota
import com.example.proyecto.screens.NotasViewModel

@Composable
fun EditarNota(navController: NavHostController, notaViewModel: NotasViewModel, id: Int) {
    var nota by remember { mutableStateOf<Nota?>(null) }
    var titulo by remember { mutableStateOf("") }  // Estado editable para el título
    var contenido by remember { mutableStateOf("") }  // Estado editable para el contenido

    LaunchedEffect(id) {
        val fetchedNota = notaViewModel.getNotaById(id)
        nota = fetchedNota
        titulo = fetchedNota?.titulo ?: ""
        contenido = fetchedNota?.descripcion ?: ""
    }

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed)
    MenuLateral(
        navController= navController,
        drawerState = drawerState
    ){
        Scaffold(
            topBar = {
                TopBar(drawerState)
            },
            modifier = Modifier.fillMaxSize()) { innerPadding ->
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
                            name = "Editar Nota",
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
                    val estadoDeTexto = rememberSaveable { mutableStateOf("") }
                    TextField(
                        value = titulo,
                        onValueChange = { newTitle ->
                            titulo = newTitle  // Actualizar el estado editable del título
                        },
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
                    val estadoDeTexto2 = rememberSaveable { mutableStateOf("") }
                    TextField(
                            value = contenido,
                            onValueChange = { newContent ->
                                contenido = newContent  // Actualizar el estado editable del contenido
                            },
                        placeholder = { Text(stringResource(R.string.descripcion_de_la_tarea)) },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    Button(
                        onClick = {
                            nota?.let {
                                val updatedNota = it.copy(titulo = titulo, descripcion = contenido)
                                notaViewModel.updateNota(updatedNota)
                                }
                            navController.navigate(route = Rutas.Notas.ruta) },
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