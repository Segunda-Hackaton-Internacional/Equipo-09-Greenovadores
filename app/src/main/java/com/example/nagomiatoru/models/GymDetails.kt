package com.example.nagomiatoru.models


data class GymDetails(
    val name: String,
    val address: String,
    val phone: String?,
    val openingHours: String?,
    val website: String?,
    val description: String?,
    val email: String?
)