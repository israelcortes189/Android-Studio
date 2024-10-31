package com.example.proyecto

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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

val verdeAzul = Color(0xFFE0E99D)

@Composable
fun Tareas(navController: NavHostController) {
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed)
    MenuLateral(
        navController= navController,
        drawerState = drawerState
    ){
        Scaffold(topBar = {
            TopBar(drawerState)
        },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.regresar_Tareas),
                            modifier = Modifier
                                .size(35.dp)
                                .padding(end = 8.dp)
                                .clickable { navController.popBackStack() },
                            tint = Color.Gray,
                        )
                        texto(stringResource(R.string.tareas1), Modifier.weight(1f))
                    }
                    cuadroDeBusqueda()
                    lista()
                    Button(
                        onClick = { navController.navigate(route = Rutas.AgregarTareas.ruta) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1567A6),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = stringResource(R.string.agregar_tarea_Tarea))
                    }
                }
            }
        }
    }
}

@Composable
fun texto(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally),
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
            placeholder = { Text(stringResource(R.string.buscar_Tareas)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
               // containerColor = Color.LightGray,
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
        "Titulo",

    )
    Column {
        for (tarea in tareas) {
            cuadroDeTareas(tarea)
            Text(text = "Completar antes de:  08/10/2024",
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
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 40.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = verdeAzul)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ){
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.editar_tarea),
                modifier = Modifier
                    .size(35.dp)
                    .align(Alignment.BottomEnd),
                tint = Color.Gray,
            )
            Text(
                text = tarea,
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.eliminar_tarea),
                modifier = Modifier
                    .size(35.dp)
                    .align(Alignment.TopEnd),
                tint = Color.Red,
            )
        }
    }
}