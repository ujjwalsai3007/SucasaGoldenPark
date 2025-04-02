package com.example.prestige

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class RaiseIssueFragment : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var issueTitleInput: EditText
    private lateinit var issueDescriptionInput: EditText
    private lateinit var issueDateInput: TextView
    private lateinit var submitIssueButton: Button
    private lateinit var auth: FirebaseAuth
    private val TAG = "RaiseIssueFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_raise_issue, container, false)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        
        // Check if user is logged in
        if (auth.currentUser == null) {
            Log.e(TAG, "User is not authenticated")
            Toast.makeText(requireContext(), "Please log in again", Toast.LENGTH_SHORT).show()
            return view
        }

        issueTitleInput = view.findViewById(R.id.issueTitleInput)
        issueDescriptionInput = view.findViewById(R.id.issueDescriptionInput)
        issueDateInput = view.findViewById(R.id.issueDateInput)
        submitIssueButton = view.findViewById(R.id.submitIssueButton)

        databaseReference = FirebaseDatabase.getInstance().getReference("Issues")
        Log.d(TAG, "Using authentication token: ${auth.currentUser?.uid}")

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
        // Verify user is authenticated before submitting
        if (auth.currentUser == null) {
            Log.e(TAG, "Cannot submit issue: User not authenticated")
            Toast.makeText(requireContext(), "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }
        
        val title = issueTitleInput.text.toString().trim()
        val description = issueDescriptionInput.text.toString().trim()
        val date = issueDateInput.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {
            Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d(TAG, "Attempting to submit issue with auth: ${auth.currentUser?.uid}")
        
        // Get user details from shared preferences
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", android.content.Context.MODE_PRIVATE)
        val userId = auth.currentUser?.uid ?: ""
        val houseNumber = sharedPreferences.getString("houseNumber", "Unknown")
        
        val issueId = databaseReference.push().key ?: return
        val issue = Issue(issueId, title, description, date, houseNumber ?: "Unknown", userId)

        databaseReference.child(issueId).setValue(issue).addOnSuccessListener {
            Log.d(TAG, "Issue submitted successfully")
            Toast.makeText(requireContext(), "Issue raised successfully", Toast.LENGTH_SHORT).show()
            issueTitleInput.text.clear()
            issueDescriptionInput.text.clear()
            issueDateInput.text = ""
        }.addOnFailureListener { e ->
            Log.e(TAG, "Failed to submit issue: ${e.message}")
            Toast.makeText(requireContext(), "Failed to raise issue: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}