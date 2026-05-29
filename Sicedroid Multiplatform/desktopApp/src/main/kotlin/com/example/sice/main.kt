package com.example.sice

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.runtime.*
import com.example.sice.data.LocalRepository
import com.example.sice.data.MainRepository
import com.example.sice.data.SNRepository
import com.example.sice.di.DesktopAppContainer
import com.example.sice.di.DesktopRoot
import com.example.sice.repository.LocalRepositoryJvm
import com.example.sice.repository.SNRepositoryKtor
import com.example.sice.viewModel.SNViewModelDesktop
import java.io.File
import kotlin.system.exitProcess


fun main() = application {
    // crear singletons aquí
    val localRepo: LocalRepository = LocalRepositoryJvm()
    val remoteRepo: SNRepository = SNRepositoryKtor(baseUrl = "https://sicenet.surguanajuato.tecnm.mx/ws/wsalumnos.asmx")
    val mainRepo = MainRepository(local = localRepo, remote = remoteRepo)

    println("DEBUG main: local=${System.identityHashCode(localRepo)} main.local=${System.identityHashCode(mainRepo.local)}")

    // pasar las instancias al contenedor (no crear nuevas dentro del contenedor)
    val container = DesktopAppContainer(
        storageDirParam = null,
        baseUrlParam = null,
        localRepositoryParam = localRepo,
        remoteRepositoryParam = remoteRepo,
        mainRepositoryParam = mainRepo
    )

    val vm = SNViewModelDesktop(container.mainRepository)

    Window(onCloseRequest = {
        try { vm.shutdown() } catch (_: Throwable) {}
        try { exitProcess(0) } catch (_: Throwable) {}
    }, title = "Sice Desktop") {
        DesktopRoot(vm)
    }
}
