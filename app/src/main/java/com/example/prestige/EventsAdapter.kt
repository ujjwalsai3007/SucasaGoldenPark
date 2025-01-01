package com.example.prestige

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class EventsAdapter(
    private val eventsList: List<Event>,
    private val showDeleteButton: Boolean,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<EventsAdapter.EventViewHolder>() {

    inner class EventViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val eventNameTextView: TextView = itemView.findViewById(R.id.eventNameText)
        val eventDateTextView: TextView = itemView.findViewById(R.id.eventDateText)
        val eventDescriptionTextView: TextView = itemView.findViewById(R.id.eventDescriptionText)
        val deleteButton: Button = itemView.findViewById(R.id.deleteEventButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = eventsList[position]
        holder.eventNameTextView.text = "Name: ${event.eventName}"
        holder.eventDateTextView.text = "Date: ${event.eventDate}"
        holder.eventDescriptionTextView.text = "Description: ${event.eventDescription}"

        if (showDeleteButton) {
            holder.deleteButton.visibility = View.VISIBLE
            holder.deleteButton.setOnClickListener {
                onDeleteClick(event.eventId)
            }
        } else {
            holder.deleteButton.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = eventsList.size
}