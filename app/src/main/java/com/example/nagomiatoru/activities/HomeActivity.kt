package com.example.nagomiatoru.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                    true
                }
                R.id.nav_profile -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment())
                        .commit()
                    true
                }
                R.id.nav_shopping -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ShoppingFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }
    }
}
