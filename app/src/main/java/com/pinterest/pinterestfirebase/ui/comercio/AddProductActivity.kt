package com.pinterest.pinterestfirebase.ui.comercio
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pinterest.pinterestfirebase.data.repository.AuthRepository
import com.pinterest.pinterestfirebase.databinding.ActivityAddProductBinding
import java.io.ByteArrayOutputStream

class AddProductActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddProductBinding
    private lateinit var viewModel: ProductAddViewModel
    private val imagesBase64 = mutableListOf<String>()

    private val pickImagesLauncher = registerForActivityResult(
        ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris?.forEachIndexed { index, uri ->
            val bitmap = uriToBitmap(uri)
            val base64 = bitmapToBase64(bitmap)
            imagesBase64.add(base64)
            updateImagePlaceholders(index, bitmap)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddProductBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar ViewModel
        val vmFactory = ProductAddViewModelFactory(
            ProductRepository(FirebaseFirestore.getInstance()),
            AuthRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
        )
        viewModel = ViewModelProvider(this, vmFactory)[ProductAddViewModel::class.java]

        // Listeners para agregar imágenes
        binding.mainImagePlaceholder.setOnClickListener { pickImagesLauncher.launch("image/*") }
        binding.addImage1.setOnClickListener { pickImagesLauncher.launch("image/*") }
        binding.addImage2.setOnClickListener { pickImagesLauncher.launch("image/*") }
        binding.addImage3.setOnClickListener { pickImagesLauncher.launch("image/*") }

        // Botón de publicar
        binding.publishButton.setOnClickListener {
            val name = binding.inputProductName.text.toString().trim()
            val price = binding.inputPrice.text.toString().toDoubleOrNull()
            val stock = binding.inputStock.text.toString().toIntOrNull()
            val detail = binding.inputDetail.text.toString().trim()
            val category = binding.inputCategory.text.toString().trim()

            if (name.isEmpty() || price == null || stock == null || detail.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (imagesBase64.isEmpty()) {
                Toast.makeText(this, "Debe seleccionar al menos una imagen", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "usuario_desconocido"
            val db = FirebaseFirestore.getInstance()
            val docRef = db.collection("products").document()
            val productId = docRef.id
            // Crear objeto producto

            val product = Product(
                id = productId,
                ownerId = FirebaseAuth.getInstance().currentUser?.uid ?: "usuario_desconocido",
                name = name,
                price = price,
                stock = stock,
                detail = detail,
                category = category,
                imagesBase64 = imagesBase64
            )
            // Guardar producto con ID
            docRef.set(product)
                .addOnSuccessListener {
                    Toast.makeText(this, "Publicado con éxito", Toast.LENGTH_SHORT).show()

                    // Devolver solo el ID
                    val resultIntent = Intent()
                    resultIntent.putExtra("producto_id", productId)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Error al guardar: ${it.message}", Toast.LENGTH_LONG).show()
                }
            // Guardar en Firebase
            viewModel.addProduct(
                name = name,
                price = price,
                stock = stock,
                detail = detail,
                category = category,
                imagesBase64 = imagesBase64
            )

            // Devolver producto a ComercioActivity
            val resultIntent = Intent()
            resultIntent.putExtra("nuevo_producto", product)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // Observador del resultado en Firebase
        viewModel.result.observe(this) { res ->
            res.onSuccess {
                Toast.makeText(this, "Publicado con éxito", Toast.LENGTH_SHORT).show()
            }.onFailure {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Botón atrás
        binding.backButton.setOnClickListener {
            startActivity(Intent(this, ComercioActivity::class.java))
            finish()
        }
    }

    private fun uriToBitmap(uri: Uri): Bitmap {
        return if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        } else {
            val source = ImageDecoder.createSource(contentResolver, uri)
            ImageDecoder.decodeBitmap(source)
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream)
        val byteArray = stream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }

    private fun updateImagePlaceholders(index: Int, bitmap: Bitmap) {
        when (index) {
            0 -> binding.mainImagePlaceholder.setImageBitmap(bitmap)
            1 -> binding.addImage1.setImageBitmap(bitmap)
            2 -> binding.addImage2.setImageBitmap(bitmap)
            3 -> binding.addImage3.setImageBitmap(bitmap)
        }
    }
}
