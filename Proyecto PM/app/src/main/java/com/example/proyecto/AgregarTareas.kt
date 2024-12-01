package com.example.proyecto

import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Looper.prepare
import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.proyecto.Componentes.MenuFotos
import com.example.proyecto.Componentes.MenuLateral
import com.example.proyecto.Componentes.TopBar
import com.example.proyecto.Models.Tarea
import com.example.proyecto.screens.NotasViewModel
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun AgregarTareas(navController: NavHostController, notaViewModel: NotasViewModel) {
    var tipo by remember { mutableStateOf("ninguno") }
    val context = LocalContext.current
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed)

    var uri by remember { mutableStateOf<Uri?>(null) }
    var hasImage by remember { mutableStateOf(false) }
    var hasVideo by remember { mutableStateOf(false) }
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var videoUris by remember { mutableStateOf<List<Uri>>(emptyList()) }

    var showFullScreenPlayer by remember { mutableStateOf(false) }
    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var showFullScreenImage by remember { mutableStateOf(false) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    MenuLateral(
        navController = navController,
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopBar(drawerState)
            },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                item {
                    // Icono de regresar en notas
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.regresar_Agregar_Tarea),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { navController.popBackStack() },
                            tint = Color.Gray
                        )
                        texto2(
                            name = stringResource(R.string.agregar_nota_Agregar_Tarea),
                            modifier = Modifier.padding(horizontal = 40.dp),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold
                        )
                        MenuFotos(
                            onImagesSelected = { selectedUris ->
                                imageUris = selectedUris
                            },
                            onVideosSelected = { selectedUris ->
                                videoUris = videoUris + selectedUris.filter { it !in videoUris }
                            },
                            onFileTypeChanged = { nuevoTipo ->
                                when (nuevoTipo) {
                                    "foto" -> {
                                        hasImage = true
                                    }
                                    "video" -> {
                                        hasVideo = true
                                    }
                                    else -> {
                                        hasImage = false
                                        hasVideo = false
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(80.dp))

                    // Campo de título
                    texto2(
                        name = stringResource(R.string.titulo_Agregar_Tarea),
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val estadoDeTextoV = rememberSaveable { mutableStateOf("") }
                    TextField(
                        value = estadoDeTextoV.value,
                        onValueChange = { estadoDeTextoV.value = it },
                        placeholder = { Text(stringResource(R.string.escribe_el_t_tulo_de_la_nota)) },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(50.dp))

                    // Campo de descripción
                    texto2(
                        name = stringResource(R.string.descripcion_Agregar_Tarea),
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    val estadoDeTextoV2 = rememberSaveable { mutableStateOf("") }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        TextField(
                            value = estadoDeTextoV2.value,
                            onValueChange = { estadoDeTextoV2.value = it },
                            placeholder = { Text(stringResource(R.string.descripcion_de_la_nota)) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (hasImage && imageUris.isNotEmpty()) {
                        texto2(
                            name = "Imágenes seleccionadas:",
                            modifier = Modifier.padding(horizontal = 20.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        LazyRow(modifier = Modifier.padding(horizontal = 20.dp)) {
                            items(imageUris) { uri ->
                                AsyncImage(
                                    model = uri,
                                    contentDescription = "Selected image",
                                    modifier = Modifier
                                        .size(75.dp)
                                        .padding(4.dp)
                                        .clickable {
                                            selectedImageUri = uri
                                            showFullScreenImage = true
                                        }
                                )
                            }
                        }
                        if (showFullScreenImage && selectedImageUri != null) {
                            FullScreenImageViewer(
                                imageUri = selectedImageUri!!
                            ) {
                                showFullScreenImage = false
                            }
                        }
                    }

                    // Videos seleccionados
                    if (hasVideo && videoUris.isNotEmpty()) {
                        texto2(
                            name = "Videos seleccionados:",
                            modifier = Modifier.padding(horizontal = 20.dp),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )

                        LazyRow(modifier = Modifier.padding(horizontal = 20.dp)) {
                            items(videoUris) { uri ->
                                val context = LocalContext.current
                                var thumbnailBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

                                // Generar miniatura usando MediaMetadataRetriever
                                LaunchedEffect(uri) {
                                    withContext(Dispatchers.IO) {
                                        val retriever = MediaMetadataRetriever()
                                        try {
                                            retriever.setDataSource(context, uri) // Usa el contexto y URI
                                            val bitmap = retriever.getFrameAtTime(0) // Captura el primer frame
                                            thumbnailBitmap = bitmap?.asImageBitmap()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        } finally {
                                            retriever.release()
                                        }
                                    }
                                }

                                // Mostrar la miniatura o el ícono de carga
                                thumbnailBitmap?.let { bitmap ->
                                    Image(
                                        bitmap = bitmap,
                                        contentDescription = "Miniatura del video",
                                        modifier = Modifier
                                            .size(75.dp)
                                            .padding(8.dp)
                                            .clickable {
                                                selectedVideoUri = uri
                                                showFullScreenPlayer = true
                                            },
                                        contentScale = ContentScale.Crop
                                    )
                                } ?: run {
                                    Icon(
                                        imageVector = Icons.Default.Videocam,
                                        contentDescription = "Cargando miniatura...",
                                        modifier = Modifier
                                            .size(75.dp)
                                            .padding(8.dp),
                                        tint = Color.Gray
                                    )
                                }
                            }
                        }
                        if (showFullScreenPlayer && selectedVideoUri != null) {
                            FullScreenVideoPlayer(
                                videoUri = selectedVideoUri!!
                            ) {
                                showFullScreenPlayer = false
                            }
                        }
                    }

                    // Botón de agregar tarea
                    Button(
                        onClick = {
                            if (estadoDeTextoV.value.isNotBlank() && estadoDeTextoV2.value.isNotBlank()) {
                                val imagenUris = imageUris.map { it.toString() }
                                val videoUris = videoUris.map { it.toString() }

                                notaViewModel.addTarea(
                                    Tarea(
                                        titulo = estadoDeTextoV.value,
                                        descripcion = estadoDeTextoV2.value
                                    ),
                                    imagenUris = imagenUris,
                                    videoUris = videoUris
                                )
                            }
                            navController.navigate(route = Rutas.Tareas.ruta)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1567A6),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = stringResource(R.string.agregar_nota_Agregar_Tarea_1))
                    }
                }
            }
        }
    }
}

@Composable
fun texto(name: String, fontSize: androidx.compose.ui.unit.TextUnit, modifier: Modifier = Modifier, fontWeight: FontWeight) {
    Text(
        text = name,
        modifier = modifier,
        fontSize = fontSize,
        fontWeight = fontWeight
    )
}

@Composable
fun FullScreenVideoPlayer(videoUri: Uri, onDismiss: () -> Unit) {
    val context = LocalContext.current
    val exoPlayer = remember {
        SimpleExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(videoUri))
            prepare()
        }
    }

    DisposableEffect(exoPlayer) {
        onDispose {
            exoPlayer.release()
        }
    }

    Dialog(onDismissRequest = {
        exoPlayer.stop() // Detener la reproducción
        exoPlayer.release() // Liberar el reproductor
        onDismiss()
    }) {
        Box(
            modifier = Modifier
                .fillMaxSize() // Hacer que el reproductor ocupe toda la pantalla
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        player = exoPlayer
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize() // Reproductor ocupa toda la pantalla
            )

            // Botón para cerrar el reproductor
            IconButton(
                onClick = {
                    exoPlayer.stop() // Detener la reproducción
                    exoPlayer.release() // Liberar el reproductor
                    onDismiss() // Cerrar el reproductor
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

@Composable
fun FullScreenImageViewer(imageUri: Uri, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        setImageURI(imageUri)
                        scaleType = ImageView.ScaleType.FIT_CENTER
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )

            // Botón para cerrar el visor de imagen
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}




