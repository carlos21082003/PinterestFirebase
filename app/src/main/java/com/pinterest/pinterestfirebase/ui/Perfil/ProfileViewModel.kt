package com.pinterest.pinterestfirebase.ui.Perfil

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.pinterest.pinterestfirebase.data.model.Usuarios
import com.pinterest.pinterestfirebase.data.repository.UserRepository

// ProfileViewModel.kt
class ProfileViewModel() : ViewModel() {

    private val repository = UserRepository()

    fun agregarMascota(mascota: Usuarios, imagenUri: Uri?, onResult: (Boolean) -> Unit) {
//        repository.agregarLibro(mascota, imagenUri, onResult)
    }
}