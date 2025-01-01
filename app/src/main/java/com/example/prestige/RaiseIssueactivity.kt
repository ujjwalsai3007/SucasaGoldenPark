package com.example.prestige

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.Calendar

class RaiseIssueactivity : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var dateTextView: TextView
    private lateinit var selectDateButton: Button
    private lateinit var submitButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_raise_issue) // Ensure this XML file is correct

        titleInput = findViewById(R.id.issueTitleInput)
        descriptionInput = findViewById(R.id.issueDescriptionInput)
        dateTextView = findViewById(R.id.issueDateTextView)
        selectDateButton = findViewById(R.id.selectDateButton)
        submitButton = findViewById(R.id.submitIssueButton)

        databaseReference = FirebaseDatabase.getInstance().getReference("Issues")

        // Set up the DatePickerDialog
        selectDateButton.setOnClickListener {
            showDatePicker()
        }

        submitButton.setOnClickListener {
            submitIssue()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "${selectedDay.toString().padStart(2, '0')}/" +
                    "${(selectedMonth + 1).toString().padStart(2, '0')}/" +
                    selectedYear
            dateTextView.text = formattedDate
        }, year, month, day)

        datePickerDialog.show()
    }

    private fun submitIssue() {
        val title = titleInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        val date = dateTextView.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || date.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            return
        }

        val issueId = databaseReference.push().key ?: return
        val issue = Issue(issueId, title, description, date)

        databaseReference.child(issueId).setValue(issue).addOnSuccessListener {
            Toast.makeText(this, "Issue raised successfully", Toast.LENGTH_SHORT).show()
            titleInput.text.clear()
            descriptionInput.text.clear()
            dateTextView.text = ""
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to raise issue", Toast.LENGTH_SHORT).show()
        }
    }
}