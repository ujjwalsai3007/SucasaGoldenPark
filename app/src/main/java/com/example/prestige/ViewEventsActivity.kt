package com.example.prestige

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prestige.databinding.ActivityViewEventsBinding
import com.google.firebase.database.*

class ViewEventsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityViewEventsBinding
    private lateinit var databaseReference: DatabaseReference
    private val eventsList = mutableListOf<Event>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewEventsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView Setup
        binding.eventsRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = EventsAdapter(eventsList, showDeleteButton = false) {}
        binding.eventsRecyclerView.adapter = adapter

        // Fetch Events
        fetchEvents(adapter)
    }

    private fun fetchEvents(adapter: EventsAdapter) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Events")
        databaseReference.addValueEventListener(object : ValueEventListener {
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
                Toast.makeText(this@ViewEventsActivity, "Failed to fetch events", Toast.LENGTH_SHORT).show()
            }
        })
    }
}