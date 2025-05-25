package com.example.nagomiatoru.activities

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.nagomiatoru.adapters.HospitalAdapter
import com.example.nagomiatoru.data.App
import com.example.nagomiatoru.databinding.ActivityHospitalsBinding
import com.example.nagomiatoru.models.Hospital
import com.example.nagomiatoru.models.enums.DiseaseCategoryEnum

class HospitalsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHospitalsBinding
    private lateinit var hospitalAdapter: HospitalAdapter
    private var allHospitals = mutableListOf<Hospital>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHospitalsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ocultar la barra de acción
        supportActionBar?.hide()

        setupRecyclerView()
        setupSpinner()
        loadHospitals()

        binding.btnFilter.setOnClickListener {
            val selectedSpeciality = binding.spDiseases.selectedItem.toString()
            val filtered = if (selectedSpeciality == "All") {
                allHospitals
            } else {
                allHospitals.filter { it.speciality == selectedSpeciality }
            }
            hospitalAdapter.updateData(filtered)
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        hospitalAdapter = HospitalAdapter(emptyList())
        binding.rvHospitals.layoutManager = LinearLayoutManager(this)
        binding.rvHospitals.adapter = hospitalAdapter
    }

    private fun setupSpinner() {
        val specialities = listOf("All") + DiseaseCategoryEnum.values().map { it.toString() }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, specialities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spDiseases.adapter = adapter
    }


    private fun loadHospitals() {
        val db = App.firestore
        db.collection("hospitals")
            .get()
            .addOnSuccessListener { result ->
                allHospitals.clear()
                for (document in result) {
                    val hospital = document.toObject(Hospital::class.java)
                    allHospitals.add(hospital)
                }
                hospitalAdapter.updateData(allHospitals)
            }
            .addOnFailureListener {
                Log.d("Firestore", "Error loading hospitals")
            }
    }
}