@file:Suppress("NAME_SHADOWING")

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


val verdeClaro = Color(0xFFCFFFCF)

@Composable
fun Notas(navController: NavHostController) {

    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    MenuLateral(
        navController = navController,
        drawerState = drawerState
    )
    {
        Scaffold(
            topBar = {
                TopBar(drawerState)
            },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
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
                            contentDescription = stringResource(R.string.regresar_Notas),
                            modifier = Modifier
                                .size(35.dp)
                                .padding(end = 8.dp)
                                .clickable { navController.popBackStack() },
                            tint = Color.Gray,
                        )
                        textoNotas(stringResource(R.string.notas_Notas), Modifier.weight(1f))
                    }
                    cuadroDeBusquedaNotas()
                    listaNotas()
                    Button(
                        onClick = { navController.navigate(route = Rutas.AgregarNotas.ruta) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp)
                    ) {
                        Text(text = stringResource(R.string.agregar_nota_Notas))
                    }
                }
            }
        }
    }
}

@Composable
fun textoNotas(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally),
        fontSize = 45.sp,
        fontWeight = FontWeight.Bold
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cuadroDeBusquedaNotas() {
    var buscar by rememberSaveable { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        TextField(
            value = buscar,
            onValueChange = { buscar = it },
            placeholder = { Text(stringResource(R.string.buscar)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
              //  containerColor = Color.LightGray,
                focusedIndicatorColor = Color.Blue,
                unfocusedIndicatorColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Composable
fun listaNotas() {
    val notas = listOf(
        "Titulo",
        "Titulo",
        "Titulo",
        "Titulo",
        "Titulo",

    )

    Column {
        for (nota in notas) {
            cuadroDeTareasNotas(nota)
            Spacer(modifier = Modifier.height(30.dp))
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}
val colorAzulVibrante = Color(0xFF4A90E2)
@Composable
fun cuadroDeTareasNotas(nota: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(horizontal = 40.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = verdeClaro)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(R.string.editar_nota),
                modifier = Modifier
                    .size(35.dp)
                    .align(Alignment.BottomEnd),
                tint = Color.Gray,
            )
            Text(
                text = nota,
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = stringResource(R.string.eliminar_nota),
                modifier = Modifier
                    .size(35.dp)
                    .align(Alignment.TopEnd),
                tint = Color.Red,
            )
        }
    }
}

