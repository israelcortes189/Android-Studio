package com.example.sice.di

import androidx.compose.runtime.*
import com.example.sice.App
import com.example.sice.viewModel.SNViewModelDesktop

@Composable
fun DesktopRoot(viewModel: SNViewModelDesktop) {
    LaunchedEffect(Unit) {
        try { viewModel.start() } catch (t: Throwable) { t.printStackTrace() }
    }
    App(viewModel)
}