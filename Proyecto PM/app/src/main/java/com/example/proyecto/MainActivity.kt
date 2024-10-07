package com.example.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            navegacion()
        }
    }
}

@Composable
fun navegacion(){
    val navController =  rememberNavController()
    NavHost(navController = navController, startDestination = Rutas.Principal.ruta){
        composable (route= Rutas.Tareas.ruta){
            Tareas(navController)
        }
        composable (route= Rutas.Principal.ruta){
            Principal(navController)
        }
        composable (route= Rutas.Notas.ruta){
            Notas(navController)
        }
    }
}