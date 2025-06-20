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
import com.pinterest.pinterestfirebase.ui.comercio.ComercioActivity
import com.pinterest.pinterestfirebase.ui.foro.ForoActivity
import com.pinterest.pinterestfirebase.ui.perfil.ProfileActivity

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initUI()
    }

    private fun initUI() {
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)


        bottomNavigation.selectedItemId = R.id.nav_home

        val activityMap = mapOf(
            R.id.nav_profile to ProfileActivity::class.java,
            R.id.nav_comercio to ComercioActivity::class.java,
            R.id.nav_foro to ForoActivity::class.java
        )

        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_home) {
                true
            } else {
                activityMap[item.itemId]?.let { activityClass ->
                    startActivity(Intent(this, activityClass))
                    true
                } ?: false
            }
        }
    }
}