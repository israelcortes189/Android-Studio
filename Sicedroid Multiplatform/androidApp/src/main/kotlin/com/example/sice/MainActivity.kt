package com.example.sice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.MaterialTheme
import com.example.sice.ui.screens.AndroidSNViewModelFactory
import com.example.sice.ui.screens.SNViewModel
import com.example.sice.ui.theme.MarsPhotosTheme
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val factory: AndroidSNViewModelFactory by lazy {
        // Obtener el MainRepository único desde el Application container
        val appContainer = (application as App).container
        AndroidSNViewModelFactory(repository = appContainer.mainRepository, context = this)
    }

    private val vm: SNViewModel by viewModels { factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                // Reutilizar UI compartida pasando el core compartido
                App(viewModel = vm.sharedCore)
            }
        }
    }
}


