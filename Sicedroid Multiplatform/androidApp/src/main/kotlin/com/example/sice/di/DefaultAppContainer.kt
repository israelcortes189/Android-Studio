package com.example.sice.di

import com.example.sice.AddCookiesInterceptor
import android.content.Context
import com.example.sice.data.LocalRepository
import com.example.sice.Network.MarsApiService
import com.example.sice.Network.SICENETWService
import com.example.sice.ReceivedCookiesInterceptor
import com.example.sice.data.MainRepository
import com.example.sice.data.MarsPhotosRepository
import com.example.sice.data.NetworSNRepository
import com.example.sice.data.RoomLocalRepository
import com.example.sice.data.SNRepository
import com.example.sice.data.datbase.AppDatabase
import kotlin.getValue
import kotlin.jvm.java
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory


class DefaultAppContainer(private val applicationContext: Context) : AppContainer {

    // Ejemplo: base URLs
    private val baseUrl = "https://android-kotlin-fun-mars-server.appspot.com/"
    private val baseUrlSN = "https://sicenet.surguanajuato.tecnm.mx"

    // Cliente OkHttp con interceptores de cookies
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(AddCookiesInterceptor(applicationContext))     // Añade cookies guardadas
        .addInterceptor(ReceivedCookiesInterceptor(applicationContext)) // Recibe y guarda cookies nuevas
        .build()

    // Retrofit para el servicio de ejemplo (Mars)
    private val retrofit: Retrofit = Retrofit.Builder()
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .baseUrl(baseUrl)
        .build()

    // Retrofit para SICENET con soporte SOAP/XML
    private val retrofitSN: Retrofit = Retrofit.Builder()
        .baseUrl(baseUrlSN)
        .addConverterFactory(SimpleXmlConverterFactory.createNonStrict())
        .client(client) // Usa el cliente con interceptores
        .build()

    // Servicio Retrofit para SICENET
    private val retrofitServiceSN: SICENETWService by lazy {
        retrofitSN.create(SICENETWService::class.java)
    }

    override val snRepository: SNRepository by lazy {
        NetworSNRepository(retrofitServiceSN, applicationContext)
    }

    // Base de datos Android (Room) — Android-only
    private val database: AppDatabase by lazy {
        AppDatabase.getDatabase(applicationContext)
    }

    // Repositorio local que implementa la interfaz LocalRepository (mapea entidades Room <-> DTOs)
    override val localRepository: LocalRepository by lazy {
        // Asegúrate de que AppDatabase tenga estos métodos exactos
        RoomLocalRepository(
            profileDao = database.profileDao(),
            cardexDao = database.cardexDao(),
            cargaDao = database.cargaDao(),
            calificacionUnidadDao = database.CalificacionUnidadDao(),
            calificacionFinalDao = database.CalificacionFinalDao()
        )
    }

    // Repositorio principal (shared) que orquesta local + remoto
    override val mainRepository: MainRepository by lazy {
        MainRepository(localRepository, snRepository)
    }
}