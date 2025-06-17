package com.pinterest.pinterestfirebase.data.repository


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pinterest.pinterestfirebase.data.model.Usuarios
import kotlinx.coroutines.tasks.await

class UserRepository() {

    suspend fun actualizarCuenta(email: String, firstName: String, lastName: String, imageUrl: String) {
        val user = FirebaseAuth.getInstance().currentUser
            ?: throw Exception("No hay usuario autenticado")

        val uid = user.uid

        // (Opcional) Si también quieres actualizar el email
        if (user.email != email) {
            user.updateEmail(email).await()
        }

        // Actualiza también Firestore
        val userData = mapOf(
            "firstName" to firstName,
            "lastName" to lastName,
            "imagenUrl" to imageUrl,
        )

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update(userData)  // usa `.update()` para modificar campos existentes
            .await()

        Log.d("ActualizarCuenta", "Perfil y Firestore actualizados correctamente")
    }

    fun obtenerUsuarioActual(onResult: (Usuarios?) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val uid = user?.uid

        if (uid == null) {
            onResult(null)
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val usuario = document.toObject(Usuarios::class.java)
                    usuario?.email = user.email ?: ""

                    onResult(usuario)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
                onResult(null)
            }
    }

}