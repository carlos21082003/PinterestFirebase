package com.pinterest.pinterestfirebase.ui.publicacion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.firestore.FirebaseFirestore
import com.pinterest.pinterestfirebase.R
import com.pinterest.pinterestfirebase.data.repository.PubliNRepository
import com.pinterest.pinterestfirebase.databinding.ActivityHomeBinding
import com.pinterest.pinterestfirebase.ui.comercio.ComercioActivity
import com.pinterest.pinterestfirebase.ui.foro.CuidarActivity
import com.pinterest.pinterestfirebase.ui.perfil.ProfileActivity
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: PublicacionNHomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val publiNRepository = PubliNRepository(FirebaseFirestore.getInstance())

        viewModel = ViewModelProvider(this, HomeViewModelFactory(publiNRepository))
            .get(HomeViewModel::class.java)

        adapter = PublicacionNHomeAdapter()

        binding.recyclerPublicacionesHome.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        binding.recyclerPublicacionesHome.adapter = adapter

        lifecycleScope.launch {
            viewModel.publicaciones.collect { result ->
                result.onSuccess { list ->
                    adapter.submitList(list)
                }.onFailure {
                    Toast.makeText(this@HomeActivity, "Error al cargar publicaciones", Toast.LENGTH_SHORT).show()
                }
            }
        }

        initUI()
    }

    private fun initUI() {
        val bottomNavigation = binding.bottomNavigation

        bottomNavigation.selectedItemId = R.id.nav_home

        val activityMap = mapOf(
            R.id.nav_profile to ProfileActivity::class.java,
            R.id.nav_comercio to ComercioActivity::class.java,
            R.id.nav_foro to CuidarActivity::class.java
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
