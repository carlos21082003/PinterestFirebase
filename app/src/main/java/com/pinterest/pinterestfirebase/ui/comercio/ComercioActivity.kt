package com.pinterest.pinterestfirebase.ui.comercio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.pinterest.pinterestfirebase.R
import com.pinterest.pinterestfirebase.ui.perfil.ProfileActivity
import com.pinterest.pinterestfirebase.ui.foro.ForoActivity
import com.pinterest.pinterestfirebase.ui.product.ProductAdapter
import com.pinterest.pinterestfirebase.ui.publicacion.ElegirTipoActivity

class ComercioActivity : AppCompatActivity() {

    private lateinit var adapter: ProductAdapter
    private val productos = mutableListOf<Product>()
    private val firestore = FirebaseFirestore.getInstance()

    companion object {
        private const val REQUEST_CODE_ADD_PRODUCT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comercio)

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerProducts)

        adapter = ProductAdapter(productos) { producto ->
            val intent = Intent(this, DetalleComercioActivity::class.java)
            intent.putExtra("producto_id", producto.id)
            startActivity(intent)
        }

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnAddProduct).setOnClickListener {
            val intent = Intent(this, AddProductActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_PRODUCT)
        }

        cargarProductosDesdeFirebase()
        initUI()
    }

    //fun para que el nav funcione
    private fun initUI() {

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigation.selectedItemId = R.id.nav_comercio

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_comercio -> {
                    // Ya estamos en el perfil, no hacer nada más
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, ElegirTipoActivity::class.java))
                    true
                }
                R.id.nav_foro -> {
                    startActivity(Intent(this, ForoActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun cargarProductosDesdeFirebase() {
        firestore.collection("products")
            .get()
            .addOnSuccessListener { result ->
                productos.clear()
                for (document in result) {
                    try {
                        val product = document.toObject(Product::class.java).copy(id = document.id)
                        productos.add(product)
                    } catch (e: Exception) {
                        Log.e("Firestore", "Error al convertir producto", e)
                    }
                }
                Log.d("Firestore", "Productos cargados: ${productos.size}")
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error al obtener productos", e)
            }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_ADD_PRODUCT && resultCode == Activity.RESULT_OK) {
            cargarProductosDesdeFirebase()
        }
    }
}

