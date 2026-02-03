package com.example.marsphotos.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.marsphotos.ui.screens.SNViewModel




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: SNViewModel
) {
    val profile = viewModel.profileState
    val loading = viewModel.isLoading

    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil Académico") }
            )
        }
    ) { padding ->

        if (loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (profile != null) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                ProfileItem("Matrícula", profile.matricula)
                ProfileItem("Nombre", profile.nombre)
                ProfileItem("Carrera", profile.carrera)
                ProfileItem("Semestre", profile.semestre)
                ProfileItem("Promedio", profile.promedio)

            }

        } else {
            Text(
                text = "No se pudo cargar el perfil",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun ProfileItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
        Divider()
    }
}

