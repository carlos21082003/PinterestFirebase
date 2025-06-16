package com.pinterest.pinterestfirebase.ui.auth

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pinterest.pinterestfirebase.data.repository.AuthRepository
import com.pinterest.pinterestfirebase.databinding.ActivityRegisterBinding
import com.pinterest.pinterestfirebase.ui.publicacion.PubliNListActivity
import com.pinterest.pinterestfirebase.data.repository.ImagenManager
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var imgPreview: ImageView
    private var imagenUri: Uri? = null

    private val seleccionarImagenLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imagenUri = uri
            imgPreview.setImageURI(uri)
        }
    }

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        imgPreview = binding.imagePreview

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa Firebase Auth y Firestore
        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        // Inicializa el repositorio de autenticación
        val authRepository = AuthRepository(firebaseAuth, firestore)

        // Inicializa el ViewModel con una Factory
        registerViewModel = ViewModelProvider(this, RegisterViewModelFactory(authRepository))
            .get(RegisterViewModel::class.java)

        // Observa el resultado del intento de registro
        registerViewModel.registerResult.observe(this) { result ->
            result.onSuccess {
                // Si el registro es exitoso, redirige a la PetListActivity
                Toast.makeText(this, "Registro exitoso. ¡Bienvenido!", Toast.LENGTH_SHORT).show()
                navigateToPetList()
            }.onFailure { exception ->
                // Si el registro falla, muestra un mensaje de error
                Toast.makeText(this, "Error al registrar: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }

        binding.btnSelectImage.setOnClickListener {
            seleccionarImagenLauncher.launch("image/*")
        }


        // Configura el listener para el botón de registro  setOnClickListener

        binding.btnRegister.setOnClickListener {
            lifecycleScope.launch {
                val email = binding.etEmailRegister.text.toString().trim()
                val password = binding.etPasswordRegister.text.toString().trim()
                val confirmPassword = binding.etConfirmPasswordRegister.text.toString().trim()
                val firstName = binding.etFirstName.text.toString().trim()
                val lastName = binding.etLastName.text.toString().trim()

                // Validaciones
                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() ||
                    firstName.isEmpty() || lastName.isEmpty()) {
                    Toast.makeText(this@RegisterActivity, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (password != confirmPassword) {
                    Toast.makeText(this@RegisterActivity, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (password.length < 6) {
                    Toast.makeText(this@RegisterActivity, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                if (firstName.length < 2) {
                    binding.etFirstName.error = "Nombre demasiado corto"
                    return@launch
                }

                if (lastName.length < 2) {
                    binding.etLastName.error = "Apellido demasiado corto"
                    return@launch
                }

                if (imagenUri == null) {
                    Toast.makeText(this@RegisterActivity, "Selecciona una imagen de perfil", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val rutaImagenLocal = ImagenManager.convertirImagenABase64(contentResolver, imagenUri!!)

                if (rutaImagenLocal == null) {
                    Toast.makeText(this@RegisterActivity, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Llamada al ViewModel con todos los datos
                registerViewModel.register(email, password, firstName, lastName, rutaImagenLocal)
            }
        }


        // Configura el listener para el texto de inicio de sesión
        binding.tvLogin.setOnClickListener {
            // Simplemente finaliza esta actividad para volver a LoginActivity
            finish()
        }
    }


    /**
     * Navega a la PetListActivity y finaliza la RegisterActivity para que el usuario no pueda volver atrás.
     */
    private fun navigateToPetList() {
        val intent = Intent(this, PubliNListActivity::class.java)
        // Limpia la pila de actividades para que el usuario no pueda volver a la pantalla de registro
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish() // Finaliza esta actividad
    }

}


/**
 * Factory para crear instancias de RegisterViewModel.
 * Necesario para inyectar el AuthRepository en el ViewModel.
 */
class RegisterViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}