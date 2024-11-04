package com.example.proyecto.Navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.proyecto.AgregarNotas
import com.example.proyecto.AgregarTareas
import com.example.proyecto.EditarNota
import com.example.proyecto.EditarTarea
import com.example.proyecto.Notas
import com.example.proyecto.Principal
import com.example.proyecto.Rutas
import com.example.proyecto.Tareas
import com.example.proyecto.screens.NotasViewModel

@Composable
fun navegacion(notaViewModel: NotasViewModel,) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Rutas.Principal.ruta) {
        composable(route = Rutas.Tareas.ruta) {
            Tareas(navController, notaViewModel = notaViewModel)
        }
        composable(route = Rutas.Principal.ruta) {
            Principal(navController)
        }
        composable(route = Rutas.Notas.ruta) {
            Notas(navController, notaViewModel = notaViewModel)
        }
        composable(route = Rutas.AgregarTareas.ruta) {
            AgregarTareas(navController, notaViewModel = notaViewModel)
        }
        composable(route = Rutas.AgregarNotas.ruta) {
            AgregarNotas(navController, notaViewModel = notaViewModel)
        }

        composable(route = Rutas.EditarNota.ruta,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditarNota(navController = navController, notaViewModel = notaViewModel, id = id)
        }

        composable(route = Rutas.EditarTarea.ruta,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditarTarea(navController = navController, notaViewModel = notaViewModel, id = id)
        }
    }
}