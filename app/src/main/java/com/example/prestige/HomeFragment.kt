package com.example.prestige

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.example.prestige.RaiseIssueFragment

class HomeFragment : Fragment() {

    private lateinit var securityGuardRef: DatabaseReference
    private lateinit var cleaningStatusRef: DatabaseReference
    private lateinit var availabilityTextView: TextView
    private lateinit var apartmentcleanedTextView: TextView
    private lateinit var auth: FirebaseAuth
    private val TAG = "HomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_frgement, container, false)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()

        // Check if user is authenticated
        if (auth.currentUser == null) {
            Log.e(TAG, "User is not authenticated")
            Toast.makeText(requireContext(), "Please login again", Toast.LENGTH_SHORT).show()
            return view
        }

        // Initialize Views
        availabilityTextView = view.findViewById(R.id.securityGuardAvailability)
        apartmentcleanedTextView = view.findViewById(R.id.apartmentcleaned)

        val ordersButton = view.findViewById<Button>(R.id.ordersButton)
        val raiseIssueButton = view.findViewById<Button>(R.id.raiseIssueButton)
        val viewIssuesButton = view.findViewById<Button>(R.id.viewIssuesButton)

        // Initialize Firebase references
        securityGuardRef = FirebaseDatabase.getInstance().getReference("SecurityGuardStatus")
        cleaningStatusRef = FirebaseDatabase.getInstance().getReference("ApartmentCleaningStatus")

        // Fetch Data
        fetchSecurityGuardAvailability()
        fetchApartmentCleaningStatus()

        // Navigate to "View Orders" screen
        ordersButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OrdersFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to "Raise an Issue" screen
        raiseIssueButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, RaiseIssueFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigate to "View Issues" screen
        viewIssuesButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, IssuesFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun fetchApartmentCleaningStatus() {
        if (auth.currentUser == null) {
            Log.e(TAG, "Cannot fetch apartment cleaning status: User not authenticated")
            apartmentcleanedTextView.text = "Authentication Error"
            return
        }

        Log.d(TAG, "Fetching apartment cleaning status with auth token: ${auth.currentUser?.uid}")
        cleaningStatusRef.child("isCleaned").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val isCleaned = snapshot.getValue(Boolean::class.java) ?: false
                    if (isCleaned) {
                        apartmentcleanedTextView.text = "Cleaned"
                        apartmentcleanedTextView.setTextColor(
                            resources.getColor(android.R.color.holo_green_dark, null)
                        )
                    } else {
                        apartmentcleanedTextView.text = "Uncleaned"
                        apartmentcleanedTextView.setTextColor(
                            resources.getColor(android.R.color.holo_red_dark, null)
                        )
                    }
                } else {
                    Log.d(TAG, "Apartment cleaning data doesn't exist")
                    apartmentcleanedTextView.text = "No Data"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to fetch apartment cleaning data: ${error.message}")
                Toast.makeText(
                    requireContext(),
                    "Failed to fetch data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun fetchSecurityGuardAvailability() {
        if (auth.currentUser == null) {
            Log.e(TAG, "Cannot fetch security guard status: User not authenticated")
            availabilityTextView.text = "Authentication Error"
            return
        }

        Log.d(TAG, "Fetching security guard status with auth token: ${auth.currentUser?.uid}")
        securityGuardRef.child("isAvailable").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val isAvailable = snapshot.getValue(Boolean::class.java) ?: false
                    if (isAvailable) {
                        availabilityTextView.text = "Available"
                        availabilityTextView.setTextColor(
                            resources.getColor(android.R.color.holo_green_dark, null)
                        )
                    } else {
                        availabilityTextView.text = "Not Available"
                        availabilityTextView.setTextColor(
                            resources.getColor(android.R.color.holo_red_dark, null)
                        )
                    }
                } else {
                    Log.d(TAG, "Security guard data doesn't exist")
                    availabilityTextView.text = "No Data"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to fetch security guard data: ${error.message}")
                Toast.makeText(
                    requireContext(),
                    "Failed to fetch data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}