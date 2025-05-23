package com.example.nagomiatoru.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.example.nagomiatoru.R
import com.example.nagomiatoru.activities.LoginActivity
import com.example.nagomiatoru.activities.WelcomeActivity
import com.example.nagomiatoru.data.App
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflar el layout correcto para el fragmento de perfil
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Configurar los elementos de UI y sus listeners
        setupUIElements(view)

        return view
    }

    private fun setupUIElements(view: View) {
        // Configurar el switch de modo oscuro
        val switchDarkMode = view.findViewById<SwitchCompat>(R.id.switch_dark_mode)
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            // Aquí iría la lógica para cambiar al modo oscuro
            val message = if (isChecked) "Modo oscuro activado" else "Modo oscuro desactivado"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        // Configurar elementos clickeables
        setupClickableItem(view, R.id.layout_personal_info, "Información personal")
        setupClickableItem(view, R.id.layout_language, "Seleccionar idioma")
        setupClickableItem(view, R.id.layout_privacy_policy, "Política de privacidad")
        setupClickableItem(view, R.id.layout_about_app, "Acerca de la aplicación")

        // Configurar botón de actualizar a PRO
        val layoutUpgradePro = view.findViewById<LinearLayout>(R.id.layout_upgrade_pro)
        layoutUpgradePro.setOnClickListener {
            Toast.makeText(context, "Actualizar a PRO", Toast.LENGTH_SHORT).show()
        }

        // Configurar botón de cierre de sesión
        val layoutLogout = view.findViewById<LinearLayout>(R.id.layout_logout)
        layoutLogout.setOnClickListener {
            Toast.makeText(context, "Cerrando sesión...", Toast.LENGTH_SHORT).show()
            App.auth.signOut()
            val intent = Intent(requireContext(), WelcomeActivity::class.java)
            startActivity(intent)
            requireActivity().finish()

        }
    }

    private fun setupClickableItem(view: View, layoutId: Int, toastMessage: String) {
        val layout = view.findViewById<LinearLayout>(layoutId)
        layout.setOnClickListener {
            Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT).show()
        }
    }
}