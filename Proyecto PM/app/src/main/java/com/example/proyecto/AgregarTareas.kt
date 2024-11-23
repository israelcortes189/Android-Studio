package com.example.proyecto

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.proyecto.Componentes.MenuFotos
import com.example.proyecto.Componentes.MenuLateral
import com.example.proyecto.Componentes.TopBar
import com.example.proyecto.Models.Tarea
import com.example.proyecto.screens.NotasViewModel

@Composable
fun AgregarTareas(navController: NavHostController, notaViewModel: NotasViewModel) {
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed)

    var uri by remember { mutableStateOf<Uri?>(null) }
    var hasImage by remember { mutableStateOf(false) }
    var hasVideo by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

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
                    // Icono de regresar en notas
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.regresar_Agregar_Tarea),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { navController.popBackStack() },
                            tint = Color.Gray
                        )
                        texto2(
                            name = stringResource(R.string.agregar_nota_Agregar_Tarea),
                            modifier = Modifier.padding(horizontal = 40.dp),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold

                        )
                        MenuFotos(onImageSelected = { selectedUri ->
                            imageUri = selectedUri // Actualizamos el estado con la imagen seleccionada
                            hasImage = selectedUri != null // Verificamos si hay una imagen
                            uri = selectedUri // Guardamos la URI en la variable uri
                        })
                    }

                    Spacer(modifier = Modifier.height(80.dp))


                    texto2(
                        name = stringResource(R.string.titulo_Agregar_Tarea),
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val estadoDeTextoV = rememberSaveable { mutableStateOf("") }
                    TextField(
                        value = estadoDeTextoV.value,
                        onValueChange = { estadoDeTextoV.value = it },
                        placeholder = { Text(stringResource(R.string.escribe_el_t_tulo_de_la_nota)) },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    texto2(
                        name = stringResource(R.string.descripcion_Agregar_Tarea),
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val estadoDeTextoV2 = rememberSaveable { mutableStateOf("") }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        TextField(
                            value = estadoDeTextoV2.value,
                            onValueChange = { estadoDeTextoV2.value = it },
                            placeholder = { Text(stringResource(R.string.descripcion_de_la_nota)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )


                        Spacer(modifier = Modifier.height(16.dp)) // Espacio entre los elementos

                        if ((hasImage || hasVideo) && imageUri != null) {
                            texto2(
                                name = "Imagenes: ",
                                modifier = Modifier.padding(horizontal = 20.dp),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp)) // Espacio entre los elementos

                            if (hasImage) {
                                AsyncImage(
                                    model = imageUri,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .size(75.dp),
                                    contentDescription = "Selected image"
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if(estadoDeTextoV.value.isNotBlank() && estadoDeTextoV2.value.isNotBlank()){
                                notaViewModel.addTarea(Tarea(titulo = estadoDeTextoV.value, descripcion = estadoDeTextoV2.value))
                            }
                            navController.navigate(route = Rutas.Tareas.ruta)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1567A6),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = stringResource(R.string.agregar_nota_Agregar_Tarea_1))
                    }
                }
            }
        }
    }
}

@Composable
fun texto(name: String, fontSize: androidx.compose.ui.unit.TextUnit, modifier: Modifier = Modifier, fontWeight: FontWeight) {
    Text(
        text = name,
        modifier = modifier,
        fontSize = fontSize,
        fontWeight = fontWeight
    )
}



