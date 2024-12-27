package com.example.prestige

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.prestige.databinding.ActivitySignInBinding
import com.google.firebase.database.FirebaseDatabase

class SignIn : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val emailField = binding.email
        val passwordField = binding.password
        val signInButton = binding.signinn
        val rememberMeCheckbox = binding.remembermee

        // Pre-fill fields if "Remember Me" is enabled
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        if (sharedPreferences.getBoolean("rememberMe", false)) {
            emailField.setText(sharedPreferences.getString("email", ""))
            passwordField.setText(sharedPreferences.getString("password", ""))
            rememberMeCheckbox.isChecked = true
        }

        signInButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val rememberMe = rememberMeCheckbox.isChecked

            // Validation for empty fields
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill all the details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Email validation
            if (!email.endsWith("@gmail.com")) {
                Toast.makeText(this, "Email must end with @gmail.com", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save or clear SharedPreferences
            val editor = sharedPreferences.edit()
            if (rememberMe) {
                editor.putString("email", email)
                editor.putString("password", password)
                editor.putBoolean("rememberMe", true)
                editor.apply()
            } else {
                editor.clear()
                editor.apply()
            }

            // Firebase authentication
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            databaseReference.child(email.replace(".", ",")).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val storedPassword = snapshot.child("password").value.toString()
                    if (storedPassword == password) {
                        Toast.makeText(this, "Logged in Successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainScreen::class.java)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, "Invalid Password", Toast.LENGTH_SHORT).show()
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