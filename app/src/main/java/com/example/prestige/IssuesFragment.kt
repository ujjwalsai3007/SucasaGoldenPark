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

    private lateinit var issuesRecyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private val issuesList = mutableListOf<Issue>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_issues, container, false)

        issuesRecyclerView = view.findViewById(R.id.issuesRecyclerView)
        issuesRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val adapter = IssuesAdapter(issuesList)
        issuesRecyclerView.adapter = adapter

        databaseReference = FirebaseDatabase.getInstance().getReference("Issues")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                issuesList.clear()
                for (issueSnapshot in snapshot.children) {
                    val issue = issueSnapshot.getValue(Issue::class.java)
                    if (issue != null) {
                        issuesList.add(issue)
                    }
                }
                issuesList.reverse()
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load issues: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })

        return view
    }
}