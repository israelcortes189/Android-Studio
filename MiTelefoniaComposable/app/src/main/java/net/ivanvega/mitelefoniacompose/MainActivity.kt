package net.ivanvega.mitelefoniacompose
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.ivanvega.mitelefoniacompose.ui.theme.MiTelefoniaComposeTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ScreenViewModel by viewModels()

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            val sendSmsGranted = perms[Manifest.permission.SEND_SMS] == true
            val readPhoneStateGranted = perms[Manifest.permission.READ_PHONE_STATE] == true
            val receiveSmsGranted = perms[Manifest.permission.RECEIVE_SMS] == true

            // No llamar viewModel.toggleEnabled() aquí
            if (sendSmsGranted && readPhoneStateGranted) {
                if (viewModel.enabled) {
                    // Activity inicia el servicio si el usuario ya había activado la función
                    val intent = Intent(this, CallListenService::class.java)
                    ContextCompat.startForegroundService(this, intent)
                }
            } else {
                viewModel.clearEvent()
            }
        }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestAllNeededPermissionsIfMissing()

        setContent {
            MiTelefoniaComposeTheme {
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                // Observa eventos del ViewModel y muestra Snackbar o lanza permisos
                val events by viewModel.events.collectAsState()

                LaunchedEffect(events) {
                    when (val ev = events) {
                        is ScreenViewModel.Event.RequestPermissions -> {
                            requestAllNeededPermissionsIfMissing()
                            viewModel.clearEvent()
                        }
                        is ScreenViewModel.Event.Error -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(ev.message)
                            }
                            viewModel.clearEvent()
                        }
                        is ScreenViewModel.Event.Info -> {
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar(ev.message)
                            }
                            viewModel.clearEvent()
                        }
                        null -> {}
                        ScreenViewModel.Event.StartService -> TODO()
                        ScreenViewModel.Event.StopService -> TODO()
                    }
                }

                Scaffold(
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                    topBar = { SmallTopAppBar(title = { Text("Mi Telefonía") }) }
                ) { padding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                                .padding(16.dp)
                        ) {
                            Spacer(modifier = Modifier.height(24.dp))

                            // Ajustes de respuesta automática
                            AutoReplySettings(viewModel = viewModel)
                        }
                    }
            }
        }

        // Observador de eventos que inicia/detiene el servicio desde la Activity
        lifecycleScope.launchWhenStarted {
            viewModel.events.collect { ev ->
                when (ev) {
                    is ScreenViewModel.Event.RequestPermissions -> {
                        requestAllNeededPermissionsIfMissing()
                        viewModel.clearEvent()
                    }
                    is ScreenViewModel.Event.StartService -> {
                        if (ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                            val intent = Intent(this@MainActivity, CallListenService::class.java)
                            ContextCompat.startForegroundService(this@MainActivity, intent)
                        } else {
                            requestAllNeededPermissionsIfMissing()
                        }
                        viewModel.clearEvent()
                    }
                    is ScreenViewModel.Event.StopService -> {
                        stopService(Intent(this@MainActivity, CallListenService::class.java))
                        viewModel.clearEvent()
                    }
                    is ScreenViewModel.Event.Error -> {
                        // Muestra Snackbar/Toast desde aquí o via Compose
                        viewModel.clearEvent()
                    }
                    is ScreenViewModel.Event.Info -> {
                        viewModel.clearEvent()
                    }
                    null -> {}
                }
            }
        }
    }


    private fun requestAllNeededPermissionsIfMissing() {
        val needed = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.SEND_SMS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.RECEIVE_SMS)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            needed.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (needed.isNotEmpty()) {
            requestPermissionsLauncher.launch(needed.toTypedArray())
        }
    }
}

@Composable
fun AutoReplySettings(viewModel: ScreenViewModel) {
    // Usamos estados directos del ViewModel (mutableStateOf)
    val number = viewModel.phoneNumber
    val message = viewModel.message
    val enabled = viewModel.enabled

    Column {
        Text(text = "Respuesta automática", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = number,
            onValueChange = { viewModel.onPhoneNumberChange(it) },
            label = { Text("Número objetivo (ej. +521...)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { viewModel.onMessageChange(it) },
            label = { Text("Mensaje automático") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 100.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = {
                // Guarda y activa/desactiva la función
                viewModel.saveSettings()
                viewModel.toggleEnabled()
            }) {
                Text(if (enabled) "Desactivar" else "Activar")
            }

            Button(onClick = { viewModel.sendSMSManual() }) {
                Text("Probar envío")
            }
        }
    }
}




