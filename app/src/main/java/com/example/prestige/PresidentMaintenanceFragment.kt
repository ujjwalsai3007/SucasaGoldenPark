package com.example.prestige

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.example.prestige.databinding.FragmentPresidentMaintenanceBinding

class PresidentMaintenanceFragment : Fragment() {

    private lateinit var binding: FragmentPresidentMaintenanceBinding
    private lateinit var databaseReference: DatabaseReference
    private val maintenanceList = mutableListOf<Maintenance>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPresidentMaintenanceBinding.inflate(inflater, container, false)

        databaseReference = FirebaseDatabase.getInstance().getReference("Maintenance")

        binding.maintenanceRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = MaintenanceAdapter(maintenanceList)
        binding.maintenanceRecyclerView.adapter = adapter

        fetchMaintenanceData(adapter)

        return binding.root
    }

    private fun fetchMaintenanceData(adapter: MaintenanceAdapter) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                maintenanceList.clear()
                for (child in snapshot.children) {
                    val maintenance = child.getValue(Maintenance::class.java)
                    if (maintenance != null) {
                        maintenanceList.add(maintenance)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}