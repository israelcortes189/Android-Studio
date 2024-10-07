package com.example.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.TextField
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AgregarNotas() {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            texto2(
                name = "Agregar Nota",
                modifier = Modifier.padding(horizontal = 100.dp),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold

            )
            Spacer(modifier = Modifier.height(80.dp))
            texto2(
                name = "Titulo:",
                modifier = Modifier.padding(horizontal = 20.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            val estadoDeTextoV = remember { mutableStateOf("") }
            TextField(
                value = estadoDeTextoV.value,
                onValueChange = { estadoDeTextoV.value = it },
                placeholder = { Text("Escribe el título de la nota...") },
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(50.dp))
            texto2(
                name = "Descripcion:",
                modifier = Modifier.padding(horizontal = 20.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            val estadoDeTextoV2 = remember { mutableStateOf("") }
            TextField(
                value = estadoDeTextoV2.value,
                onValueChange = { estadoDeTextoV2.value = it },
                placeholder = { Text("Descripcion de la nota...") },
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth()
                    .height(200.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1567A6),
                    contentColor = Color.White
                )
            ) {
                Text(text = "Adjuntar archivos")
            }
        }
    }
}

@Composable
fun texto2(name: String, fontSize: androidx.compose.ui.unit.TextUnit, modifier: Modifier = Modifier, fontWeight: FontWeight) {
    Text(
        text = name,
        modifier = modifier,
        fontSize = fontSize,
        fontWeight = fontWeight
    )
}


