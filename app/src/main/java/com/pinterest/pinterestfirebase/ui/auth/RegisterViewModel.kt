package com.pinterest.pinterestfirebase.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinterest.pinterestfirebase.data.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // LiveData para observar el estado del registro
    private val _registerResult = MutableLiveData<Result<Boolean>>()
    val registerResult: LiveData<Result<Boolean>> = _registerResult

    /**
     * Intenta registrar un nuevo usuario con el correo electrónico y la contraseña proporcionados.
     * @param email El correo electrónico del usuario.
     * @param password La contraseña del usuario.
     */
    fun register(email: String, password: String, firstName: String, lastName: String, imageUrl: String) {
        // Lanzamos una corrutina en el ámbito del ViewModel
        viewModelScope.launch {
            val result = authRepository.registerUser(email, password, firstName, lastName,imageUrl )
            _registerResult.postValue(result) // Publica el resultado en el LiveData
        }
    }
}