package com.example.prestige

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prestige.databinding.ActivitySignInBinding
import com.google.firebase.database.FirebaseDatabase

class SignIn : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        // Auto-fill email and password if "Remember Me" is checked
        val savedEmail = sharedPreferences.getString("email", null)
        val savedPassword = sharedPreferences.getString("password", null)
        val isRemembered = sharedPreferences.getBoolean("rememberMe", false)

        if (isRemembered) {
            binding.email.setText(savedEmail)
            binding.password.setText(savedPassword)
            binding.rememberMe.isChecked = true
        }

        val emailField = binding.email
        val passwordField = binding.password
        val signInButton = binding.signInBtn
        val rememberMeCheckbox = binding.rememberMe

        signInButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val rememberMe = rememberMeCheckbox.isChecked

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Firebase authentication
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            databaseReference.child(email.replace(".", ",")).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val storedPassword = snapshot.child("password").value.toString()
                    val role = snapshot.child("role").value.toString()
                    val houseNumber = snapshot.child("houseNumber").value.toString() // Fetch house number

                    if (storedPassword == password) {
                        // Save credentials if "Remember Me" is checked
                        val editor = sharedPreferences.edit()
                        if (rememberMe) {
                            editor.putString("email", email)
                            editor.putString("password", password)
                            editor.putBoolean("rememberMe", true)
                        }
                        // Save house number regardless of "Remember Me"
                        editor.putString("houseNumber", houseNumber)
                        editor.apply()

                        // Role-based redirection
                        when (role) {
                            "Resident" -> {
                                val intent = Intent(this, ResidentsScreen::class.java)
                                startActivity(intent)
                            }
                            "President" -> {
                                val intent = Intent(this, PresidentsScreen::class.java)
                                startActivity(intent)
                            }
                            "Security Guard" -> {
                                val intent = Intent(this, SecurityGuardScreen::class.java)
                                startActivity(intent)
                            }
                            else -> {
                                Toast.makeText(this, "Invalid role detected", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User does not exist", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to login", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

