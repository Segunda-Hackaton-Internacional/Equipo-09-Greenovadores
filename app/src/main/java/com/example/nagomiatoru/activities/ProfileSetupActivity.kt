package com.example.nagomiatoru.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nagomiatoru.data.App
import com.example.nagomiatoru.data.SessionManager
import com.example.nagomiatoru.databinding.ActivityProfileSetupBinding
import com.example.nagomiatoru.models.User
import com.example.nagomiatoru.models.enums.SexEnum
import com.example.nagomiatoru.utils.SuccessDialogFragment
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import java.util.Calendar

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileSetupBinding
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Iniciar ViewBinding
        binding = ActivityProfileSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ocultar la barra de acción
        supportActionBar?.hide()


        // Set up click listeners
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.buttonAddPhoto.setOnClickListener {
            // Open gallery or camera to select profile photo
            showImagePickerDialog()
        }

        val genderOptions = SexEnum.getDisplayList()
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGender.adapter = adapter

        binding.editTextDateOfBirth.setOnClickListener {
            // Show date picker
            showDatePickerDialog()
        }

        binding.buttonSkip.setOnClickListener {
            // Skip profile setup and navigate to main app
            performLogin()
        }

        binding.buttonContinue.setOnClickListener {
            // Save profile data and navigate to main app
            if (validateForm()) {
                saveUser()
            }
        }
    }

    private fun showImagePickerDialog() {
        val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Choose your profile picture")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    // Handle camera intent
                    Toast.makeText(this, "Camera option selected", Toast.LENGTH_SHORT).show()
                }
                options[item] == "Choose from Gallery" -> {
                    // Handle gallery intent
                    Toast.makeText(this, "Gallery option selected", Toast.LENGTH_SHORT).show()
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }


    private fun showDatePickerDialog() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            // Format date and set to EditText
            val dateFormat = android.text.format.DateFormat.getDateFormat(applicationContext)
            binding.editTextDateOfBirth.setText(dateFormat.format(calendar.time))
        }

        DatePickerDialog(
            this, dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun validateForm(): Boolean {
        return binding.editTextFullName.text.toString().isNotEmpty() and
                binding.phoneCodePicker.selectedCountryCodeWithPlus.toString().isNotEmpty() and
                binding.editTextPhoneNumber.text.toString().isNotEmpty() and
                binding.spinnerGender.selectedItem.toString().isNotEmpty() and
                binding.editTextDateOfBirth.text.toString().isNotEmpty()
    }

    private fun saveUser() {
        val selectedDisplayName = binding.spinnerGender.selectedItem.toString()
        val selectedSexEnum = SexEnum.values().find { it.displayName == selectedDisplayName }
        val sex = selectedSexEnum?.value.toString()
        val name = binding.editTextFullName.text.toString().trim()
        val dateOfBirth = binding.editTextDateOfBirth.text.toString()
        val code = binding.phoneCodePicker.selectedCountryCodeWithPlus
        val phoneNumber = code + binding.editTextPhoneNumber.text.toString()
        val email = intent.getStringExtra("email").toString()
        val password = intent.getStringExtra("password").toString()

        Log.d("UserRegistration", "Starting user registration process")
        Log.v("UserRegistration", "User data - Name: $name, Email: $email, Sex: $sex, DoB: $dateOfBirth, Phone: $phoneNumber")

        // Crear usuario en Firebase Auth
        App.auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (authTask.isSuccessful) {
                    val userId = authTask.result?.user?.uid ?: ""
                    Log.d("UserRegistration", "Auth successful. User ID: $userId")

                    // Crear objeto User para Firestore
                    val user = User(
                        uid = userId,
                        name = name,
                        email = email,
                        sex = sex,
                        birthDate = dateOfBirth,
                        phone = phoneNumber
                    )

                    Log.d("UserRegistration", "Attempting to save user data to Firestore")

                    // Guardar en Firestore
                    App.firestore.collection("users")
                        .document(userId)
                        .set(user)
                        .addOnSuccessListener {
                            Log.i("UserRegistration", "User data successfully saved to Firestore")
                            // Guardar datos en ocal storage
                            SessionManager.saveSession(user)
                            performLogin()
                        }
                        .addOnFailureListener { e ->
                            Log.e("UserRegistration", "Error saving user data to Firestore", e)
                            // Opcional: Mostrar error al usuario de otra forma (snackbar, dialog)
                        }
                } else {
                    val exception = authTask.exception
                    Log.e("UserRegistration", "Authentication failed", exception)

                    when (exception) {
                        is FirebaseAuthWeakPasswordException ->
                            Log.w("UserRegistration", "Weak password: ${exception.message}")
                        is FirebaseAuthInvalidCredentialsException ->
                            Log.w("UserRegistration", "Invalid email format: ${exception.message}")
                        is FirebaseAuthUserCollisionException ->
                            Log.w("UserRegistration", "User already exists: ${exception.message}")
                        else ->
                            Log.e("UserRegistration", "Unknown authentication error", exception)
                    }
                    // Opcional: Mostrar error al usuario
                }
            }
    }

    private fun performLogin() {
        // Mostrar el diálogo de éxito
        SuccessDialogFragment.show(
            fragmentManager = supportFragmentManager,
            title = "Sign Up Successful!",
            message = "Please wait.\nYou will be directed to the homepage.",
            delayMillis = 2000
        ) {
            // Esta acción se ejecutará cuando el diálogo se cierre
            navigateToHomeApp()
        }
    }

    private fun navigateToHomeApp() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}