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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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

@Composable
fun AgregarTareas(navController: NavHostController) {
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
                    // Icono de regresar en notas
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 20.dp)
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
                            modifier = Modifier.padding(horizontal = 50.dp),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
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
                    TextField(
                        value = estadoDeTextoV2.value,
                        onValueChange = { estadoDeTextoV2.value = it },
                        placeholder = { Text(stringResource(R.string.descripcion_de_la_nota)) },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .height(200.dp)
                    )



                    Button(
                        onClick = { /* Acción al hacer clic en el botón */ },
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



