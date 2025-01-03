package com.example.prestige

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prestige.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class SignUp : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

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

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid
                        val user = User(name, email, houseNumber, role)

                        userId?.let {
                            val databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                            databaseReference.child(it).setValue(user).addOnSuccessListener {
                                Toast.makeText(this, "User Registered Successfully", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this, SignIn::class.java))
                                finish()
                            }.addOnFailureListener {
                                Toast.makeText(this, "Failed to save user in database", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Sign-up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.textViewAlready.setOnClickListener {
            startActivity(Intent(this, SignIn::class.java))
        }
    }
}

data class User(
    val name: String,
    val email: String,
    val houseNumber: String,
    val role: String
)