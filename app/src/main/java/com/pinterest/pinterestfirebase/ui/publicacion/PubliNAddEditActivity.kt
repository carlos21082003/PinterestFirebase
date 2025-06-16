package com.pinterest.pinterestfirebase.ui.publicacion

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pinterest.pinterestfirebase.data.model.PublicacionN
import com.pinterest.pinterestfirebase.data.repository.AuthRepository
import com.pinterest.pinterestfirebase.data.repository.PubliNRepository
import com.pinterest.pinterestfirebase.databinding.ActivityPubliNaddEditBinding
import java.io.ByteArrayOutputStream

class PubliNAddEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPubliNaddEditBinding
    private lateinit var addEditPubliNViewModel: PubliNAddEditViewModel
    private var publinId: String? = null // Para almacenar el ID de la mascota si estamos editando
    private var selectedImageBase64: String = ""

    // Launcher para seleccionar imagen de la galería
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            handleSelectedImage(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPubliNaddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializa Firebase Auth y Firestore
        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        // Inicializa los repositorios
        val authRepository = AuthRepository(firebaseAuth, firestore)
        val publiNRepository = PubliNRepository (firestore)

        // Inicializa el ViewModel con una Factory
        addEditPubliNViewModel = ViewModelProvider(this, AddEditPubliNViewModelFactory(publiNRepository, authRepository))
            .get(PubliNAddEditViewModel::class.java)


        // Configurar botón para seleccionar imagen
        binding.btnSelectImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        //Boton para cancelar y volver a la lista de mascotas
        binding.btnCancel.setOnClickListener {
            val intent = Intent(this, PubliNListActivity::class.java)
            startActivity(intent)
        }

        // Comprueba si estamos en modo edición (si se pasa un ID de mascota)
        publinId = intent.getStringExtra("PUBLICACION_ID")
        if (publinId != null) {

            // Si hay un ID, estamos editando una mascota existente
            binding.tvTitle.text = "Editar Publicacion"
            // Rellena los campos con los datos existentes
            binding.nProducto.setText(intent.getStringExtra("PUBLICACION_NAME"))
            binding.aDescripcion.setText(intent.getStringExtra("PUBLICACION_DESCRIPCION"))

            // Cargar imagen si existe
            val imageBase64 = intent.getStringExtra("PUBLICACION_IMAGE")
            if (!imageBase64.isNullOrEmpty()) {
                selectedImageBase64 = imageBase64
                loadImageFromBase64(imageBase64)
            }
        } else {
            // Si no hay ID, estamos añadiendo una nueva mascota
            binding.tvTitle.text = "Añadir Nueva Publicacion"
        }

        // Observa el resultado de la operación de guardar (añadir/actualizar)
        addEditPubliNViewModel.saveResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Publicacion guardada exitosamente", Toast.LENGTH_SHORT).show()
                finish() // Cierra la actividad y vuelve a la lista de mascotas
            }.onFailure { exception ->
                Toast.makeText(this, "Error al guardar Publicacion: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Configura el listener para el botón de guardar mascota

        binding.btnPublicar.setOnClickListener {
            savePet()
        }

    }

    private fun handleSelectedImage(uri: Uri) {
        try {
            // Convertir la imagen a Base64
            val imageBase64 = convertImageToBase64(uri)
            if (imageBase64.isNotEmpty()) {
                selectedImageBase64 = imageBase64
                loadImageFromBase64(imageBase64)
                Toast.makeText(this, "Imagen seleccionada correctamente", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error al procesar la imagen", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al seleccionar imagen: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertImageToBase64(uri: Uri): String {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)

            // Redimensionar la imagen para optimizar el tamaño
            val resizedBitmap = resizeBitmap(bitmap, 800, 600)

            val byteArrayOutputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val byteArray = byteArrayOutputStream.toByteArray()

            Base64.encodeToString(byteArray, Base64.DEFAULT)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    private fun resizeBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val bitmapRatio = width.toFloat() / height.toFloat()
        val targetRatio = maxWidth.toFloat() / maxHeight.toFloat()

        val targetWidth: Int
        val targetHeight: Int

        if (bitmapRatio > targetRatio) {
            targetWidth = maxWidth
            targetHeight = (maxWidth / bitmapRatio).toInt()
        } else {
            targetHeight = maxHeight
            targetWidth = (maxHeight * bitmapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }

    private fun loadImageFromBase64(base64String: String) {
        try {
            val decodedBytes = Base64.decode(base64String, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)

            binding.ivSelectedImage.setImageBitmap(bitmap)
            binding.ivSelectedImage.visibility = View.VISIBLE
        } catch (e: Exception) {
            e.printStackTrace()
            binding.ivSelectedImage.visibility = View.GONE
        }
    }

    /**
     * Recopila los datos de los campos de entrada y llama al ViewModel para guardar la mascota.
     */
    private fun savePet() {

        val name = binding.nProducto.text.toString().trim()
        val descripcion = binding.aDescripcion.text.toString().trim()


        if (name.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Crea un objeto Pet. Si petId es nulo, se añadirá una nueva mascota.
        // Si petId no es nulo, se actualizará la mascota existente.
        val publiN = PublicacionN(
            id = publinId ?: "", // Si petId es nulo, se usa una cadena vacía (para nueva mascota)
            name = name,
            descripcion = descripcion,
            ownerId = "", // El ViewModel se encargará de asignar el ownerId
            imageBase64 = selectedImageBase64
        )

        addEditPubliNViewModel.savePubliN(publiN)

    }
}


/**
 * Factory para crear instancias de AddEditPetViewModel.
 * Necesario para inyectar los repositorios en el ViewModel.
 */
class AddEditPubliNViewModelFactory(
    private val publiNRepository: PubliNRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PubliNAddEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PubliNAddEditViewModel(publiNRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}