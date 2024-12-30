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

class PresidentHomeFragment : Fragment() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var  securityGuardTextView: TextView
    private lateinit var cleaningStatusTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_president_home, container, false)
        securityGuardTextView = view.findViewById(R.id.securityGuardAvailability)
        cleaningStatusTextView = view.findViewById(R.id.apartmentCleaningStatus)
        databaseReference = FirebaseDatabase.getInstance().getReference()

        fetchSecurityGuardAvailability()
        fetchApartmentCleaningStatus()


        val ordersButton = view.findViewById<Button>(R.id.viewOrdersButton)
        ordersButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.president_fragment_container, OrdersFragment())
                .addToBackStack(null)
                .commit()

        }
        return view
    }

    private fun fetchSecurityGuardAvailability() {
        databaseReference.child("SecurityGuardStatus").child("isAvailable")
            .addValueEventListener(object :ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val isAvailable = snapshot.getValue(Boolean::class.java) ?: false
                        securityGuardTextView.text =
                            if (isAvailable) "Available" else "Not available"
                        securityGuardTextView.setTextColor(
                            resources.getColor(
                                if (isAvailable) android.R.color.holo_green_dark else android.R.color.holo_red_dark,
                                null
                            )
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(), "Failed to fetch data", Toast.LENGTH_SHORT)
                        .show()

                }
            })
    }

    private fun fetchApartmentCleaningStatus()
    {
        databaseReference.child("ApartmentCleaningStatus").child("isCleaned")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val isCleaned=snapshot.getValue(Boolean::class.java)?:false
                        cleaningStatusTextView.text=if (isCleaned) "Cleaned" else "Uncleaned"
                        cleaningStatusTextView.setTextColor(
                            resources.getColor(
                                if (isCleaned) android.R.color.holo_green_dark else android.R.color.holo_red_dark,
                                null
                            )
                        )
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(requireContext(),"Failed to fetch data",Toast.LENGTH_SHORT).show()
                }

            })
    }

    }
