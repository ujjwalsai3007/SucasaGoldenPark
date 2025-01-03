package com.example.prestige

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ResidentsActivityFragment : Fragment() {

    private lateinit var eventsRecyclerView: RecyclerView
    private lateinit var databaseReferenceEvents: DatabaseReference
    private lateinit var databaseReferenceMaintenance: DatabaseReference
    private val eventsList = mutableListOf<Event>()
    private lateinit var maintenanceStatusTextView: TextView
    private lateinit var houseNumber: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_residents_activity, container, false)

        // Initialize UI components
        eventsRecyclerView = view.findViewById(R.id.eventsRecyclerView)

        eventsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Retrieve house number from shared preferences
        val sharedPreferences: SharedPreferences = requireContext().getSharedPreferences("UserPrefs", 0)
        houseNumber = sharedPreferences.getString("houseNumber", "Unknown") ?: "Unknown"

        // Setup Events RecyclerView
        setupEventsRecyclerView()

        // Fetch Maintenance Statu

        return view
    }

    private fun setupEventsRecyclerView() {
        val adapter = EventsAdapter(eventsList, false) { /* No delete functionality */ }
        eventsRecyclerView.adapter = adapter

        databaseReferenceEvents = FirebaseDatabase.getInstance().getReference("Events")
        databaseReferenceEvents.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                eventsList.clear()
                for (eventSnapshot in snapshot.children) {
                    val event = eventSnapshot.getValue(Event::class.java)
                    if (event != null) {
                        eventsList.add(event)
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load events: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }}