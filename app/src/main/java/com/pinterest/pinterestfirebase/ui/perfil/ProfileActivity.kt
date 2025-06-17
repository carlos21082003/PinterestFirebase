package com.pinterest.pinterestfirebase.ui.perfil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.pinterest.pinterestfirebase.R
import com.pinterest.pinterestfirebase.data.repository.ImagenManager
import com.pinterest.pinterestfirebase.data.repository.UserRepository
import com.pinterest.pinterestfirebase.ui.comercio.ComercioActivity
import com.pinterest.pinterestfirebase.ui.foro.ForoActivity
import com.pinterest.pinterestfirebase.ui.publicacion.ElegirTipoActivity
import com.pinterest.pinterestfirebase.ui.publicacion.PubliNListActivity
import kotlinx.coroutines.launch


class ProfileActivity: AppCompatActivity() {

    private lateinit var edtNombre: EditText
    private lateinit var edtLastname: EditText
    private lateinit var edtEmail: EditText
    private lateinit var imgPreview: ImageView
    private lateinit var btnPublicaciones: Button
    private lateinit var btnProductos: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnNewImage: Button

    private var imagenUrlActual: Uri? = null
    private var imagenOriginal: String? = null

    private var nombreOriginal = ""
    private var apellidoOriginal = ""
    private var emailOriginal = ""

    private val seleccionarImagenLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imagenUrlActual = uri
            imgPreview.setImageURI(uri)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val userRepository = UserRepository()

        edtNombre = findViewById(R.id.et_nombre)
        edtLastname = findViewById(R.id.et_apellido)
        edtEmail = findViewById(R.id.et_email)
        imgPreview = findViewById(R.id.profile_image)
        btnPublicaciones = findViewById(R.id.toolbar)
        btnGuardar = findViewById(R.id.btn_editar)
        btnNewImage = findViewById(R.id.btn_new_profile)


        btnGuardar.visibility = Button.GONE

        userRepository.obtenerUsuarioActual { usuario ->
            if (usuario != null) {
                edtNombre.setText(usuario.firstName)
                edtLastname.setText(usuario.lastName)
                edtEmail.setText(usuario.email)

                // Guardamos valores originales
                nombreOriginal = usuario.firstName ?: ""
                apellidoOriginal = usuario.lastName ?: ""
                emailOriginal = usuario.email ?: ""
                imagenOriginal = usuario.imagenUrl
                imagenUrlActual = Uri.parse(usuario.imagenUrl)

                ImagenManager.cargarImagenDesdeBase64(usuario.imagenUrl!!, imgPreview)
            } else {
                Toast.makeText(this, "No se pudo cargar el perfil del usuario", Toast.LENGTH_SHORT).show()
            }

            setupChangeListeners()
        }


        btnPublicaciones.setOnClickListener {
            val intent = Intent(this, PubliNListActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnNewImage.setOnClickListener {
            seleccionarImagenLauncher.launch("image/*")
        }

        btnGuardar.setOnClickListener {
            lifecycleScope.launch {
                val nuevoNombre = edtNombre.text.toString()
                val nuevoApellido = edtLastname.text.toString()
                val nuevoEmail = edtEmail.text.toString()

                var rutaImagen: String? = imagenOriginal

                if (imagenUrlActual.toString() != imagenOriginal) {
                    rutaImagen = ImagenManager.convertirImagenABase64(contentResolver, imagenUrlActual!!)
                }

                userRepository.actualizarCuenta(
                    email = nuevoEmail,
                    firstName = nuevoNombre,
                    lastName = nuevoApellido,
                    imageUrl = rutaImagen ?: ""
                )

                nombreOriginal = nuevoNombre
                apellidoOriginal = nuevoApellido
                emailOriginal = nuevoEmail
                imagenOriginal = rutaImagen

                btnGuardar.visibility = Button.GONE
                Toast.makeText(this@ProfileActivity, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
            }
        }

        initUI()
    }

    //fun para que el nav funcione
    private fun initUI() {

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNavigation.selectedItemId = R.id.nav_profile

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_profile -> {
                    // Ya estamos en el perfil, no hacer nada más
                    true
                }
                R.id.nav_add -> {
                    startActivity(Intent(this, ElegirTipoActivity::class.java))
                    true
                }
                R.id.nav_comercio -> {
                    startActivity(Intent(this, ComercioActivity::class.java))
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

    private fun setupChangeListeners() {
        val checkCambios: () -> Unit = {
            val nombreCambiado = edtNombre.text.toString() != nombreOriginal
            val apellidoCambiado = edtLastname.text.toString() != apellidoOriginal
            val emailCambiado = edtEmail.text.toString() != emailOriginal
            val imagenCambiada = imagenUrlActual.toString() != imagenOriginal

            btnGuardar.visibility = if (nombreCambiado || apellidoCambiado || emailCambiado || imagenCambiada) {
                Button.VISIBLE
            } else {
                Button.GONE
            }
        }

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkCambios()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        edtNombre.addTextChangedListener(watcher)
        edtLastname.addTextChangedListener(watcher)
        edtEmail.addTextChangedListener(watcher)

        // Si cambias la imagen manualmente,llama a checkCambios()
        checkCambios()
    }
}