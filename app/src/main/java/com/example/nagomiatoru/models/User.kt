package com.example.nagomiatoru.models


data class User(
    val uid: String,
    val name: String,
    val email: String,
    val sex: String,
    val birthDate: String,
    val phone: String
){
    // Constructor sin argumentos requerido por Firestore
    constructor() : this("", "", "", "", "", "")
}
