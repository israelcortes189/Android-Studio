package com.example.marsphotos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.marsphotos.ui.LoginScreen
import com.example.marsphotos.ui.HomeScreen
import com.example.marsphotos.ui.screens.SNViewModel

@Composable
fun AppNavHost(
    viewModel: SNViewModel
) {
    val navController = rememberNavController()

    // decidir pantalla inicial
    val startDestination = if (viewModel.hasSession()) {
        "home"
    } else {
        "login"
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("login") {
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(viewModel = viewModel)
        }
    }
}



