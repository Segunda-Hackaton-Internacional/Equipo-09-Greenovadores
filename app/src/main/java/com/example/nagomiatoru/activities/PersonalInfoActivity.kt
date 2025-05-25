package com.example.nagomiatoru.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.nagomiatoru.R
import com.example.nagomiatoru.data.SessionManager
import com.example.nagomiatoru.databinding.ActivityPersonalInfoBinding
import com.example.nagomiatoru.models.User
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class PersonalInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonalInfoBinding

    private val db = FirebaseFirestore.getInstance()
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        SessionManager.init(this)
        binding.buttonBack.setOnClickListener {
            finish()
        }
        setupSpinner()
        loadUserData()
        setupListeners()
    }

    private fun setupSpinner() {
        val sexOptions = arrayOf("Masculino", "Femenino", "Otro")
        val adapter = ArrayAdapter(this, R.layout.spinner_item, sexOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerSex.adapter = adapter
    }

    private fun loadUserData() {
        binding.etName.setText(SessionManager.getName() ?: "")
        binding.etPhone.setText(SessionManager.getPhone() ?: "")
        binding.etBirthDate.setText(SessionManager.getBirthDate() ?: "")

        // Configurar el spinner de sexo
        val currentSex = SessionManager.getSex() ?: "M"
        val sexPosition = when (currentSex.uppercase()) {
            "M" -> 0
            "F" -> 1
            else -> 2
        }
        binding.spinnerSex.setSelection(sexPosition)
    }

    private fun setupListeners() {
        binding.etBirthDate.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            saveUserData()
        }
    }

    private fun showDatePicker() {
        // Si hay una fecha previa, usarla como inicial
        val currentDate = SessionManager.getBirthDate()
        if (!currentDate.isNullOrEmpty()) {
            try {
                val date = dateFormat.parse(currentDate)
                date?.let { calendar.time = it }
            } catch (e: Exception) {
                Log.e("PersonalInfo", "Error parsing date: ${e.message}")
            }
        }

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                binding.etBirthDate.setText(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }

    private fun saveUserData() {
        val name = binding.etName.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val birthDate = binding.etBirthDate.text.toString().trim()
        val sexPosition = binding.spinnerSex.selectedItemPosition
        val sex = when (sexPosition) {
            0 -> "M"
            1 -> "F"
            else -> "O"
        }

        // Validaciones básicas
        if (name.isEmpty()) {
            binding.etName.error = "El nombre es requerido"
            binding.etName.requestFocus()
            return
        }

        val uid = SessionManager.getUid()
        if (uid.isNullOrEmpty()) {
            Toast.makeText(this, "Error: Usuario no autenticado", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear objeto User actualizado
        val updatedUser = SessionManager.getEmail()?.let {
            User(
                uid = uid,
                name = name,
                email = it,
                sex = sex,
                birthDate = birthDate,
                phone = phone
            )
        }

        // Actualizar en Firestore
        if (updatedUser != null) {
            updateUserInFirestore(updatedUser)
        }
    }

    private fun updateUserInFirestore(user: User) {
        binding.btnSave.isEnabled = false
        binding.btnSave.text = "Guardando..."

        db.collection("users")
            .document(user.uid)
            .set(user)
            .addOnSuccessListener {
                // Actualizar SessionManager
                SessionManager.saveSession(user)

                Toast.makeText(this, "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                finish() // Volver a la pantalla anterior
            }
            .addOnFailureListener { exception ->
                Log.e("PersonalInfo", "Error updating user: ", exception)
                Toast.makeText(this, "Error updating user", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                binding.btnSave.isEnabled = true
                binding.btnSave.text = "Save changes"
            }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}