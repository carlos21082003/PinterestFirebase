package com.pinterest.pinterestfirebase.ui.foro

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
import com.pinterest.pinterestfirebase.ui.comercio.ComercioActivity
import com.pinterest.pinterestfirebase.ui.publicacion.ElegirTipoActivity
import com.pinterest.pinterestfirebase.ui.publicacion.HomeActivity

class CuidarActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cuidar)
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

        val activityMap = mapOf(
            R.id.nav_profile to ProfileActivity::class.java,
            R.id.nav_comercio to ComercioActivity::class.java,
            R.id.nav_add to ElegirTipoActivity::class.java,
            R.id.nav_home to HomeActivity::class.java
        )

        bottomNavigation.setOnItemSelectedListener { item ->
            if (item.itemId == R.id.nav_foro) {
                true
            } else {
                activityMap[item.itemId]?.let { activityClass ->
                    startActivity(Intent(this, activityClass))
                    true
                } ?: false
            }
        }
        val btnContinuar = findViewById<Button>(R.id.btnContinuaru)
        btnContinuar.setOnClickListener {
            val intent = Intent(this, ReutilizarActivity::class.java)
            startActivity(intent)
        }
    }
}