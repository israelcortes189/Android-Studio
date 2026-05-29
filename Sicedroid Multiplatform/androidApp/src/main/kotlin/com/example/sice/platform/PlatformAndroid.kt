package com.example.sice.platform

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.sice.workers.LocalWorker
import com.example.sice.workers.RemoteWorker

fun registerAndroidPlatform(appContext: Context) {
    Platform.isOnline = isOnline@{
        val cm = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return@isOnline false
        val caps = cm.getNetworkCapabilities(cm.activeNetwork) ?: return@isOnline false
        caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                || caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    }

    Platform.scheduleSync = { tipo, matricula ->
        val uniqueName = "sync_${tipo}_$matricula"
        val wm = WorkManager.getInstance(appContext)
        val remoteRequest = OneTimeWorkRequestBuilder<RemoteWorker>()
            .setInputData(workDataOf("tipo" to tipo, "matricula" to matricula)).build()
        val localRequest = OneTimeWorkRequestBuilder<LocalWorker>()
            .setInputData(workDataOf("tipo" to tipo, "matricula" to matricula, "remoteId" to remoteRequest.id.toString())).build()
        wm.beginUniqueWork(uniqueName, ExistingWorkPolicy.KEEP, remoteRequest).then(localRequest).enqueue()
    }

    Platform.readSavedMatricula = {
        val prefs = appContext.getSharedPreferences("session", Context.MODE_PRIVATE)
        prefs.getString("matricula", null)
    }

    Platform.saveMatricula = { mat ->
        val prefs = appContext.getSharedPreferences("session", Context.MODE_PRIVATE)
        prefs.edit().putString("matricula", mat).putBoolean("hasSession", !mat.isNullOrBlank()).apply()
    }
}

