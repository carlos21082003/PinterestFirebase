package com.pinterest.pinterestfirebase.ui.comercio

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

class ProductRepository(
    private val firestore: FirebaseFirestore
) {
    private val productsCollection = firestore.collection("products")
    private val db = FirebaseFirestore.getInstance()

    fun addProduct(product: Product): Task<Void> {
        val docRef = db.collection("products").document()
        val productConId = product.copy(id = docRef.id)

        val productMap = hashMapOf(
            "id" to productConId.id,
            "ownerId" to productConId.ownerId,
            "name" to productConId.name,
            "price" to productConId.price,
            "stock" to productConId.stock,
            "detail" to productConId.detail,
            "category" to productConId.category,
            "imagesBase64" to productConId.imagesBase64,
            "createdAt" to Timestamp.now() // 👈 Campo adicional para ordenar
        )

        return docRef.set(productMap)
    }

    fun getProductsForUser(userId: String): Query =
        productsCollection.whereEqualTo("ownerId", userId)

    fun getAllProducts(): Query =
        productsCollection.orderBy("createdAt", Query.Direction.DESCENDING)

    fun getProductById(productId: String): Task<DocumentSnapshot> =
        productsCollection.document(productId).get()
}

