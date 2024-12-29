package com.example.prestige

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var securityGuardReference: DatabaseReference
    private lateinit var cleaningStatusReference: DatabaseReference
    private lateinit var availabilityTextView: TextView
    private lateinit var apartmentCleanedTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_frgement, container, false)

        // Initialize the TextViews
        availabilityTextView = view.findViewById(R.id.securityGuardAvailability)
        apartmentCleanedTextView = view.findViewById(R.id.apartmentcleaned)

        // Initialize Firebase Database references
        securityGuardReference = FirebaseDatabase.getInstance().getReference("SecurityGuardStatus")
        cleaningStatusReference = FirebaseDatabase.getInstance().getReference("ApartmentCleaningStatus")

        // Fetch and update data
        fetchSecurityGuardAvailability()
        fetchApartmentCleaningStatus()

        // Handle orders button click
        val ordersButton = view.findViewById<Button>(R.id.ordersButton)
        ordersButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OrdersFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun fetchApartmentCleaningStatus() {
        cleaningStatusReference.child("isCleaned").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val isCleaned = snapshot.getValue(Boolean::class.java) ?: false
                    if (isCleaned) {
                        apartmentCleanedTextView.text = "Cleaned"
                        apartmentCleanedTextView.setTextColor(
                            resources.getColor(
                                android.R.color.holo_green_dark,
                                null
                            )
                        )
                    } else {
                        apartmentCleanedTextView.text = "Uncleaned"
                        apartmentCleanedTextView.setTextColor(
                            resources.getColor(
                                android.R.color.holo_red_dark,
                                null
                            )
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Failed to fetch data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun fetchSecurityGuardAvailability() {
        securityGuardReference.child("isAvailable").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val isAvailable = snapshot.getValue(Boolean::class.java) ?: false
                    if (isAvailable) {
                        availabilityTextView.text = "Available"
                        availabilityTextView.setTextColor(
                            resources.getColor(
                                android.R.color.holo_green_dark,
                                null
                            )
                        )
                    } else {
                        availabilityTextView.text = "Not Available"
                        availabilityTextView.setTextColor(
                            resources.getColor(
                                android.R.color.holo_red_dark,
                                null
                            )
                        )
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    requireContext(),
                    "Failed to fetch data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}