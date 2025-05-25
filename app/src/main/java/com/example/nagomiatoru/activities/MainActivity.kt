package com.example.nagomiatoru.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.nagomiatoru.R
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.nagomiatoru.data.App
import com.example.nagomiatoru.data.SessionManager

class MainActivity : ComponentActivity() {

    private val SPLASH_TIMEOUT: Long = 2000 // 2 seconds

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Initialize SessionManager
        SessionManager.init(this)

    }

    public override fun onStart() {
        super.onStart()

        val currentUser = App.auth.currentUser

        Handler(Looper.getMainLooper()).postDelayed({
            if (currentUser != null) {
                Log.d("INCIO-----------", currentUser.email.toString() + " con uid: " + currentUser.uid.toString())
                navigateToHome()
            } else {
                val intent = Intent(this, WelcomeActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, SPLASH_TIMEOUT)

    }

    private fun navigateToHome() {
        // Navigate To HomeActivity
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}