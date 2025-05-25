package com.example.nagomiatoru.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.nagomiatoru.R
import com.google.android.material.button.MaterialButton
import androidx.fragment.app.Fragment
import com.example.nagomiatoru.activities.NearbyGymsActivity

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Encuentra los botones
        val btnRecetas = view.findViewById<MaterialButton>(R.id.btn_recetas)
        val btnGimnasios = view.findViewById<MaterialButton>(R.id.btn_gimnasios)
        val btnClinicas = view.findViewById<MaterialButton>(R.id.btn_clinicas)
        val btnRecreacion = view.findViewById<MaterialButton>(R.id.btn_recreacion)

        // Redirección a cada fragmento
        btnClinicas.setOnClickListener {
            Toast.makeText(requireContext(), "Funcionalidad en desarrollo", Toast.LENGTH_SHORT).show()
        }

        btnGimnasios.setOnClickListener {
            val intent = Intent(requireContext(), NearbyGymsActivity::class.java)
            startActivity(intent)
        }


        btnRecetas.setOnClickListener {
            Toast.makeText(requireContext(), "Funcionalidad en desarrollo", Toast.LENGTH_SHORT).show()
        }

        btnRecreacion.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RecreationalPlacesFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}
