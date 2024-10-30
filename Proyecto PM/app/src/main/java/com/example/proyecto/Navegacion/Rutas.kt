package com.example.proyecto

sealed class Rutas (var ruta:String) {
    object Tareas:Rutas("RutaTareas")
    object Notas:Rutas("RutaNotas")
    object AgregarTareas:Rutas("RutaAgregarTareas")
    object AgregarNotas:Rutas("RutaAgregarNotas")
    object Principal: Rutas("RutaPrincipal")
}