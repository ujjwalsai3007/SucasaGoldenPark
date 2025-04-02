package com.example.prestige

import android.app.Application
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class PrestigeApplication : Application() {
    private val TAG = "PrestigeApplication"
    
    companion object {
        // This will allow access to the current auth state throughout the app
        lateinit var auth: FirebaseAuth
            private set
    }
    
    override fun onCreate() {
        super.onCreate()
        
        try {
            // Initialize Firebase
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "Firebase app initialized successfully")
            
            // Initialize Firebase Authentication
            auth = FirebaseAuth.getInstance()
            Log.d(TAG, "Firebase Authentication initialized")
            
            // Enable offline persistence for Realtime Database
            val firebaseDatabase = FirebaseDatabase.getInstance()
            firebaseDatabase.setPersistenceEnabled(true)
            Log.d(TAG, "Firebase persistence enabled")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase: ${e.message}", e)
        }
    }
} 