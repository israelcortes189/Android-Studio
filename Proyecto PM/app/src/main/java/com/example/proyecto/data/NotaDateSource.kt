package com.example.proyecto.data

import com.example.proyecto.Models.Nota

class NotaDateSource {
    fun loadNotas() : List<Nota>{
        return listOf(
            Nota(titulo= "Ir a la Escuela", descripcion = "Establecer Horario")
        )
    }
}