package com.example.prestige

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Issue1(
    val issueId: String = "",
    val houseNumber: String = "",
    val title: String = "",
    val description: String = "",
    val priority: String=""
)
class IssuesAdapter(
    private val issuesList: List<Issue1>,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<IssuesAdapter.IssueViewHolder>() {

    inner class IssueViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val houseNumberTextView: TextView = view.findViewById(R.id.houseNumberTextView)
        val titleTextView: TextView = view.findViewById(R.id.issueTitleTextView)
        val descriptionTextView: TextView = view.findViewById(R.id.issueDescriptionTextView)
        val priorityTextView: TextView = view.findViewById(R.id.issueTitleTextView)
        val deleteButton: Button = view.findViewById(R.id.deleteIssueButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_issue, parent, false)
        return IssueViewHolder(view)
    }

    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        val issue = issuesList[position]
        holder.houseNumberTextView.text = "House: ${issue.houseNumber}"
        holder.titleTextView.text = "Title: ${issue.title}"
        holder.descriptionTextView.text = "Description: ${issue.description}"
        holder.priorityTextView.text = "Priority: ${issue.priority}"

        holder.deleteButton.setOnClickListener {
            onDeleteClick(issue.issueId)
        }
    }

    override fun getItemCount(): Int = issuesList.size
}