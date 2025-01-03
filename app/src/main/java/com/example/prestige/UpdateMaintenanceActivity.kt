package com.example.prestige

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prestige.databinding.ActivityUpdateMaintenanceBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class UpdateMaintenanceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdateMaintenanceBinding
    private lateinit var databaseReference: DatabaseReference
    private val houseList = mutableListOf<Maintenance>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUpdateMaintenanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        databaseReference = FirebaseDatabase.getInstance().getReference("Maintenance")

        binding.maintenanceRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = MaintenanceEditAdapter(houseList) { houseNumber, status ->
            updateMaintenanceStatus(houseNumber, status)
        }
        binding.maintenanceRecyclerView.adapter = adapter

        // Fetch data from Firebase
        fetchMaintenanceData(adapter)
    }

    private fun fetchMaintenanceData(adapter: MaintenanceEditAdapter) {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                houseList.clear() // Clear the list to avoid duplication
                populateHouseList() // Populate default house list

                // Update house list with Firebase data
                for (child in snapshot.children) {
                    val houseNumber = child.key ?: continue
                    val status = child.child("status").getValue(String::class.java) ?: "Not Paid"

                    // Find the house in the list and update its status
                    houseList.find { it.houseNumber == houseNumber }?.status = status
                }

                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@UpdateMaintenanceActivity, "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun populateHouseList() {
        // Add house numbers G1 to G7
        for (i in 1..7) {
            houseList.add(Maintenance("G$i", "Not Paid"))
        }

        // Add house numbers 101–107
        for (i in 101..107) {
            houseList.add(Maintenance(i.toString(), "Not Paid"))
        }

        // Add house numbers 201–207
        for (i in 201..207) {
            houseList.add(Maintenance(i.toString(), "Not Paid"))
        }

        // Add house numbers 301–307
        for (i in 301..307) {
            houseList.add(Maintenance(i.toString(), "Not Paid"))
        }

        // Add house numbers 401–407
        for (i in 401..407) {
            houseList.add(Maintenance(i.toString(), "Not Paid"))
        }
    }

    private fun updateMaintenanceStatus(houseNumber: String, status: String) {
        databaseReference.child(houseNumber).setValue(Maintenance(houseNumber, status))
            .addOnSuccessListener {
                Toast.makeText(this, "Status updated successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update status", Toast.LENGTH_SHORT).show()
            }
    }
}