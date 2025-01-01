package com.example.prestige

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddEventFragment : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var eventNameInput: EditText
    private lateinit var eventDateInput: EditText
    private lateinit var eventDescriptionInput: EditText
    private lateinit var submitEventButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_event, container, false)

        // Initialize views
        eventNameInput = view.findViewById(R.id.eventNameInput)
        eventDateInput = view.findViewById(R.id.eventDateInput)
        eventDescriptionInput = view.findViewById(R.id.eventDescriptionInput)
        submitEventButton = view.findViewById(R.id.submitEventButton)

        // Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Events")

        submitEventButton.setOnClickListener {
            addEventToDatabase()
        }

        return view
    }

    private fun addEventToDatabase() {
        val eventName = eventNameInput.text.toString().trim()
        val eventDate = eventDateInput.text.toString().trim()
        val eventDescription = eventDescriptionInput.text.toString().trim()

        if (eventName.isEmpty() || eventDate.isEmpty() || eventDescription.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val eventId = databaseReference.push().key ?: return
        val event = Event(eventId, eventName, eventDate, eventDescription)

        databaseReference.child(eventId).setValue(event).addOnSuccessListener {
            Toast.makeText(requireContext(), "Event scheduled successfully", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to schedule event", Toast.LENGTH_SHORT).show()
        }
    }
}