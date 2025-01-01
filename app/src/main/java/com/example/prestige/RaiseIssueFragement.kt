package com.example.prestige

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RaiseIssueFragment : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var issueTitleInput: EditText
    private lateinit var issueDescriptionInput: EditText
    private lateinit var issueDateInput: TextView
    private lateinit var submitIssueButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_raise_issue, container, false)

        issueTitleInput = view.findViewById(R.id.issueTitleInput)
        issueDescriptionInput = view.findViewById(R.id.issueDescriptionInput)
        issueDateInput = view.findViewById(R.id.issueDateInput)
        submitIssueButton = view.findViewById(R.id.submitIssueButton)

        databaseReference = FirebaseDatabase.getInstance().getReference("Issues")

        issueDateInput.setOnClickListener { showDatePicker() }
        submitIssueButton.setOnClickListener { submitIssue() }

        return view
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            issueDateInput.text = formattedDate
        }, year, month, day).show()
    }

    private fun submitIssue() {
        val title = issueTitleInput.text.toString().trim()
        val description = issueDescriptionInput.text.toString().trim()
        val date = issueDateInput.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val issueId = databaseReference.push().key ?: return
        val issue = Issue(issueId, title, description, date)

        databaseReference.child(issueId).setValue(issue).addOnSuccessListener {
            Toast.makeText(requireContext(), "Issue raised successfully", Toast.LENGTH_SHORT).show()
            issueTitleInput.text.clear()
            issueDescriptionInput.text.clear()
            issueDateInput.text = ""
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to raise issue", Toast.LENGTH_SHORT).show()
        }
    }
}