package com.example.proyecto

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.proyecto.Componentes.MenuFotos
import com.example.proyecto.Componentes.MenuFotosEditar
import com.example.proyecto.Componentes.MenuLateral
import com.example.proyecto.Componentes.TopBar
import com.example.proyecto.Models.Media
import com.example.proyecto.Models.Tarea
import com.example.proyecto.screens.NotasViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun EditarTarea(navController: NavHostController, notaViewModel: NotasViewModel, id: Int) {
    var tarea by remember { mutableStateOf<Tarea?>(null) }
    var titulo by remember { mutableStateOf("") }
    var contenido by remember { mutableStateOf("") }
    val imagenes = remember { mutableStateListOf<Media>() }
    val videos = remember { mutableStateListOf<Media>() }
    val imagenesParaEliminar = remember { mutableStateListOf<Media>() }
    val videosParaEliminar = remember { mutableStateListOf<Media>() }
    val imagenesParaAgregar = remember { mutableStateListOf<Media>() }
    val videosParaAgregar = remember { mutableStateListOf<Media>() }
    var isImageViewerOpen by remember { mutableStateOf(false) }
    var isVideoPlayerOpen by remember { mutableStateOf(false) }
    var currentUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(id) {
        val fetchedTarea = notaViewModel.getTareaById(id)
        tarea = fetchedTarea
        titulo = fetchedTarea?.titulo ?: ""
        contenido = fetchedTarea?.descripcion ?: ""
        fetchedTarea?.id?.let {
            val mediaList = notaViewModel.getMediaByTareaId(it)
            imagenes.clear()
            videos.clear()
            imagenes.addAll(mediaList.filter { media -> media.tipo == "imagen" })
            videos.addAll(mediaList.filter { media -> media.tipo == "video" })
        }
    }

    if (isImageViewerOpen && currentUri != null) {
        FullScreenImageViewer(imageUri = currentUri!!, onDismiss = { isImageViewerOpen = false })
    }

    if (isVideoPlayerOpen && currentUri != null) {
        FullScreenVideoPlayer(videoUri = currentUri!!, onDismiss = { isVideoPlayerOpen = false })
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    MenuLateral(navController = navController, drawerState = drawerState) {
        Scaffold(
            topBar = { TopBar(drawerState) },
            modifier = Modifier.fillMaxSize()
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.regresar),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { navController.popBackStack() },
                            tint = Color.Gray
                        )
                        texto(
                            name = "Editar Tarea",
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(80.dp))
                    texto(
                        name = stringResource(R.string.titulo),
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextField(
                        value = titulo,
                        onValueChange = { newTitle -> titulo = newTitle },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(50.dp))
                    texto(
                        name = stringResource(R.string.descripcion),
                        modifier = Modifier.padding(horizontal = 20.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    TextField(
                        value = contenido,
                        onValueChange = { newContent -> contenido = newContent },
                        placeholder = { Text(stringResource(R.string.descripcion_de_la_tarea)) },
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .fillMaxWidth()
                            .height(200.dp)
                    )

                    // Integrar MenuFotos para agregar imágenes y videos
                    MenuFotosEditar(
                        onImagesSelected = { newImages ->
                            newImages.forEach { uri ->
                                val media = Media(uri = uri.toString(), tipo = "imagen", tareaId = id)
                                imagenesParaAgregar.add(media)
                                imagenes.add(media)
                            }
                        },
                        onVideosSelected = { newVideos ->
                            newVideos.forEach { uri ->
                                val media = Media(uri = uri.toString(), tipo = "video", tareaId = id)
                                videosParaAgregar.add(media)
                                videos.add(media)
                            }
                        },
                        onFileTypeChanged = { tipo ->
                            // Acción opcional si necesitas diferenciar entre foto y video
                        }
                    )

                    // Mostrar imágenes si están disponibles
                    if (imagenes.isNotEmpty()) {
                        Text(text = "Imágenes seleccionadas:", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                        LazyRow(modifier = Modifier.padding(horizontal = 20.dp)) {
                            items(imagenes) { media ->
                                Box(modifier = Modifier.padding(4.dp)) {
                                    AsyncImage(
                                        model = media.uri,
                                        contentDescription = "Imagen guardada",
                                        modifier = Modifier
                                            .size(75.dp)
                                            .clickable {
                                                currentUri = Uri.parse(media.uri)
                                                isImageViewerOpen = true
                                            }
                                    )
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar imagen",
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(24.dp)
                                            .clickable {
                                                imagenes.remove(media)
                                                imagenesParaEliminar.add(media)
                                            },
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }

                    if (videos.isNotEmpty()) {
                        Text(text = "Videos seleccionados:", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp))
                        LazyRow(modifier = Modifier.padding(horizontal = 20.dp)) {
                            items(videos) { media ->
                                Box(modifier = Modifier.padding(4.dp)) {
                                    val context = LocalContext.current
                                    var thumbnailBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
                                    LaunchedEffect(media.uri) {
                                        withContext(Dispatchers.IO) {
                                            val retriever = MediaMetadataRetriever()
                                            try {
                                                retriever.setDataSource(context, Uri.parse(media.uri))
                                                val bitmap = retriever.getFrameAtTime(0)
                                                thumbnailBitmap = bitmap?.asImageBitmap()
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            } finally {
                                                retriever.release()
                                            }
                                        }
                                    }
                                    thumbnailBitmap?.let { bitmap ->
                                        Image(
                                            bitmap = bitmap,
                                            contentDescription = "Miniatura del video",
                                            modifier = Modifier
                                                .size(75.dp)
                                                .clickable {
                                                    currentUri = Uri.parse(media.uri)
                                                    isVideoPlayerOpen = true
                                                },
                                            contentScale = ContentScale.Crop
                                        )
                                    } ?: run {
                                        Icon(
                                            imageVector = Icons.Default.Videocam,
                                            contentDescription = "Cargando miniatura...",
                                            modifier = Modifier
                                                .size(75.dp),
                                            tint = Color.Gray
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Eliminar video",
                                        modifier = Modifier
                                            .align(Alignment.TopEnd)
                                            .size(24.dp)
                                            .clickable {
                                                videos.remove(media)
                                                videosParaEliminar.add(media)
                                            },
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            tarea?.let {
                                val updatedTarea = it.copy(titulo = titulo, descripcion = contenido)
                                notaViewModel.updateTarea(updatedTarea)
                                // Eliminar medios marcados para eliminar
                                imagenesParaEliminar.forEach { media ->
                                    notaViewModel.removeMedia(media)
                                }
                                videosParaEliminar.forEach { media ->
                                    notaViewModel.removeMedia(media)
                                }
                                // Agregar nuevos medios a la tarea
                                val tareaId = it.id
                                imagenesParaAgregar.forEach { media ->
                                    notaViewModel.insertMedia(media.copy(tareaId = tareaId))
                                }
                                videosParaAgregar.forEach { media ->
                                    notaViewModel.insertMedia(media.copy(tareaId = tareaId))
                                }
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
                        Text(text = "Editar")
                    }
                }
            }
        }
    }
}


