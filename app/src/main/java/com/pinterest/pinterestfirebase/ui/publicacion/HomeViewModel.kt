package com.pinterest.pinterestfirebase.ui.publicacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pinterest.pinterestfirebase.data.model.PublicacionN
import com.pinterest.pinterestfirebase.data.repository.PubliNRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val publiNRepository: PubliNRepository) : ViewModel() {

    private val _publicaciones = MutableStateFlow<Result<List<PublicacionN>>>(Result.success(emptyList()))
    val publicaciones: StateFlow<Result<List<PublicacionN>>> = _publicaciones

    init {
        viewModelScope.launch {
            publiNRepository.getAllPublicaciones().collect { result ->
                _publicaciones.value = result
            }
        }
    }
}

class HomeViewModelFactory(private val publiNRepository: PubliNRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(publiNRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}