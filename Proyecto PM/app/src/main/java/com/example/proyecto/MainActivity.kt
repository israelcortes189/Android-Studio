package com.example.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.proyecto.Navegacion.navegacion
import com.example.proyecto.screens.NotasViewModel
import com.example.proyecto.ui.theme.ProyectoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Aplicar el tema dinámico basado en el tema del sistema
            ProyectoTheme {
                val notaViewModel : NotasViewModel by viewModels()
                // Navegación de la aplicación
                navegacion(notaViewModel)
            }
        }
    }
}
