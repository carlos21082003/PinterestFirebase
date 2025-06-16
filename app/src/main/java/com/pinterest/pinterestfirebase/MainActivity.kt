package com.pinterest.pinterestfirebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pinterest.pinterestfirebase.R.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(layout.activity_main)

        // Aplicar márgenes para sistemas modernos (status bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Obtener el botón por ID y asignar el Intent para ir al foro
        val btnForo = findViewById<Button>(id.btnFORO)
        btnForo.setOnClickListener {
            Toast.makeText(this, "Click detectado", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ForoActivity::class.java)
            startActivity(intent)
        }

    }
}
