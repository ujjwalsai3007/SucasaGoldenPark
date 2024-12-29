package com.example.prestige

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prestige.databinding.ActivitySecurityGuardScreenBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SecurityGuardScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySecurityGuardScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySecurityGuardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Firebase references
        val securityGuardReference = FirebaseDatabase.getInstance().getReference("SecurityGuardStatus")
        val cleaningStatusReference = FirebaseDatabase.getInstance().getReference("ApartmentCleaningStatus")

        // Handle Security Guard Availability Switch
        setupAvailabilitySwitch(securityGuardReference)

        // Handle Apartment Cleaning Status Switch
        setupCleaningStatusSwitch(cleaningStatusReference)

        // Add Order Button
        binding.addOrderButton.setOnClickListener {
            val intent = Intent(this, AddOrderActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupAvailabilitySwitch(databaseReference: DatabaseReference) {
        val switch = binding.availabilitySwitch

        // Fetch initial availability status
        databaseReference.child("isAvailable").get().addOnSuccessListener { snapshot ->
            val isAvailable = snapshot.getValue(Boolean::class.java) ?: false
            switch.isChecked = isAvailable
        }

        // Update availability status on change
        switch.setOnCheckedChangeListener { _, isChecked ->
            databaseReference.child("isAvailable").setValue(isChecked).addOnSuccessListener {
                val message = if (isChecked) "Marked as Available" else "Marked as Unavailable"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupCleaningStatusSwitch(databaseReference: DatabaseReference) {
        val switch = binding.availabilitySwitch1

        // Fetch initial cleaning status
        databaseReference.child("isCleaned").get().addOnSuccessListener { snapshot ->
            val isCleaned = snapshot.getValue(Boolean::class.java) ?: false
            switch.isChecked = isCleaned
        }

        // Update cleaning status on change
        switch.setOnCheckedChangeListener { _, isChecked ->
            databaseReference.child("isCleaned").setValue(isChecked).addOnSuccessListener {
                val message = if (isChecked) "Marked as Cleaned" else "Marked as Uncleaned"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
        }
    }
}