package com.example.proyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.proyecto.Navegacion.navegacion
import com.example.proyecto.ui.theme.ProyectoTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Aplicar el tema dinámico basado en el tema del sistema
            ProyectoTheme {
                // Navegación de la aplicación
                navegacion()
            }
        }
    }
}
