package com.pinterest.pinterestfirebase.ui.comercio
import com.google.firebase.firestore.DocumentId
import java.io.Serializable
data class Product(
    @DocumentId var id: String = "",
    val ownerId: String = "",
    val name: String = "",
    val price: Double = 0.0,
    val stock: Int = 0,
    val detail: String = "",
    val category: String = "",
    val imagesBase64: List<String> = listOf()
): Serializable