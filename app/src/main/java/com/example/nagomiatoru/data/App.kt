package com.example.nagomiatoru.data

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class App: Application() {
    companion object {
        lateinit var auth: FirebaseAuth
        lateinit var firestore: FirebaseFirestore
    }

    override fun onCreate() {
        super.onCreate()

        // Inicialización de Firebase
        FirebaseApp.initializeApp(this)

        // Configurar instancias únicas
        auth = Firebase.auth
        firestore = Firebase.firestore

    }
}