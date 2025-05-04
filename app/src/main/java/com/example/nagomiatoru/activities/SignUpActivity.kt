package com.example.nagomiatoru.activities

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nagomiatoru.R
import com.example.nagomiatoru.utils.SuccessDialogFragment

class SignUpActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var checkboxTerms: CheckBox
    private lateinit var textPublicAgreement: TextView
    private lateinit var textTerms: TextView
    private lateinit var textPrivacyPolicy: TextView
    private lateinit var textLogin: TextView
    private lateinit var buttonContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // Ocultar la barra de acción
        supportActionBar?.hide()

        // Initialize views
        btnBack = findViewById(R.id.btnBack)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        checkboxTerms = findViewById(R.id.checkboxTerms)
        textPublicAgreement = findViewById(R.id.textPublicAgreement)
        textTerms = findViewById(R.id.textTerms)
        textPrivacyPolicy = findViewById(R.id.textPrivacyPolicy)
        textLogin = findViewById(R.id.textLogin)
        buttonContinue = findViewById(R.id.buttonContinue)

        // Set up password field
        editTextPassword.transformationMethod = PasswordTransformationMethod.getInstance()

        // Set up click listeners
        btnBack.setOnClickListener {
            finish()
        }

        textLogin.setOnClickListener {
            // Navigate to login screen
            // Intent to LoginActivity
            finish()
        }

        textPublicAgreement.setOnClickListener {
            // Open public agreement document
        }

        textTerms.setOnClickListener {
            // Open terms document
        }

        textPrivacyPolicy.setOnClickListener {
            // Open privacy policy document
        }

        buttonContinue.setOnClickListener {
            if (validateForm()) {
                // Save user credentials
                saveUserCredentials()

                // Navigate to profile setup activity
                val intent = Intent(this, ProfileSetupActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validate email
        val email = editTextEmail.text.toString().trim()
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.error = "Please enter a valid email address"
            isValid = false
        }

        // Validate password
        val password = editTextPassword.text.toString()
        if (password.isEmpty() || password.length < 8) {
            editTextPassword.error = "Password must be at least 8 characters"
            isValid = false
        }

        // Validate terms acceptance
        if (!checkboxTerms.isChecked) {
            checkboxTerms.error = "You must accept the terms"
            isValid = false
        }

        return isValid
    }

    private fun saveUserCredentials() {
        // Save user email and password to SharedPreferences or your backend
        val email = editTextEmail.text.toString().trim()
        val password = editTextPassword.text.toString()
    }


}