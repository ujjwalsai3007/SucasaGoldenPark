package com.example.prestige

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prestige.databinding.ActivitySignUpBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        binding.signup.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val houseNumber = binding.houseNumber.text.toString().trim()
            val role = binding.roleSpinner.selectedItem.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || houseNumber.isEmpty() || role == "Select Role") {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(name, email, password, houseNumber, role)
            databaseReference.child(email.replace(".", ",")).setValue(user).addOnSuccessListener {
                Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show()
                binding.name.text?.clear()
                binding.email.text?.clear()
                binding.password.text?.clear()
                binding.houseNumber.text?.clear()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to register user", Toast.LENGTH_SHORT).show()
            }
        }

        binding.textViewAlready.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
        }
    }
}

data class User(
    val name: String,         // User's full name
    val email: String,        // User's email address (e.g., user@example.com)
    val password: String,     // User's password (hashed or plaintext)
    val houseNumber: String,  // House number associated with the user (e.g., G1, G2)
    val role: String          // User role (e.g., Resident, President, Security Guard)
)