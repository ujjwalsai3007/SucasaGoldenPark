package com.example.prestige

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.prestige.databinding.ActivitySecurityGuardScreenBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SecurityGuardScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySecurityGuardScreenBinding
    private lateinit var auth: FirebaseAuth
    private val TAG = "SecurityGuardScreen"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySecurityGuardScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        
        // Check if user is authenticated
        if (auth.currentUser == null) {
            Log.e(TAG, "User is not authenticated, redirecting to login")
            Toast.makeText(this, "Please log in to continue", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignIn::class.java))
            finish()
            return
        }
        
        Log.d(TAG, "User authenticated: ${auth.currentUser?.uid}")

        // Firebase references
        val securityGuardReference = FirebaseDatabase.getInstance().getReference("SecurityGuardStatus")
        val cleaningStatusReference = FirebaseDatabase.getInstance().getReference("ApartmentCleaningStatus")

        // Set up the Security Guard Availability Switch
        setupSecurityGuardSwitch(securityGuardReference)

        // Set up the Apartment Cleaning Status Switch
        setupCleaningStatusSwitch(cleaningStatusReference)

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
            logoutUser()
        }
    }

    private fun setupSecurityGuardSwitch(databaseReference: DatabaseReference) {
        val switch = binding.availabilitySwitch

        // Get initial status
        databaseReference.child("isAvailable").get().addOnSuccessListener { snapshot ->
            val isAvailable = snapshot.getValue(Boolean::class.java) ?: false
            switch.isChecked = isAvailable
        }

        // Update status on change
        switch.setOnCheckedChangeListener { _, isChecked ->
            databaseReference.child("isAvailable").setValue(isChecked)
                .addOnSuccessListener {
                    val message = if (isChecked) "Marked as Available" else "Marked as Unavailable"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun setupCleaningStatusSwitch(databaseReference: DatabaseReference) {
        val switch = binding.availabilitySwitch1

        // Get initial status
        databaseReference.child("isCleaned").get().addOnSuccessListener { snapshot ->
            val isCleaned = snapshot.getValue(Boolean::class.java) ?: false
            switch.isChecked = isCleaned
        }

        // Update status on change
        switch.setOnCheckedChangeListener { _, isChecked ->
            databaseReference.child("isCleaned").setValue(isChecked)
                .addOnSuccessListener {
                    val message = if (isChecked) "Marked as Cleaned" else "Marked as Uncleaned"
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun logoutUser() {
        auth.signOut()
        Log.d(TAG, "User logged out")
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, SignIn::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
    
    override fun onResume() {
        super.onResume()
        // Check authentication again when screen resumes
        if (auth.currentUser == null) {
            Log.e(TAG, "User authentication lost, redirecting to login")
            Toast.makeText(this, "Session expired. Please log in again", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }
    }
}