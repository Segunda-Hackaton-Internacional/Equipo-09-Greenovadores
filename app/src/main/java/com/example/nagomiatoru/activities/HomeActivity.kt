package com.example.nagomiatoru.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.nagomiatoru.R
import com.example.nagomiatoru.fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        supportActionBar?.hide()
        bottomNavigationView = findViewById(R.id.bottom_navigation)


        val targetFragment = intent.getIntExtra("fragment", R.id.nav_home)

        val fragment = when (targetFragment) {
            R.id.nav_profile -> ProfileFragment()
            R.id.nav_shopping -> ShoppingFragment()
            else -> HomeFragment()
        }


        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()

        bottomNavigationView.selectedItemId = targetFragment

        // Listener de navegación
        bottomNavigationView.setOnItemSelectedListener { item ->
          val fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_profile -> ProfileFragment()
                R.id.nav_shopping -> ShoppingFragment()
                R.id.nav_wellness -> WellnessFragment()
                //R.id.nav_favorites -> goto()
                else -> null
            }
        }

    }

}
