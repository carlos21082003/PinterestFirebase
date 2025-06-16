package com.pinterest.pinterestfirebase.ui.product

import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.pinterest.pinterestfirebase.R
import com.pinterest.pinterestfirebase.ui.comercio.Product

class ProductAdapter(
    private val productos: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombre: TextView = itemView.findViewById(R.id.textProductName)
        val precio: TextView = itemView.findViewById(R.id.textProductPrice)
        val imagen: ImageView = itemView.findViewById(R.id.imageProduct)
        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClick(productos[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val producto = productos[position]
        holder.nombre.text = producto.name
        holder.precio.text = "S/ ${producto.price}"

        if (producto.imagesBase64.isNotEmpty()) {
            try {
                val imageBase64 = producto.imagesBase64[0]
                val imageBytes = Base64.decode(imageBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.imagen.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                holder.imagen.setImageResource(R.drawable.sample_product)
            }
        } else {
            holder.imagen.setImageResource(R.drawable.sample_product)
        }
    }

    override fun getItemCount(): Int = productos.size
}
