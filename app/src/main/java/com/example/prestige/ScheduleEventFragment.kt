package com.example.prestige

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.prestige.databinding.FragmentScheduleEventBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class ScheduleEventFragment : Fragment() {

    private lateinit var binding: FragmentScheduleEventBinding
    private lateinit var databaseReference: DatabaseReference
    private var selectedDate: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScheduleEventBinding.inflate(inflater, container, false)

        databaseReference = FirebaseDatabase.getInstance().getReference("Events")

        // Date Picker Dialog
        binding.selectDateButton.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    selectedDate = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                    binding.selectedDateTextView.text = "Selected Date: $selectedDate"
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        // Schedule Event Button
        binding.scheduleEventButton.setOnClickListener {
            val eventName = binding.eventNameInput.text.toString().trim()
            val eventDescription = binding.eventDescriptionInput.text.toString().trim()

            if (eventName.isEmpty() || eventDescription.isEmpty() || selectedDate == null) {
                Toast.makeText(requireContext(), "Please fill all fields and select a date", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val eventId = databaseReference.push().key ?: return@setOnClickListener
            val event = Event(eventId, eventName, eventDescription, selectedDate!!)

            databaseReference.child(eventId).setValue(event).addOnSuccessListener {
                Toast.makeText(requireContext(), "Event Scheduled Successfully", Toast.LENGTH_SHORT).show()
                binding.eventNameInput.text.clear()
                binding.eventDescriptionInput.text.clear()
                binding.selectedDateTextView.text = "No Date Selected"
                selectedDate = null
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to schedule event", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}