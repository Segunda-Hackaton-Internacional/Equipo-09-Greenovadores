package com.example.nagomiatoru.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.nagomiatoru.R
import android.os.Handler
import android.os.Looper

class MainActivity : ComponentActivity() {

    private val SPLASH_TIMEOUT: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Delayed transition to main activity
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, WelcomeActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_TIMEOUT)
    }
}