package com.example.miappmultiplatform

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform