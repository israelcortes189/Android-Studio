package com.example.proyecto

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

val verdeClaro = Color(0xFFCFFFCF)
@Composable
fun Notas(navController: NavHostController) {
    Scaffold(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding).fillMaxSize()
        )
        {
            item{
                    textoNotas("Notas", Modifier.padding(bottom = 16.dp))
                    cuadroDeBusquedaNotas()
                    listaNotas()
                    Button(onClick = {navController.navigate(route = Rutas.AgregarTareas.ruta)},
                        modifier = Modifier.fillMaxWidth().
                        padding(horizontal = 30.dp)
                    ) {
                        Text(text = "Agregar Nota")
                    }
            }
        }
    }
}

@Composable
fun textoNotas(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier,
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
fun listaNotas() {
    val notas = listOf(
        "Titulo",
        "Titulo",
        "Titulo",
        "Titulo",
        "Titulo",
        "el jysus es nina "
    )

    Column {
        for (nota in notas) {
            cuadroDeTareasNotas(nota)
            Spacer(modifier = Modifier.height(30.dp))
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun cuadroDeTareasNotas(nota: String) {
    Card(
        modifier = Modifier.fillMaxWidth().height(200.dp).padding(horizontal = 40.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = verdeClaro)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Editar nota",
                modifier = Modifier.size(35.dp).align(Alignment.BottomEnd),
                tint = Color.Gray,
            )
            Text(
                text = nota,
                modifier = Modifier.padding(16.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Eliminar nota",
                modifier = Modifier.size(35.dp).align(Alignment.TopEnd),
                tint = Color.Red,
            )
        }
    }
}

