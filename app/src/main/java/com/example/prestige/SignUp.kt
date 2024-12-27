package com.example.prestige

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.prestige.databinding.ActivitySignUpBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase Database setup
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        // Input field and button reference
        val name = binding.name
        val email = binding.email
        val password = binding.password
        val houseNumber = binding.houseNumber
        val signupButton = binding.signup

        // Handling sign-up button
        signupButton.setOnClickListener {
            val username = name.text.toString().trim()
            val useremail = email.text.toString().trim()
            val userpassword = password.text.toString().trim()
            val userhouseNumber = houseNumber.text.toString().trim()

            // Validation for empty fields
            if (username.isEmpty() || useremail.isEmpty() || userpassword.isEmpty() || userhouseNumber.isEmpty()) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // House number validation
            val validHouseNumbers = listOf(
                "G1", "G2", "G3", "G4", "G5", "G6", "G7",
                "101", "102", "103", "104", "105", "106", "107",
                "201", "202", "203", "204", "205", "206", "207",
                "301", "302", "303", "304", "305", "306", "307",
                "401", "402", "403", "404", "405", "406", "407"
            )
            if (!validHouseNumbers.contains(userhouseNumber)) {
                Toast.makeText(this, "Invalid house number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Email validation
            if (!useremail.endsWith("@gmail.com")) {
                Toast.makeText(this, "Email must end with @gmail.com", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save user data to Firebase using sanitized email as the key
            val user = User(username, useremail, userpassword, userhouseNumber)
            databaseReference.child(useremail.replace(".", ",")).setValue(user).addOnSuccessListener {
                Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show()
                name.text?.clear()
                email.text?.clear()
                password.text?.clear()
                houseNumber.text?.clear()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to register user", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle "Already Registered? Sign In" click
        binding.textViewalready.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
    }
}

// User Data Class
data class User(
    val name: String,
    val email: String,
    val password: String,
    val houseNumber: String
)