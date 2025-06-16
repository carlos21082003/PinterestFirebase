package com.pinterest.pinterestfirebase.ui.comercio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.pinterest.pinterestfirebase.data.repository.AuthRepository

class ProductAddViewModelFactory(
    private val repo: ProductRepository,
    private val authRepo: AuthRepository
): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProductAddViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProductAddViewModel(repo, authRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}