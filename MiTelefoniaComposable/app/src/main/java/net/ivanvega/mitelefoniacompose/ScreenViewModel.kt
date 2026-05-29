package net.ivanvega.mitelefoniacompose

import android.telephony.SmsManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.Manifest
import android.content.pm.PackageManager

class ScreenViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs: SharedPreferences =
        application.getSharedPreferences("auto_reply_prefs", Context.MODE_PRIVATE)

    var phoneNumber by mutableStateOf(prefs.getString(KEY_NUMBER, "") ?: "")
    var message by mutableStateOf(prefs.getString(KEY_MESSAGE, "") ?: "")
    var enabled by mutableStateOf(prefs.getBoolean(KEY_ENABLED, false))

    // Eventos one-shot: puedes cambiar a SharedFlow si prefieres
    private val _events = MutableStateFlow<Event?>(null)
    val events: StateFlow<Event?> = _events

    fun onPhoneNumberChange(newValue: String) { phoneNumber = newValue }
    fun onMessageChange(newValue: String) { message = newValue }

    fun saveSettings() {
        prefs.edit()
            .putString(KEY_NUMBER, phoneNumber)
            .putString(KEY_MESSAGE, message)
            .apply()
    }

    fun toggleEnabled() {
        val newState = !enabled
        enabled = newState
        prefs.edit().putBoolean(KEY_ENABLED, newState).apply()

        if (newState) {
            saveSettings()
            if (!hasSmsPermission() || !hasReadPhoneStatePermission()) {
                _events.value = Event.RequestPermissions
                return
            }
            _events.value = Event.StartService
        } else {
            _events.value = Event.StopService
        }
    }

    fun sendSMSManual() {
        saveSettings()
        if (phoneNumber.isBlank() || message.isBlank()) {
            _events.value = Event.Error("Número o mensaje vacíos")
            return
        }
        if (!hasSmsPermission()) {
            _events.value = Event.RequestPermissions
            return
        }
        viewModelScope.launch {
            sendSmsInternal(phoneNumber, message)
        }
    }

    private suspend fun sendSmsInternal(number: String, text: String) {
        try {
            val normalized = normalize(number)
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(normalized, null, text, null, null)
            _events.value = Event.Info("SMS enviado a $normalized")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending SMS: ${e.message}")
            _events.value = Event.Error("Error enviando SMS: ${e.message}")
        }
    }

    private fun normalize(number: String): String = number.filter { it.isDigit() || it == '+' }

    private fun hasSmsPermission(): Boolean {
        val ctx = getApplication<Application>().applicationContext
        return ContextCompat.checkSelfPermission(ctx, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
    }

    private fun hasReadPhoneStatePermission(): Boolean {
        val ctx = getApplication<Application>().applicationContext
        return ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
    }

    fun clearEvent() { _events.value = null }

    sealed class Event {
        object RequestPermissions : Event()
        object StartService : Event()
        object StopService : Event()
        data class Error(val message: String) : Event()
        data class Info(val message: String) : Event()
    }

    companion object {
        private const val TAG = "ScreenViewModel"
        private const val KEY_NUMBER = "auto_number"
        private const val KEY_MESSAGE = "auto_message"
        private const val KEY_ENABLED = "auto_enabled"
    }
}


