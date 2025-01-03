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

        // Button Listeners
        setupButtonListeners()
    }

    private fun setupAvailabilitySwitch(databaseReference: DatabaseReference) {
        val switch = binding.availabilitySwitch

        // Fetch initial availability status
        databaseReference.child("isAvailable").get()
            .addOnSuccessListener { snapshot ->
                val isAvailable = snapshot.getValue(Boolean::class.java) ?: false
                switch.isChecked = isAvailable
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch availability status", Toast.LENGTH_SHORT).show()
            }

        // Update availability status on change
        switch.setOnCheckedChangeListener { _, isChecked ->
            databaseReference.child("isAvailable").setValue(isChecked)
                .addOnSuccessListener {
                    val message = if (isChecked) "Marked as Available" else "Marked as Unavailable"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupCleaningStatusSwitch(databaseReference: DatabaseReference) {
        val switch = binding.cleaningStatusSwitch

        // Fetch initial cleaning status
        databaseReference.child("isCleaned").get()
            .addOnSuccessListener { snapshot ->
                val isCleaned = snapshot.getValue(Boolean::class.java) ?: false
                switch.isChecked = isCleaned
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to fetch cleaning status", Toast.LENGTH_SHORT).show()
            }

        // Update cleaning status on change
        switch.setOnCheckedChangeListener { _, isChecked ->
            databaseReference.child("isCleaned").setValue(isChecked)
                .addOnSuccessListener {
                    val message = if (isChecked) "Marked as Cleaned" else "Marked as Not Cleaned"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { exception ->
                    Toast.makeText(this, "Error: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupButtonListeners() {
        // Add Order Button
        binding.addOrderButton.setOnClickListener {
            val intent = Intent(this, AddOrderActivity::class.java)
            startActivity(intent)
        }

        // Raise Issue Button
        binding.raiseIssueButton.setOnClickListener {
            val intent = Intent(this, RaiseIssueactivity::class.java)
            startActivity(intent)
        }

        // View Issues Button
        binding.viewIssuesButton.setOnClickListener {
            val intent = Intent(this, ViewIssuesActivity::class.java)
            startActivity(intent)
        }

        // View Events Button
        binding.viewEventsButton.setOnClickListener {
            val intent = Intent(this, ViewEventsActivity::class.java)
            startActivity(intent)
        }

        // Update Maintenance Button
        binding.updateMaintenanceButton.setOnClickListener {
            val intent = Intent(this, UpdateMaintenanceActivity::class.java)
            startActivity(intent)
        }

        // Logout Button
        binding.logoutOption.setOnClickListener {
            val intent = Intent(this, SignIn::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish() // Close the current activity
        }
    }
}