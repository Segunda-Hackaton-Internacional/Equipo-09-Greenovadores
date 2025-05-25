package com.example.nagomiatoru.models

import com.example.nagomiatoru.models.Recipe

data class Hospital(
    val name: String,
    val speciality: String,
    val openHour: String,
    val closeHour: String,
    val phone: String,
    val address: String
) {
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", "", "", "", "", "")
}
