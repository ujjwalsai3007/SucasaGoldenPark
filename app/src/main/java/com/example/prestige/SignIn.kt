package com.example.prestige

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prestige.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignIn : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var auth: FirebaseAuth
    private val TAG = "SignIn"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val savedEmail = sharedPreferences.getString("email", null)
        val savedPassword = sharedPreferences.getString("password", null)
        val isRemembered = sharedPreferences.getBoolean("rememberMe", false)

        if (isRemembered) {
            binding.email.setText(savedEmail)
            binding.password.setText(savedPassword)
            binding.rememberMe.isChecked = true
        }

        // Set up sign up link click listener
        binding.signUpLink.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
        }

        binding.signInBtn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val rememberMe = binding.rememberMe.isChecked

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable sign in button to prevent multiple attempts
            binding.signInBtn.isEnabled = false
            
            try {
                // Sign in with Firebase Authentication
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { authTask ->
                        if (authTask.isSuccessful) {
                            // Authentication successful, now fetch user data from the database
                            Log.d(TAG, "Firebase Authentication successful")
                            
                            val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                            databaseReference.child(email.replace(".", ",")).get()
                                .addOnSuccessListener { snapshot ->
                                    if (snapshot.exists()) {
                                        val userName = snapshot.child("name").value.toString()
                                        val role = snapshot.child("role").value.toString()
                                        val houseNumber = snapshot.child("houseNumber").value?.toString() ?: "Unknown"

                                        Log.d(TAG, "User data retrieved successfully with role: $role")
                                        val editor = sharedPreferences.edit()
                                        if (rememberMe) {
                                            editor.putString("email", email)
                                            editor.putString("password", password)
                                            editor.putBoolean("rememberMe", true)
                                        }
                                        editor.putBoolean("isLoggedIn", true) // Save login state
                                        editor.putString("houseNumber", houseNumber)
                                        editor.putString("role", role)
                                        editor.putString("userName", userName)
                                        editor.putString("uid", auth.currentUser?.uid ?: "")
                                        editor.apply()

                                        // Redirect to respective main screen
                                        when (role) {
                                            "Resident" -> startActivity(Intent(this, ResidentsScreen::class.java))
                                            "President" -> startActivity(Intent(this, PresidentsScreen::class.java))
                                            "Security Guard" -> startActivity(
                                                Intent(
                                                    this,
                                                    SecurityGuardScreen::class.java
                                                )
                                            )

                                            else -> Toast.makeText(
                                                this,
                                                "Invalid role detected",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        finish() // Close SignIn activity
                                    } else {
                                        // User document not found in the database
                                        Log.d(TAG, "User found in Authentication but not in database")
                                        Toast.makeText(this, "User data not found", Toast.LENGTH_SHORT).show()
                                        binding.signInBtn.isEnabled = true
                                        
                                        // Sign out the user from Firebase Auth since we couldn't find their data
                                        auth.signOut()
                                    }
                                }
                                .addOnFailureListener { e ->
                                    Log.e(TAG, "Error retrieving user data: ${e.message}", e)
                                    Toast.makeText(this, "Sign in failed: ${e.message}", Toast.LENGTH_LONG).show()
                                    binding.signInBtn.isEnabled = true
                                    
                                    // Sign out the user from Firebase Auth since we couldn't get their data
                                    auth.signOut()
                                }
                        } else {
                            // Authentication failed
                            Log.e(TAG, "Firebase Authentication failed: ${authTask.exception?.message}")
                            Toast.makeText(this, "Incorrect email or password", Toast.LENGTH_SHORT).show()
                            binding.signInBtn.isEnabled = true
                        }
                    }
            } catch (e: Exception) {
                Log.e(TAG, "Exception during sign in: ${e.message}", e)
                Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                binding.signInBtn.isEnabled = true
            }
        }
    }
}