package com.example.marsphotos.ui.navigation


sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile")
}
