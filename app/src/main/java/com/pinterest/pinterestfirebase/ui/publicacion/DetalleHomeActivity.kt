package com.pinterest.pinterestfirebase.ui.publicacion

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pinterest.pinterestfirebase.R

class DetalleHomeActivity : AppCompatActivity() {

    private lateinit var detalleImagen: ImageView
    private lateinit var detalleNombre: TextView
    private lateinit var detalleDescripcion: TextView
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detalle_home)

        initViews()
        setupData()
        setupListeners()
    }

    private fun initViews() {
        detalleImagen = findViewById(R.id.detalle_imagen)
        detalleNombre = findViewById(R.id.detalle_nombre)
        detalleDescripcion = findViewById(R.id.detalle_descripcion)
        backButton = findViewById(R.id.back_button)
    }

    private fun setupData() {
        // Obtener datos del Intent
        val nombre = intent.getStringExtra("PUBLICACION_NOMBRE") ?: ""
        val descripcion = intent.getStringExtra("PUBLICACION_DESCRIPCION") ?: ""
        val imageBase64 = intent.getStringExtra("PUBLICACION_IMAGE") ?: ""

        // Establecer el nombre
        detalleNombre.text = nombre

        // Establecer la descripción
        detalleDescripcion.text = descripcion

        // Cargar la imagen
        if (imageBase64.isNotEmpty()) {
            try {
                val decodedBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                detalleImagen.setImageBitmap(bitmap)
            } catch (e: Exception) {
                detalleImagen.setImageResource(R.drawable.ic_image_placeholder)
            }
        } else {
            detalleImagen.setImageResource(R.drawable.ic_image_placeholder)
        }
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            finish()
        }
    }
}