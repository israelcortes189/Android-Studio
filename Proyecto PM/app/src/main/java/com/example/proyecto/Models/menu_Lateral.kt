package com.example.proyecto.Models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.outlined.AddTask
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.NoteAlt
import androidx.compose.material.icons.outlined.Task
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.proyecto.R
import com.example.proyecto.Rutas

sealed class menu_Lateral(
    val icon: ImageVector,
    val title: Int,
    val ruta: String
) {
    object menu_Lateral1 : menu_Lateral(
        Icons.Outlined.Home,
        R.string.pagina_principal,
        Rutas.Principal.ruta
    )
    object menu_Lateral2 : menu_Lateral(
        Icons.Outlined.NoteAlt,
        R.string.notas,
        Rutas.Notas.ruta
    )
    object menu_Lateral3 : menu_Lateral(
        Icons.Outlined.Task,
        R.string.tareas,
        Rutas.Tareas.ruta
    )
    object menu_Lateral4 : menu_Lateral(
        Icons.Outlined.AddTask,
        R.string.agregar_notas,
        Rutas.AgregarNotas.ruta
    )
    object menu_Lateral5 : menu_Lateral(
        Icons.Outlined.AddBox,
        R.string.agregar_tareas1,
        Rutas.AgregarTareas.ruta
    )

}