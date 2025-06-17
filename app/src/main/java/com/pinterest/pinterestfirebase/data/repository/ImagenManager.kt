package com.pinterest.pinterestfirebase.data.repository

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapShader
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Shader
import android.net.Uri
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayInputStream

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

            val circularBitmap = getCircularBitmap(bitmap)
            imageView.setImageBitmap(circularBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
        val size = minOf(bitmap.width, bitmap.height)
        val x = (bitmap.width - size) / 2
        val y = (bitmap.height - size) / 2

        val squaredBitmap = Bitmap.createBitmap(bitmap, x, y, size, size)
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
            shader = BitmapShader(squaredBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
        return output
    }
}