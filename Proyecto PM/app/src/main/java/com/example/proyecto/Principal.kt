package com.example.proyecto

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@Composable
fun Principal(navController: NavHostController) {
    val color = Color(0xFF1567A6)
    val buttonShape = RoundedCornerShape(8.dp)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 5.dp)
    ) {
        item {
            // se agrego el cerrar sesion
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, end = 8.dp),
                contentAlignment = Alignment.TopEnd
            ) {
                Button(
                    onClick = {

                        navController.navigate(route = "Login")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    shape = buttonShape
                ) {
                    Text(text = stringResource(R.string.salir), color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                text = stringResource(R.string.bienvenido_amigo),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 90.dp),
                textAlign = TextAlign.Center,
                style = TextStyle(fontFamily = FontFamily.SansSerif)
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    contentDescription = null,
                    painter = painterResource(id = R.drawable.img1),
                    modifier = Modifier
                )
            }
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { navController.navigate(route = Rutas.Tareas.ruta) },
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .border(width = 1.dp, color = Color.Black, shape = buttonShape),
                    colors = ButtonDefaults.buttonColors(containerColor = color),
                    shape = buttonShape
                ) {
                    Text(
                        text = stringResource(R.string.tareas_Principal),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 100.dp)
                    )
                }
            }

            // Botón de Notas centrado
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { navController.navigate(route = Rutas.Notas.ruta) },
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .border(width = 1.dp, color = Color.Black, shape = buttonShape),
                    colors = ButtonDefaults.buttonColors(containerColor = color),
                    shape = buttonShape
                ) {
                    Text(
                        text = stringResource(R.string.notas_Principal),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 100.dp)
                    )
                }
            }
        }
    }
}