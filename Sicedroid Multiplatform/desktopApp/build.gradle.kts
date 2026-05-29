import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization") version "1.9.0"

}

dependencies {
    implementation(projects.shared)

    implementation(compose.desktop.currentOs)
    implementation(libs.kotlinx.coroutinesSwing)

    implementation(libs.compose.uiToolingPreview)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    val ktorVersion = "2.3.12"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-cio:$ktorVersion") // El motor CIO que usas en el código
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")

    implementation("org.json:json:20230227")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

}

compose.desktop {
    application {
        mainClass = "com.example.sice.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Sice"                // nombre amigable del paquete
            packageVersion = "1.0.0"
            description = "Sice Desktop"
            vendor = "TuEmpresa"
            // iconFile.set(project.file("src/main/resources/icon.ico"))
        }
    }
}