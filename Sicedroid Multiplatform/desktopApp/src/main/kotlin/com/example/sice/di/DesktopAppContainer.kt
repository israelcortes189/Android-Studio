package com.example.sice.di

import com.example.sice.data.LocalRepository
import com.example.sice.data.MainRepository
import com.example.sice.data.MarsPhotosRepository
import com.example.sice.data.SNRepository
import com.example.sice.di.AppContainer
import com.example.sice.repository.LocalRepositoryJvm
import com.example.sice.repository.SNRepositoryKtor
import java.io.File

class DesktopAppContainer(
    // parámetros de constructor con nombres distintos para evitar shadowing
    storageDirParam: File? = null,
    baseUrlParam: String? = null,
    // opcionalmente permitir inyección de implementaciones (útil para tests)
    localRepositoryParam: LocalRepository? = null,
    remoteRepositoryParam: SNRepository? = null,
    mainRepositoryParam: MainRepository? = null
) : AppContainer {

    private val storage: File = (storageDirParam ?: File(System.getProperty("user.home"), ".sice_desktop")).also {
        if (!it.exists()) it.mkdirs()
    }

    // override las propiedades definidas en AppContainer
    override val snRepository: SNRepository by lazy {
        remoteRepositoryParam ?: SNRepositoryKtor(
            baseUrl = baseUrlParam ?: "https://sicenet.surguanajuato.tecnm.mx/ws/wsalumnos.asmx"
        )
    }

    override val localRepository: LocalRepository by lazy {
        localRepositoryParam ?: LocalRepositoryJvm(storage)
    }

    override val mainRepository: MainRepository by lazy {
        mainRepositoryParam ?: MainRepository(local = localRepository, remote = snRepository)
    }

    init {
        println("DEBUG DesktopAppContainer created: local=${System.identityHashCode(localRepository)} remote=${System.identityHashCode(snRepository)} main=${System.identityHashCode(mainRepository)}")
    }
}



