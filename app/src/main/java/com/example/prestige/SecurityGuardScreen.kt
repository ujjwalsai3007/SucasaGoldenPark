package com.example.prestige

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.FirebaseDatabase
import com.example.prestige.databinding.ActivitySecurityGuardScreenBinding

class SecurityGuardScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySecurityGuardScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySecurityGuardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val switch = binding.availabilitySwitch
        val databaseReference = FirebaseDatabase.getInstance().getReference("SecurityGuardStatus")
        databaseReference.child("isAvailable").get().addOnSuccessListener { snapshot ->
            val isAvailable = snapshot.getValue(Boolean::class.java) ?: false
            switch.isChecked = isAvailable
        }
        switch.setOnCheckedChangeListener { _, isChecked ->
            databaseReference.child("isAvailable").setValue(isChecked).addOnSuccessListener {
                val message = if (isChecked) "Marked as Available" else "Marked as Unavailable"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addOrderButton.setOnClickListener {
            val intent = Intent(this, AddOrderActivity::class.java)
            startActivity(intent)
        }
    }
}