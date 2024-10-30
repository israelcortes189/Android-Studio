package com.example.proyecto.Models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.Task
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.proyecto.Rutas

sealed class menu_Lateral(
    val icon: ImageVector,
    val title: String,
    val ruta: String
) {
    object menu_Lateral1 : menu_Lateral(
        Icons.Outlined.Home,
        "Pagina Principal",
        Rutas.Principal.ruta
    )
    object menu_Lateral2 : menu_Lateral(
        Icons.Outlined.NoteAlt,
        "Notas",
        Rutas.Notas.ruta
    )
    object menu_Lateral3 : menu_Lateral(
        Icons.Outlined.Task,
        "Tareas",
        Rutas.Tareas.ruta
    )
    object menu_Lateral4 : menu_Lateral(
        Icons.Outlined.AddTask,
        "Agregar Nota",
        Rutas.AgregarNotas.ruta
    )
    object menu_Lateral5 : menu_Lateral(
        Icons.Outlined.AddBox,
        "Agregar Tarea",
        Rutas.AgregarTareas.ruta
    )

}