package com.example.sice.ui.screens

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sice.App
import com.example.sice.data.MainRepository

class AndroidSNViewModelFactory(
    private val repository: MainRepository,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SNViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SNViewModel(repository, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
