package com.pinterest.pinterestfirebase

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pinterest.pinterestfirebase.data.model.Comentario
import com.pinterest.pinterestfirebase.data.repository.ComentarioAdapter

class ForoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ComentarioAdapter
    private val listaComentarios = mutableListOf<Comentario>()

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foro)

        recyclerView = findViewById(R.id.rvComentarios)
        val etComentario = findViewById<EditText>(R.id.etComentario)
        val btnEnviar = findViewById<Button>(R.id.btnEnviar)

        val imageSlider = findViewById<ImageSlider>(R.id.imageSlider)

        val slideModels = listOf(
            SlideModel(R.drawable.ambiente1, "Reducir"),
            SlideModel(R.drawable.ambiente2, "Reciclar"),
            SlideModel(R.drawable.ambiente3, "Separar")
        )

        imageSlider.setImageList(slideModels, ScaleTypes.FIT)




        adapter = ComentarioAdapter(listaComentarios)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnEnviar.setOnClickListener {
            val texto = etComentario.text.toString().trim()
            if (texto.isNotEmpty()) {
                val comentario = Comentario(
                    mensaje = texto,
                    fecha = System.currentTimeMillis(),
                    autor = auth.currentUser?.email ?: "Anónimo"
                )

                db.collection("comentarios").add(comentario)
                    .addOnSuccessListener {
                        etComentario.text.clear()
                    }
            }
        }

        // Escucha en tiempo real
        db.collection("comentarios")
            .orderBy("fecha", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshots, _ ->
                if (snapshots != null) {
                    listaComentarios.clear()
                    for (doc in snapshots.documents) {
                        val comentario = doc.toObject(Comentario::class.java)
                        comentario?.let { listaComentarios.add(it) }
                    }
                    adapter.notifyDataSetChanged()
                }
            }
    }
}