package com.example.sice.di

import com.example.sice.data.LocalRepository
import com.example.sice.data.MainRepository
import com.example.sice.data.MarsPhotosRepository
import com.example.sice.data.SNRepository

interface AppContainer {
    val snRepository: SNRepository
    val localRepository: LocalRepository
    val mainRepository: MainRepository
}