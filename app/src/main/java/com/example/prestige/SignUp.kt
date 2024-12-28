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

        // Firebase Database setup
        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        // Restrict username to alphabets only
        binding.name.filters = arrayOf(InputFilter { source, _, _, _, _, _ ->
            if (source.matches(Regex("^[a-zA-Z]+$"))) source else ""
        })

        binding.signup.setOnClickListener {
            val name = binding.name.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val houseNumber = binding.houseNumber.text.toString().trim()
            val role = binding.roleSpinner.selectedItem.toString()

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || houseNumber.isEmpty() || role == "Select Role") {
                Toast.makeText(this, "Please fill all the fields and select a valid role", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!email.matches(Regex("^[a-zA-Z].*"))) {
                Toast.makeText(this, "Email must start with an alphabet", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!email.endsWith("@gmail.com")) {
                Toast.makeText(this, "Email must end with @gmail.com", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val validHouseNumbers = listOf(
                "G1", "G2", "G3", "G4", "G5", "G6", "G7",
                "101", "102", "103", "104", "105", "106", "107",
                "201", "202", "203", "204", "205", "206", "207",
                "301", "302", "303", "304", "305", "306", "307",
                "401", "402", "403", "404", "405", "406", "407"
            )
            if (!validHouseNumbers.contains(houseNumber)) {
                Toast.makeText(this, "Invalid house number", Toast.LENGTH_SHORT).show()
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
            val intent = Intent(this, SignIn::class.java)
            startActivity(intent)
        }
    }
}

data class User(
    val name: String,
    val email: String,
    val password: String,
    val houseNumber: String,
    val role: String
)