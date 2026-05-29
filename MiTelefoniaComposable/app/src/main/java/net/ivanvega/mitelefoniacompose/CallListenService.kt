package net.ivanvega.mitelefoniacompose

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.telephony.SmsManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.app.PendingIntent


class CallListenService : Service() {

    private lateinit var telephonyManager: TelephonyManager
    private var phoneStateListener: PhoneStateListener? = null

    override fun onCreate() {
        super.onCreate()
        telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        Log.d(TAG, "Service onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand - starting foreground")
        try {
            createNotificationChannelIfNeeded() // crea canal antes
            startForeground(NOTIF_ID, createNotification()) // debe ejecutarse rápido
        } catch (e: Exception) {
            Log.e(TAG, "Error starting foreground: ${e.message}", e)
            // Si falla, detener el servicio para evitar estado inconsistente
            stopSelf()
            return START_NOT_STICKY
        }

        // Registrar listener (ligero) después de startForeground
        registerListener()

        return START_STICKY
    }


    private fun registerListener() {
        Log.d(TAG, "Registering PhoneStateListener")
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "READ_PHONE_STATE no concedido; abort registerListener")
            return
        }
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, incomingNumber: String?) {
                Log.d(TAG, "onCallStateChanged state=$state incoming=$incomingNumber")
                handleCallState(state, incomingNumber)
            }
        }
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }


    private fun handleCallState(state: Int, incomingNumber: String?) {
        try {
            val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val target = prefs.getString(KEY_NUMBER, "") ?: ""
            val message = prefs.getString(KEY_MESSAGE, "") ?: ""
            val enabled = prefs.getBoolean(KEY_ENABLED, false)
            if (!enabled || target.isBlank() || message.isBlank()) return

            if (state == TelephonyManager.CALL_STATE_RINGING) {
                if (!incomingNumber.isNullOrBlank()) {
                    if (normalize(incomingNumber) == normalize(target)) {
                        sendSms(incomingNumber, message)
                    }
                } else {
                    Log.w(TAG, "incomingNumber vacío; no se puede comparar")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "handleCallState error", e)
        }
    }

    private fun normalize(number: String): String = number.filter { it.isDigit() || it == '+' }

    private fun sendSms(number: String, text: String) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "SEND_SMS no concedido; no se envía SMS")
            return
        }
        try {
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(number, null, text, null, null)
            Log.d(TAG, "SMS enviado a $number")
        } catch (e: Exception) {
            Log.e(TAG, "Error enviando SMS", e)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "Service onDestroy - unregistering listener")
        phoneStateListener?.let { telephonyManager.listen(it, PhoneStateListener.LISTEN_NONE) }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotificationChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            val channel = NotificationChannel(CHANNEL_ID, "Auto Reply", NotificationManager.IMPORTANCE_LOW)
            nm.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        // Intent para abrir la Activity principal al tocar la notificación
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        else
            PendingIntent.FLAG_UPDATE_CURRENT

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, pendingIntentFlags)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Respuesta automática activa")
            .setContentText("Escuchando llamadas entrantes")
            .setSmallIcon(android.R.drawable.sym_action_call)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    companion object {
        private const val TAG = "CallListenService"
        const val PREFS_NAME = "auto_reply_prefs"
        const val KEY_NUMBER = "auto_number"
        const val KEY_MESSAGE = "auto_message"
        const val KEY_ENABLED = "auto_enabled"
        private const val NOTIF_ID = 1
        private const val CHANNEL_ID = "auto_reply_channel"
    }
}
