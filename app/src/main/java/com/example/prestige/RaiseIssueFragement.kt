package com.example.prestige

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

data class Issue(
    val houseNumber: String = "",
    val title: String = "",
    val description: String = ""

)

class RaiseIssueFragment : Fragment() {

    private lateinit var issueTitleInput: EditText
    private lateinit var issueDescriptionInput: EditText
    private lateinit var prioritySpinner: Spinner
    private lateinit var submitIssueButton: Button
    private lateinit var databaseReference: DatabaseReference
    private var houseNumber: String = "President"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_raise_issue, container, false)

        // Initialize Views
        issueTitleInput = view.findViewById(R.id.issueTitleInput)
        issueDescriptionInput = view.findViewById(R.id.issueDescriptionInput)
        prioritySpinner = view.findViewById(R.id.prioritySpinner)
        submitIssueButton = view.findViewById(R.id.submitIssueButton)

        // Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Issues")

        // Handle Submit Button Click
        submitIssueButton.setOnClickListener {
            submitIssue()
        }

        return view
    }

    private fun submitIssue() {
        val title = issueTitleInput.text.toString().trim()
        val description = issueDescriptionInput.text.toString().trim()
        val priority = prioritySpinner.selectedItem.toString()

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val issueId = databaseReference.push().key ?: return
        val issue = Issue1(issueId, houseNumber, title, description, priority)

        databaseReference.child(issueId).setValue(issue).addOnSuccessListener {
            Toast.makeText(requireContext(), "Issue raised successfully", Toast.LENGTH_SHORT).show()
            issueTitleInput.text.clear()
            issueDescriptionInput.text.clear()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Failed to raise issue", Toast.LENGTH_SHORT).show()
        }
    }
}