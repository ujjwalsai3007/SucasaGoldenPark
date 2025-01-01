package com.example.prestige

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class ViewIssuesActivity : AppCompatActivity() {

    private lateinit var issuesRecyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private val issuesList = mutableListOf<Issue>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_issues)

        issuesRecyclerView = findViewById(R.id.issuesRecyclerView)
        issuesRecyclerView.layoutManager = LinearLayoutManager(this)

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
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ViewIssuesActivity, "Failed to load issues: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}