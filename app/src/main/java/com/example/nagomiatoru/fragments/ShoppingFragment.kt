package com.example.nagomiatoru.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.example.nagomiatoru.R

class ShoppingFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_shopping, container, false)

        // Configurar botón para ir a la tienda online
        setupVisitStoreButton(view)

        return view
    }

    private fun setupVisitStoreButton(view: View) {
        val btnVisitStore = view.findViewById<Button>(R.id.btn_visit_store)
        btnVisitStore.setOnClickListener {
            openWebStore()
        }
    }

    private fun openWebStore() {
        // URL de la tienda online (reemplazar con la URL real)
        val storeUrl = "http://nagomi-atoru.site"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(storeUrl))
        startActivity(intent)
    }
}