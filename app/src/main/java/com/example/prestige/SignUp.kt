package com.example.prestige

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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

        // Resize drawables
        setupDrawables()

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

        // Set up the role spinner
        val roles = arrayOf("Resident", "President", "Security Guard")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, roles)
        binding.roleSpinner.setAdapter(adapter)
        binding.roleSpinner.setText(roles[0], false) // Set default selection to "Resident"

        binding.signup.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val houseNumber = binding.houseNumber.text.toString().trim()
            val role = binding.roleSpinner.text.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || houseNumber.isEmpty() || role.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!roles.contains(role)) {
                Toast.makeText(this, "Please select a valid role", Toast.LENGTH_SHORT).show()
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
                                    binding.roleSpinner.setText(roles[0], false)
                                    
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

    private fun setupDrawables() {
        val drawables = mapOf(
            binding.name to R.drawable.user_svgrepo_com,
            binding.email to R.drawable.email_1_svgrepo_com,
            binding.password to R.drawable.password_svgrepo_com,
            binding.houseNumber to R.drawable.home_svgrepo_com
        )

        // Convert dp to pixels - using 16dp for smaller icons
        val size = (16 * resources.displayMetrics.density).toInt() // 16dp converted to pixels

        drawables.forEach { (editText, drawableRes) ->
            val drawable = ContextCompat.getDrawable(this, drawableRes)?.apply {
                setBounds(0, 0, size, size)
            }
            editText.setCompoundDrawables(drawable, null, null, null)
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