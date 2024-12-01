package com.example.proyecto

import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.proyecto.Componentes.MenuLateral
import com.example.proyecto.Componentes.TopBar
import com.example.proyecto.Models.Media
import com.example.proyecto.Models.Tarea
import com.example.proyecto.screens.NotasViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

val verdeAzul = Color(0xFFE0E99D)

@Composable
fun Tareas(navController: NavHostController, notaViewModel: NotasViewModel) {
    val tareas = notaViewModel.listaTareas.collectAsState().value

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    MenuLateral(navController = navController, drawerState = drawerState) {
        Scaffold(
            topBar = { TopBar(drawerState) },
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.regresar_Tareas),
                            modifier = Modifier
                                .size(35.dp)
                                .padding(end = 8.dp)
                                .clickable { navController.popBackStack() },
                            tint = Color.Gray,
                        )
                        texto(stringResource(R.string.tareas1), Modifier.weight(1f))
                    }
                    cuadroDeBusqueda()
                    Button(
                        onClick = { navController.navigate(route = Rutas.AgregarTareas.ruta) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1567A6),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = stringResource(R.string.agregar_tarea_Tarea))
                    }
                }

                items(tareas) { tarea ->
                    cuadroDeTareas(tarea, notaViewModel = notaViewModel, navController)
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
fun texto(name: String, modifier: Modifier = Modifier) {
    Text(
        text = name,
        modifier = modifier.wrapContentWidth(Alignment.CenterHorizontally),
        fontSize = 45.sp,
        fontWeight = FontWeight.Bold
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun cuadroDeBusqueda() {
    var buscar by rememberSaveable { mutableStateOf("") }
    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = buscar,
            onValueChange = { buscar = it },
            placeholder = { Text(stringResource(R.string.buscar_Tareas)) },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Blue,
                unfocusedIndicatorColor = Color.Gray
            )
        )
        Spacer(modifier = Modifier.height(25.dp))
    }
}

@Composable
fun listaTareas(tareas: List<Tarea>, notaViewModel: NotasViewModel, navController: NavHostController) {
    Column {
        for (tarea in tareas) {
            cuadroDeTareas(tarea, notaViewModel =  notaViewModel, navController)
            Spacer(modifier = Modifier.height(30.dp))
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}

@Composable
fun cuadroDeTareas(tarea: Tarea, notaViewModel: NotasViewModel, navController: NavHostController) {
    val imagenes = remember { mutableStateListOf<Media>() }
    val videos = remember { mutableStateListOf<Media>() }
    var isImageViewerOpen by remember { mutableStateOf(false) }
    var isVideoPlayerOpen by remember { mutableStateOf(false) }
    var currentUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(tarea.id) {
        val mediaList = notaViewModel.getMediaByTareaId(tarea.id)
        imagenes.clear()
        videos.clear()
        imagenes.addAll(mediaList.filter { it.tipo == "imagen" })
        videos.addAll(mediaList.filter { it.tipo == "video" })
    }

    if (isImageViewerOpen && currentUri != null) {
        FullScreenImageViewer(imageUri = currentUri!!, onDismiss = { isImageViewerOpen = false })
    }

    if (isVideoPlayerOpen && currentUri != null) {
        FullScreenVideoPlayer(videoUri = currentUri!!, onDismiss = { isVideoPlayerOpen = false })
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = verdeAzul)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.editar_nota),
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            val id = tarea.id
                            navController.navigate("RutaEditarTarea/$id")
                        },
                    tint = Color.Gray
                )
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.eliminar_tarea),
                    modifier = Modifier
                        .size(35.dp)
                        .clickable {
                            notaViewModel.removeTarea(tarea)
                        },
                    tint = Color.Red
                )
            }

            Text(
                text = tarea.titulo,
                modifier = Modifier.padding(bottom = 8.dp),
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Text(
                text = tarea.descripcion,
                modifier = Modifier.padding(bottom = 8.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )

            if (imagenes.isNotEmpty()) {
                Text(text = "Imágenes seleccionadas:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                    items(imagenes) { media ->
                        AsyncImage(
                            model = media.uri,
                            contentDescription = "Imagen guardada",
                            modifier = Modifier
                                .size(75.dp)
                                .padding(4.dp)
                                .clickable {
                                    currentUri = Uri.parse(media.uri)
                                    isImageViewerOpen = true
                                }
                        )
                    }
                }
            }

            if (videos.isNotEmpty()) {
                Text(text = "Videos seleccionados:", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                LazyRow(modifier = Modifier.padding(vertical = 8.dp)) {
                    items(videos) { media ->
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
                                    .padding(8.dp)
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
                                    .size(75.dp)
                                    .padding(8.dp),
                                tint = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}