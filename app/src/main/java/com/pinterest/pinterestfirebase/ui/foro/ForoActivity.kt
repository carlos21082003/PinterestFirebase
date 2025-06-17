package com.pinterest.pinterestfirebase.ui.foro

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pinterest.pinterestfirebase.R
import com.pinterest.pinterestfirebase.ui.perfil.ProfileActivity
import com.pinterest.pinterestfirebase.ui.comercio.ComercioActivity
import com.pinterest.pinterestfirebase.ui.publicacion.ElegirTipoActivity

class ForoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_foro)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initUI()
    }

    private fun initUI() {

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigation.selectedItemId = R.id.nav_foro

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_foro -> {
                    // Ya estamos en ElegirTipo, no hacer nada más
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.nav_comercio -> {
                    startActivity(Intent(this, ComercioActivity::class.java))
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, ElegirTipoActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}