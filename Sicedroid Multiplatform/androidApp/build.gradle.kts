import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlin.ksp) // Asegúrate que en toml sea la versión compatible con tu Kotlin
}

dependencies {
    implementation(projects.shared)

    implementation(libs.androidx.activity.compose)
    implementation(libs.compose.uiToolingPreview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.compose.uiTooling)

    // --- SECCIÓN DE ROOM (Sin comillas y usando el catálogo) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // --- RESTO DE LIBRERÍAS ---
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:4.11.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("io.coil-kt:coil-compose:2.4.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
    implementation("com.squareup.retrofit2:converter-simplexml:2.9.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    implementation("androidx.compose.material:material:1.4.3")
    implementation("androidx.compose.material:material-icons-extended:1.4.3")

    implementation("com.google.code.gson:gson:2.10.1")
}

android {
    namespace = "com.example.sice"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.example.sice"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    signingConfigs {
        create("release") {
            storeFile = file("keystores/release-keystore.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = "sice_release"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }
    buildTypes {
        release {
            //signingConfig = signingConfigs["release"]
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

}