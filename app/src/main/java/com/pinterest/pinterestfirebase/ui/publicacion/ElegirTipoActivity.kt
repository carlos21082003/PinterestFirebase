package com.pinterest.pinterestfirebase.ui.publicacion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pinterest.pinterestfirebase.R

class ElegirTipoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_elegir_tipo)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
    }
    private fun initUI() {
        val btnHome = findViewById<Button>(R.id.btnPNormal)
        val btnIrPerfil = findViewById<Button>(R.id.btnIrPerfil)

//        btnHome.setOnClickListener {
//            irAgregar()
//        }
//        btnIrPerfil.setOnClickListener {
//            irPerfil()
//        }

    }

//    private fun irAgregar() {
//        startActivity(Intent(this, Agregar::class.java))
//    }

//    private fun irPerfil() {
//        startActivity(Intent(this, ProfileActivity::class.java))
//    }
}