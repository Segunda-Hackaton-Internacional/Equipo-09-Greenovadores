package com.example.nagomiatoru.models

data class GeoapifyResponse(
    val features: List<Feature>
)

data class Feature(
    val properties: PlaceProperties
)

data class PlaceProperties(
    val name: String?,
    val street: String?,
    val city: String?,
    val country: String?,
    val formatted: String?,
    val lat: Double?,
    val lon: Double?
)
