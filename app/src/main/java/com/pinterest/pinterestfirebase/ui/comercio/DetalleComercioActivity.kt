package com.pinterest.pinterestfirebase.ui.comercio

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.firestore.FirebaseFirestore
import com.pinterest.pinterestfirebase.R

class DetalleComercioActivity : AppCompatActivity() {

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detallecomercio)

        val productId = intent.getStringExtra("producto_id")

        if (productId.isNullOrEmpty()) {
            Log.e("DetalleComercio", "ID de producto no recibido")
            finish()
            return
        }

        cargarProductoDesdeFirebase(productId)

        findViewById<ImageButton>(R.id.back_button2).setOnClickListener {
            finish()
        }
    }

    private fun cargarProductoDesdeFirebase(productId: String) {
        firestore.collection("products").document(productId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val product = document.toObject(Product::class.java)
                    if (product != null) {
                        mostrarDetalles(product)
                    }
                } else {
                    Log.e("DetalleComercio", "Producto no encontrado en Firestore")
                    finish()
                }
            }
            .addOnFailureListener {
                Log.e("DetalleComercio", "Error al obtener producto", it)
                finish()
            }
    }

    private fun mostrarDetalles(product: Product) {
        findViewById<TextView>(R.id.product_name).text = product.name
        findViewById<TextView>(R.id.product_price2).text = "S/ ${product.price}"
        findViewById<TextView>(R.id.product_stock2).text = "${product.stock}"
        findViewById<TextView>(R.id.product_description).text = product.detail

        val viewPager = findViewById<ViewPager2>(R.id.product_image_slider)
        viewPager.adapter = ImageSliderAdapter(product.imagesBase64)
    }
}



