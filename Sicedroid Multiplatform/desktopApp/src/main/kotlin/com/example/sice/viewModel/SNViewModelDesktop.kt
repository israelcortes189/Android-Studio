package com.example.sice.viewModel

import com.example.sice.data.MainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class SNViewModelDesktop(repository: MainRepository) : SNViewModelCore(repository) {

    /**
     * Hook de plataforma: iniciar la carga inicial.
     * Llama a loadProfile() que ya maneja online/offline y persistencia.
     */
    override fun start(matricula: String?) {
        // Si quieres comportamiento distinto en Desktop, cámbialo aquí.
        loadProfile(matricula)
    }

    /**
     * Hook de plataforma: limpieza al cerrar la app.
     * Llama a clear() que cancela el scope del ViewModel.
     */
    override fun shutdown() {
        clear()
    }

    /**
     * Hook que se invoca cuando una sincronización remota fue exitosa.
     * Puedes programar tareas, notificaciones o simplemente loggear.
     */
    override fun onSyncRequested(tipo: String, matricula: String) {
        // Ejemplo: no hacemos nada por defecto; puedes loggear o programar trabajo.
        // println("Sync requested: $tipo for $matricula")
    }
}