package com.pinterest.pinterestfirebase.ui.publicacion

import android.content.Intent
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pinterest.pinterestfirebase.data.model.PublicacionN
import com.pinterest.pinterestfirebase.R

class PublicacionNHomeAdapter : ListAdapter<PublicacionN, PublicacionNHomeAdapter.HomeViewHolder>(PubliNDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_home, parent, false)
        return HomeViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imagen: ImageView = itemView.findViewById(R.id.imagenHome)

        fun bind(publi: PublicacionN) {
            if (publi.imageBase64.isNotEmpty()) {
                try {
                    val decodedBytes = Base64.decode(publi.imageBase64, Base64.DEFAULT)
                    val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                    imagen.setImageBitmap(bitmap)
                } catch (e: Exception) {
                    imagen.setImageResource(R.drawable.ic_image_placeholder)
                }
            } else {
                imagen.setImageResource(R.drawable.ic_image_placeholder)
            }

            // Click listener para navegar al detalle
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, DetalleHomeActivity::class.java).apply {
                    putExtra("PUBLICACION_ID", publi.id)
                    putExtra("PUBLICACION_NOMBRE", publi.name)
                    putExtra("PUBLICACION_DESCRIPCION", publi.descripcion)
                    putExtra("PUBLICACION_IMAGE", publi.imageBase64)
                }
                context.startActivity(intent)
            }
        }
    }
}
