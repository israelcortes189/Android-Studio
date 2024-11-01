package com.example.proyecto.di

import android.content.Context
import androidx.room.Room
import com.example.proyecto.data.NotasDataBaseDao
import com.example.proyecto.data.NotasDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): NotasDatabase =
        Room.databaseBuilder(
            context,
            NotasDatabase::class.java,
            "notas"
        ).fallbackToDestructiveMigration().build()

    @Singleton
    @Provides
    fun provideNotasDao(notasDataBase: NotasDatabase): NotasDataBaseDao =
        notasDataBase.notaDao()
}
