package com.example.prestige

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prestige.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private val TAG = "SignUp"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        try {
            // Initialize Firebase Database
            databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            Log.d(TAG, "Firebase Database initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Firebase Database: ${e.message}", e)
            Toast.makeText(this, "Error connecting to database", Toast.LENGTH_SHORT).show()
        }

        binding.signup.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val houseNumber = binding.houseNumber.text.toString().trim()
            val role = binding.spinner.selectedItem.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || houseNumber.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Show loading indicator or disable button
            binding.signup.isEnabled = false
            
            try {
                // Create user with Firebase Authentication first
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            // Authentication successful, get the new user's UID
                            val uid = auth.currentUser?.uid ?: ""
                            Log.d(TAG, "Firebase Authentication user created with UID: $uid")
                            
                            // Now store the user data in the Realtime Database
                            val user = User(name, email, password, houseNumber, role, uid)
                            
                            // Save by both email key and UID for flexibility
                            val emailKey = email.replace(".", ",")
                            databaseReference.child(emailKey).setValue(user)
                                .addOnSuccessListener {
                                    Log.d(TAG, "User data stored in Realtime Database successfully")
                                    
                                    // Also save under UID reference
                                    databaseReference.child(uid).setValue(user)
                                    
                                    Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show()
                                    binding.name.text?.clear()
                                    binding.email.text?.clear()
                                    binding.password.text?.clear()
                                    binding.houseNumber.text?.clear()
                                    
                                    // Navigate to sign in screen
                                    startActivity(Intent(this, SignIn::class.java))
                                    finish()
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Failed to store user data: ${e.message}", e)
                                    Toast.makeText(this, "Failed to register user: ${e.message}", Toast.LENGTH_LONG).show()
                                    binding.signup.isEnabled = true
                                }
                        } else {
                            // Authentication failed
                            Log.e(TAG, "Firebase Authentication failed: ${authTask.exception?.message}")
                            Toast.makeText(this, "Registration failed: ${authTask.exception?.message}", Toast.LENGTH_LONG).show()
                            binding.signup.isEnabled = true
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during registration: ${e.message}", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                binding.signup.isEnabled = true
            }
        }

        binding.textViewAlready.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }
    }
}

data class User(
    val name: String,         // User's full name
    val email: String,        // User's email address (e.g., user@example.com)
    val password: String,     // User's password (hashed or plaintext)
    val houseNumber: String,  // House number associated with the user (e.g., G1, G2)
    val role: String,         // User role (e.g., Resident, President, Security Guard)
    val uid: String = ""      // Firebase Authentication UID
)