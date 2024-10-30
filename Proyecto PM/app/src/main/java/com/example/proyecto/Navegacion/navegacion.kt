package com.example.proyecto.Navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.proyecto.AgregarNotas
import com.example.proyecto.AgregarTareas
import com.example.proyecto.Notas
import com.example.proyecto.Principal
import com.example.proyecto.Rutas
import com.example.proyecto.Tareas

@Composable
fun navegacion() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Rutas.Principal.ruta) {
        composable(route = Rutas.Tareas.ruta) {
            Tareas(navController)
        }
        composable(route = Rutas.Principal.ruta) {
            Principal(navController)
        }
        composable(route = Rutas.Notas.ruta) {
            Notas(navController)
        }
        composable(route = Rutas.AgregarTareas.ruta) {
            AgregarTareas(navController)
        }
        composable(route = Rutas.AgregarNotas.ruta) {
            AgregarNotas(navController)
        }
    }
}