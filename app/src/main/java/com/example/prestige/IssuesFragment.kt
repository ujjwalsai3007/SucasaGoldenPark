package com.example.prestige

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class IssuesFragment : Fragment() {
    private lateinit var databaseReference: DatabaseReference
    private lateinit var issuesRecyclerView: RecyclerView
    private lateinit var issuesAdapter: IssuesAdapter
    private val issuesList = mutableListOf<Issue1>()
    private var houseNumber: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_issues, container, false)

        // Initialize RecyclerView
        issuesRecyclerView = view.findViewById(R.id.issuesRecyclerView)
        issuesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Fetch the user's house number from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("MyPrefs", 0)
        houseNumber = sharedPreferences.getString("houseNumber", "") ?: ""

        // Initialize Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Issues")

        // Initialize Adapter
        issuesAdapter = IssuesAdapter(issuesList) { issueId ->
            deleteIssue(issueId)
        }
        issuesRecyclerView.adapter = issuesAdapter

        // Fetch issues from Firebase
        fetchIssues()

        return view
    }

    private fun fetchIssues() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                issuesList.clear()
                for (issueSnapshot in snapshot.children) {
                    val issue = issueSnapshot.getValue(Issue1::class.java)
                    if (issue != null) {
                        issuesList.add(issue)
                    }
                }
                issuesAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load issues: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun deleteIssue(issueId: String) {
        databaseReference.child(issueId).get().addOnSuccessListener { snapshot ->
            val issue = snapshot.getValue(Issue1::class.java)
            if (issue != null && issue.houseNumber == houseNumber) {
                databaseReference.child(issueId).removeValue().addOnSuccessListener {
                    Toast.makeText(requireContext(), "Issue deleted successfully", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to delete issue", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "You can only delete your own issues", Toast.LENGTH_SHORT).show()
            }
        }
    }
}