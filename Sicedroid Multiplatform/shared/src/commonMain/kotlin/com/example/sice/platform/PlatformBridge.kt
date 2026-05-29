package com.example.sice.platform

object Platform {
    var isOnline: () -> Boolean = { true }
    var scheduleSync: (tipo: String, matricula: String) -> Unit = { _, _ -> }
    var readSavedMatricula: () -> String? = { null }
    var saveMatricula: (String?) -> Unit = { _ -> }
}