package com.example.sice

import android.app.Application
import android.content.Context
import android.util.Log
import com.example.sice.di.DefaultAppContainer
import com.example.sice.platform.registerAndroidPlatform

class App : Application() {
    // contenedor inicializado perezosamente con applicationContext
    val container: DefaultAppContainer by lazy { DefaultAppContainer(applicationContext) }

    override fun onCreate() {
        super.onCreate()
        val prefs = getSharedPreferences("session", Context.MODE_PRIVATE)
        val savedMat = prefs.getString("matricula", null)
        Log.d("DEBUG_SESSION", "App.onCreate savedMat=$savedMat")
        if (!savedMat.isNullOrBlank()) {
            Log.d("DEBUG_SESSION", "Restoring repo matricula before set: ${container.mainRepository.getCurrentMatricula()}")
            container.mainRepository.setCurrentMatricula(savedMat)
            Log.d("DEBUG_SESSION", "Restored repo matricula after set: ${container.mainRepository.getCurrentMatricula()}")
        }
        registerAndroidPlatform(applicationContext)
    }
}
