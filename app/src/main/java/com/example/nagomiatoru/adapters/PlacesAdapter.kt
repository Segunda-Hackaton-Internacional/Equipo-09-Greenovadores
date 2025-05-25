package com.example.nagomiatoru.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nagomiatoru.R
import com.example.nagomiatoru.models.PlaceProperties

class   PlacesAdapter(private val places: List<PlaceProperties>) :
    RecyclerView.Adapter<PlacesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.tvName)
        val address: TextView = view.findViewById(R.id.tvAddress)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_place, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val place = places[position]
        holder.name.text = place.name ?: "Sin nombre"
        holder.address.text = place.formatted ?: "Sin dirección"
    }

    override fun getItemCount(): Int = places.size
}