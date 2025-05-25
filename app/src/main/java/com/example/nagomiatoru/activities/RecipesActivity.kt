package com.example.nagomiatoru.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.nagomiatoru.R
import com.example.nagomiatoru.data.App
import com.example.nagomiatoru.databinding.ActivityProfileSetupBinding
import com.example.nagomiatoru.databinding.ActivityRecipesBinding
import com.example.nagomiatoru.fragments.RecipesFragment
import com.example.nagomiatoru.models.Recipe

class RecipesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRecipesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Iniciar ViewBinding
        binding = ActivityRecipesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ocultar la barra de acción
        supportActionBar?.hide()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RecipesFragment())
                .commit()
        }

    }


}