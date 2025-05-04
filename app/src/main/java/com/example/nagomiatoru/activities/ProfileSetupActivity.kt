package com.example.nagomiatoru.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.nagomiatoru.R
import com.example.nagomiatoru.utils.SuccessDialogFragment
import java.util.Calendar

class ProfileSetupActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var imageProfile: ImageView
    private lateinit var buttonAddPhoto: ImageView
    private lateinit var editTextFullName: EditText
    private lateinit var layoutCountryCode: LinearLayout
    private lateinit var spinnerGender: Spinner
    private lateinit var editTextDateOfBirth: EditText
    private lateinit var buttonSkip: Button
    private lateinit var buttonContinue: Button

    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_setup)

        // Ocultar la barra de acción
        supportActionBar?.hide()

        // Initialize views
        btnBack = findViewById(R.id.btnBack)
        imageProfile = findViewById(R.id.imageProfile)
        buttonAddPhoto = findViewById(R.id.buttonAddPhoto)
        editTextFullName = findViewById(R.id.editTextFullName)
        layoutCountryCode = findViewById(R.id.layoutCountryCode)
        spinnerGender = findViewById(R.id.spinnerGender)
        editTextDateOfBirth = findViewById(R.id.editTextDateOfBirth)
        buttonSkip = findViewById(R.id.buttonSkip)
        buttonContinue = findViewById(R.id.buttonContinue)

        // Set up click listeners
        btnBack.setOnClickListener {
            finish()
        }

        buttonAddPhoto.setOnClickListener {
            // Open gallery or camera to select profile photo
            showImagePickerDialog()
        }

        layoutCountryCode.setOnClickListener {
            // Show country code picker
            showCountryCodeDialog()
        }

        spinnerGender = findViewById(R.id.spinnerGender)

        val genderOptions = listOf("Select Gender", "Male", "Female", "Other", "Prefer not to say")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGender.adapter = adapter

        editTextDateOfBirth.setOnClickListener {
            // Show date picker
            showDatePickerDialog()
        }

        buttonSkip.setOnClickListener {
            // Skip profile setup and navigate to main app
            performLogin()
        }

        buttonContinue.setOnClickListener {
            // Save profile data and navigate to main app
            if (validateForm()) {
                saveProfileData()
                performLogin()
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

    private fun showCountryCodeDialog() {
        // In a real app, you would show a list of country codes
        Toast.makeText(this, "Country code selection would appear here", Toast.LENGTH_SHORT).show()
    }

    private fun showGenderSelectionDialog() {
        val genders = arrayOf("Male", "Female", "Other", "Prefer not to say")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Gender")
        builder.setItems(genders) { dialog, which ->
            // Update gender field with selected option
            Toast.makeText(this, "Selected: ${genders[which]}", Toast.LENGTH_SHORT).show()
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
            editTextDateOfBirth.setText(dateFormat.format(calendar.time))
        }

        DatePickerDialog(
            this, dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun validateForm(): Boolean {
        var isValid = true

        // Validate name (optional validation)
        val name = editTextFullName.text.toString().trim()
        if (name.isEmpty()) {
            editTextFullName.error = "Please enter your name"
            isValid = false
        }

        return isValid
    }

    private fun saveProfileData() {
        // Save profile data to SharedPreferences or your backend
        val name = editTextFullName.text.toString().trim()
        val dateOfBirth = editTextDateOfBirth.text.toString()


        Toast.makeText(this, "Profile saved successfully", Toast.LENGTH_SHORT).show()
    }

    private fun navigateToHomeApp() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun performLogin() {
        // Aquí iría la lógica de autenticación real

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
}