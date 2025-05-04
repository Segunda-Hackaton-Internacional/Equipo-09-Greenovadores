package com.example.nagomiatoru.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.text.method.SingleLineTransformationMethod
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.nagomiatoru.R
import com.example.nagomiatoru.utils.SuccessDialogFragment

class LoginActivity : AppCompatActivity() {

    private lateinit var etPassword: EditText
    private var passwordVisible = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Ocultar la barra de acción
        supportActionBar?.hide()

        // Inicializar vistas
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        val etEmail = findViewById<EditText>(R.id.et_email)
        etPassword = findViewById<EditText>(R.id.et_password)
        val cbRemember = findViewById<CheckBox>(R.id.cb_remember)
        val tvForgotPassword = findViewById<TextView>(R.id.tv_forgot_password)
        val tvSignUp = findViewById<TextView>(R.id.tv_signup)
        val btnLogin = findViewById<Button>(R.id.btn_login)

        // Configurar click listeners
        btnBack.setOnClickListener {
            finish()
        }

        // Manejar visibilidad de contraseña
        etPassword.setOnTouchListener { _, event ->
            // Detectar click en el icono de visibilidad (drawableEnd)
            val drawableEnd = 2

            if (event.rawX >= (etPassword.right - etPassword.compoundDrawables[drawableEnd].bounds.width())) {
                togglePasswordVisibility()
                return@setOnTouchListener true
            }
            false
        }

        tvForgotPassword.setOnClickListener {
            // Navegar a la pantalla de recuperación de contraseña
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }

        tvSignUp.setOnClickListener {
            // Navegar a la pantalla de registro
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            // Validar campos
            if (validateFields()) {
                // Realizar inicio de sesión
                performLogin(etEmail.text.toString(), etPassword.text.toString(), cbRemember.isChecked)
            }
        }
    }

    private fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible

        if (passwordVisible) {
            // Mostrar contraseña
            etPassword.transformationMethod = SingleLineTransformationMethod.getInstance()
            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_password_visible, 0)
        } else {
            // Ocultar contraseña
            etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            etPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_password_hidden, 0)
        }

        // Mantener cursor al final del texto
        etPassword.setSelection(etPassword.text.length)
    }

    private fun validateFields(): Boolean {
        val email = findViewById<EditText>(R.id.et_email).text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (email.isEmpty()) {
            findViewById<EditText>(R.id.et_email).error = "Please enter your email"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            findViewById<EditText>(R.id.et_email).error = "Please enter a valid email"
            return false
        }

        if (password.isEmpty()) {
            etPassword.error = "Please enter your password"
            return false
        }

        return true
    }

    private fun performLogin(email: String, password: String, rememberMe: Boolean) {
        // Aquí iría la lógica de autenticación real

        // Mostrar el diálogo de éxito
        SuccessDialogFragment.show(
            fragmentManager = supportFragmentManager,
            title = "Log in Successful!",
            message = "Please wait.\nYou will be directed to the homepage.",
            delayMillis = 2000
        ) {
            // Esta acción se ejecutará cuando el diálogo se cierre
            navigateToHome()
        }
    }

    private fun navigateToHome() {
        // Navegar a la actividad principal
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}