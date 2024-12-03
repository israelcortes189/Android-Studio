package com.example.proyecto.Componentes

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.proyecto.ComposeFileProvider
import java.io.File
import java.util.UUID

@Composable
fun MenuFotos(onImagesSelected: (List<Uri>) -> Unit, onVideosSelected: (List<Uri>) -> Unit, onFileTypeChanged: (String) -> Unit) {
    val context = LocalContext.current
    var expanded by remember { mutableStateOf(false) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var tipo = "tipo"
    var imageUri: Uri? by remember { mutableStateOf(null) }
    var videoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Launcher para seleccionar imágenes o videos
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uri ->
            uri?.let {
                imageUris = imageUris + it  // Agregar nueva imagen a la lista
                onImagesSelected(imageUris)  // Llama al callback con la lista actualizada
            }
            onFileTypeChanged("foto")
        }
    )

    // Launcher para seleccionar videos
    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents(),
        onResult = { uris ->
            uris?.let{
                videoUris = videoUris + it
                onVideosSelected(videoUris)
            }
            onFileTypeChanged("video")
        }
    )

    // Lector de imágenes desde la cámara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->444
            if (success && imageUri != null) {
                // Agregar la imagen tomada a la lista de imágenes
                imageUri?.let {
                    imageUris = imageUris + it  // Agregar la nueva foto a la lista
                    onImagesSelected(imageUris)  // Llama al callback con la lista actualizada
                }
                onFileTypeChanged("foto")  // Notifica que se capturó una foto
            }
        }
    )

    // Lector de videos desde la cámara
    val videoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CaptureVideo(),
        onResult = { success ->
            if (success && imageUri != null) {  // Asegúrate de que tienes una URI para el video
                // Agregar el video capturado a la lista de videos
                imageUri?.let {
                    videoUris = videoUris + it  // Agregar el video a la lista
                    onVideosSelected(videoUris)  // Llama al callback con la lista de videos
                }
                onFileTypeChanged("video")  // Notifica que se capturó un video
                Log.d("VIDEO", "Video capturado: $imageUri")
            } else {
                Log.d("VIDEO", "Captura de video cancelada o fallida")
            }
        }
    )

    // Solicitud de permisos para la cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                imageUri = ComposeFileProvider.getImageUri(context) // Genera la URI aquí
                imageUri?.let {
                    cameraLauncher.launch(it)
                }
            } else {
                Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    )

    Box(
        modifier = Modifier.fillMaxWidth()
            .wrapContentSize(Alignment.TopEnd)
            .size(50.dp)
    ) {
        IconButton(onClick = { expanded = !expanded },
            modifier = Modifier.size(40.dp)) {
            Icon(
                modifier = Modifier.size(35.dp),
                imageVector = Icons.Default.Add,
                contentDescription = "More"
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(200.dp)
        ) {
            // Opción para seleccionar una imagen o video
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "Load",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar Imagen")
                    }
                },
                onClick = {
                        imagePicker.launch("image/*")
                }
            )

            // Opción para seleccionar un video
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Load",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Seleccionar Video")
                    }
                },
                onClick = {
                    videoPicker.launch("video/*")
                }
            )

            // Opción para tomar una foto con la cámara
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Take",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar Foto")
                    }
                },
                onClick = {
                    // Verificamos si el permiso de cámara está concedido
                    val permissionCheckResult = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    )
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        imageUri = ComposeFileProvider.getImageUri(context)
                        cameraLauncher.launch(imageUri!!) // Lanzamos la cámara
                    } else {
                        // Si no está concedido, solicitamos el permiso de cámara
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            )

            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = "Take",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tomar Video")
                    }
                },
                onClick = {
                    // Verificamos si el permiso de cámara está concedido
                    val permissionCheckResult = ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    )
                    if (permissionCheckResult == PackageManager.PERMISSION_GRANTED) {
                        val videoUri = ComposeFileProvider.getImageUri(context) // Suponiendo una función similar a getImageUri
                        videoLauncher.launch(videoUri)
                        imageUri = videoUri
                    } else {
                        // Si no está concedido, solicitamos el permiso de cámara
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
            )

            // Opción para grabar voz (sin implementación por ahora)
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Grabación de Voz")
                    }
                },
                onClick = { /* Implementar grabación de voz si es necesario */ }
            )
        }
    }
}
