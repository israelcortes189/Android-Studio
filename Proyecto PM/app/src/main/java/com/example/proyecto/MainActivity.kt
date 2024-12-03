package com.example.proyecto

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
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

        // Crear el canal de notificación
        createNotificationChannel()

        setContent {
            // Aplicar el tema dinámico basado en el tema del sistema
            ProyectoTheme {
                val notaViewModel: NotasViewModel by viewModels()
                // Navegación de la aplicación
                navegacion(notaViewModel)
            }
        }
    }

    //Creamos el canal de notifucaciones de tipo ALO
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "alarm_id"
            val channelName = "Alarm Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelName, importance).apply {
                description = "Canal para alarmas"
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}

