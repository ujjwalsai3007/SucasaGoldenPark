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

        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)

        val savedEmail = sharedPreferences.getString("email", null)
        val savedPassword = sharedPreferences.getString("password", null)
        val isRemembered = sharedPreferences.getBoolean("rememberMe", false)

        if (isRemembered) {
            binding.email.setText(savedEmail)
            binding.password.setText(savedPassword)
            binding.rememberMe.isChecked = true
        }

        binding.signInBtn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val rememberMe = binding.rememberMe.isChecked

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
            databaseReference.child(email.replace(".", ",")).get().addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val storedPassword = snapshot.child("password").value.toString()
                    val userName = snapshot.child("name").value.toString() // Fetch user name
                    val role = snapshot.child("role").value.toString()
                    val houseNumber = snapshot.child("houseNumber").value?.toString() ?: "Unknown"

                    if (storedPassword == password) {
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
                        Toast.makeText(this, "Incorrect password", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}