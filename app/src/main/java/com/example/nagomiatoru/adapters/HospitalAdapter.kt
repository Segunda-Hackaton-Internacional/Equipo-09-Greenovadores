package com.example.nagomiatoru.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.nagomiatoru.R
import com.example.nagomiatoru.models.Hospital

class HospitalAdapter(private var hospitalList: List<Hospital>) :
    RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder>() {

    inner class HospitalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.name)
        val speciality: TextView = itemView.findViewById(R.id.speciality)
        val hours: TextView = itemView.findViewById(R.id.hours)
        val phone: TextView = itemView.findViewById(R.id.phone)
        val address: TextView = itemView.findViewById(R.id.Address)
        val imgSpeciality: ImageView = itemView.findViewById(R.id.imgSpeciality)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HospitalViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hospital, parent, false)
        return HospitalViewHolder(view)
    }

    override fun onBindViewHolder(holder: HospitalViewHolder, position: Int) {
        val hospital = hospitalList[position]
        holder.name.text = hospital.name
        holder.speciality.text = hospital.speciality
        holder.hours.text = "${hospital.openHour} - ${hospital.closeHour}"
        holder.phone.text = hospital.phone
        holder.address.text = hospital.address

        // Cargar imagen basada en la especialidad
        val context = holder.itemView.context
        val resourceName = hospital.speciality.lowercase().replace(" ", "_")
        val resourceId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        if (resourceId != 0) {
            holder.imgSpeciality.setImageResource(resourceId)
        } else {
            holder.imgSpeciality.setImageResource(R.drawable.other) // default
        }
    }

    override fun getItemCount(): Int = hospitalList.size

    fun updateData(newList: List<Hospital>) {
        hospitalList = newList
        notifyDataSetChanged()
    }
}
