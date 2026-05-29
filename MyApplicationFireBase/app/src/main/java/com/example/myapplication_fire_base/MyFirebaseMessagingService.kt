package com.example.myapplication_fire_base
import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.myapplication_fire_base.R.drawable.ic_stat_ic_notification
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {

    // Se ejecuta cuando el token de Firebase cambia o se genera por primera vez
    override fun onNewToken(token: String) {
        Log.d("FCM", "Nuevo token: $token")
    }

    // Se ejecuta cuando llega un mensaje (estando la app en primer plano o si trae 'data')
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Extrae el título y cuerpo priorizando la notificación y luego los datos
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Aviso"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: ""

        showNotification(title, body)
    }

    private fun showNotification(title: String, body: String) {
        val channelId = "default_channel"
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Configuración necesaria para
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Notificaciones", NotificationManager.IMPORTANCE_HIGH)
            nm.createNotificationChannel(channel)
        }

        // Intento para abrir la app al tocar la notificación
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        // Flag IMMUTABLE es obligatorio en versiones modernas de Android
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // Construcción de la notificación
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // Icono genérico del sistema
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true) // Se cierra al tocarla
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()

        // Mostrar la notificación usando un ID único basado en el tiempo actual
        nm.notify(System.currentTimeMillis().toInt(), notification)
    }
}