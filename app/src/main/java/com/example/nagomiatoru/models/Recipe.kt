package com.example.nagomiatoru.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Recipe(
    val name: String,
    val description: String,
    val ingredients: List<String>,
    val imageUrl: String
) : Parcelable{
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", "", emptyList(), "")
}
