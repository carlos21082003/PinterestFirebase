package com.pinterest.pinterestfirebase.data.repository

import android.content.ContentResolver
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayInputStream
import kotlin.io.readBytes
import kotlin.text.isNullOrEmpty

object ImagenManager {
    fun convertirImagenABase64(contentResolver: ContentResolver, uri: Uri?): String? {
        return try {
            if (uri == null) return null
            val inputStream = contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) {
                Base64.encodeToString(bytes, Base64.NO_WRAP)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun cargarImagenDesdeBase64(base64: String?, imageView: ImageView) {
        try {
            if (base64.isNullOrEmpty()) return
            val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
            val inputStream = ByteArrayInputStream(decodedBytes)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}