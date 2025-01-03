package com.example.prestige

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MaintenanceAdapter(
    private val maintenanceList: List<Maintenance>
) : RecyclerView.Adapter<MaintenanceAdapter.MaintenanceViewHolder>() {

    inner class MaintenanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val houseNumberTextView: TextView = itemView.findViewById(R.id.houseNumberTextView)
        val statusTextView: TextView = itemView.findViewById(R.id.statusTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_maintenance, parent, false)
        return MaintenanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaintenanceViewHolder, position: Int) {
        val maintenance = maintenanceList[position]
        holder.houseNumberTextView.text = maintenance.houseNumber
        holder.statusTextView.text = maintenance.status
    }

    override fun getItemCount(): Int = maintenanceList.size
}