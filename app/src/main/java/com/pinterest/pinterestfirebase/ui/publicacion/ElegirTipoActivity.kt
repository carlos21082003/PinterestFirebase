package com.pinterest.pinterestfirebase.ui.publicacion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pinterest.pinterestfirebase.R
import com.pinterest.pinterestfirebase.ui.perfil.ProfileActivity
import com.pinterest.pinterestfirebase.ui.comercio.AddProductActivity
import com.pinterest.pinterestfirebase.ui.comercio.ComercioActivity
import com.pinterest.pinterestfirebase.ui.foro.CuidarActivity


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
        val btnSubirN = findViewById<Button>(R.id.btnPNormal)
        val btnAgregar = findViewById<Button>(R.id.btnAgregar)
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        btnSubirN.setOnClickListener {
            irAgregar()
        }

        btnAgregar.setOnClickListener {
            irAgregarComercio()
        }

        bottomNavigation.selectedItemId = R.id.nav_add

        val activityMap = mapOf(
            R.id.nav_profile to ProfileActivity::class.java,
            R.id.nav_comercio to ComercioActivity::class.java,
            R.id.nav_foro to CuidarActivity::class.java,
            R.id.nav_home to HomeActivity::class.java
        )

        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_add) {
                true
            } else {
                activityMap[item.itemId]?.let { activityClass ->
                    startActivity(Intent(this, activityClass))
                    true
                } ?: false
            }
        }
    }

    private fun irAgregar() {
        startActivity(Intent(this, PubliNAddEditActivity::class.java))
    }

    private fun irAgregarComercio() {
        startActivity(Intent(this, AddProductActivity::class.java))
    }

}