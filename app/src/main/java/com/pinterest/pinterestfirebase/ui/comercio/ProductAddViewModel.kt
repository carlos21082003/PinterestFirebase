package com.pinterest.pinterestfirebase.ui.comercio
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.pinterest.pinterestfirebase.data.repository.AuthRepository
class ProductAddViewModel(
    private val repo: ProductRepository,
    private val authRepo: AuthRepository
): ViewModel() {

    private val _result = MutableLiveData<Result<Void?>>()
    val result: LiveData<Result<Void?>> = _result

    fun addProduct(
        name: String,
        price: Double,
        stock: Int,
        detail: String,
        category: String,
        imagesBase64: List<String>
    ) {
        val userId = authRepo.getCurrentUserId()
        if (userId.isNullOrEmpty()) {
            _result.postValue(Result.failure(Exception("Usuario no autenticado")))
            return
        }

        val product = Product(
            ownerId = userId,
            name = name,
            price = price,
            stock = stock,
            detail = detail,
            category = category,
            imagesBase64 = imagesBase64
        )

        repo.addProduct(product)
            .addOnSuccessListener {
                _result.postValue(Result.success(null))
            }
            .addOnFailureListener {
                _result.postValue(Result.failure(it))
            }
    }
}

