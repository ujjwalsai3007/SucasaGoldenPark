package com.example.prestige

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IssuesAdapter(
    private val issuesList: List<Issue>
) : RecyclerView.Adapter<IssuesAdapter.IssueViewHolder>() {

    inner class IssueViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleText)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionText)
        val dateTextView: TextView = itemView.findViewById(R.id.dateText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IssueViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_issue, parent, false)
        return IssueViewHolder(view)
    }

    override fun getItemCount(): Int = issuesList.size

    override fun onBindViewHolder(holder: IssueViewHolder, position: Int) {
        val issue = issuesList[position]
        holder.titleTextView.text = "Title: ${issue.title}"
        holder.descriptionTextView.text = "Description: ${issue.description}"
        holder.dateTextView.text = "Date: ${issue.date}"
    }
}