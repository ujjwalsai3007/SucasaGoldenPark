package com.example.prestige

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MaintenanceEditAdapter(
    private val houseList: List<Maintenance>,
    private val onSaveClick: (String, String) -> Unit
) : RecyclerView.Adapter<MaintenanceEditAdapter.MaintenanceViewHolder>() {

    inner class MaintenanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val houseNumberTextView: TextView = itemView.findViewById(R.id.houseNumberTextView)
        val statusEditText: EditText = itemView.findViewById(R.id.statusEditText)
        val saveButton: Button = itemView.findViewById(R.id.saveButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MaintenanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_maintenance_edit, parent, false)
        return MaintenanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: MaintenanceViewHolder, position: Int) {
        val house = houseList[position]
        holder.houseNumberTextView.text = house.houseNumber ?: "Unknown"
        holder.statusEditText.setText(house.status ?: "")

        holder.saveButton.setOnClickListener {
            val status = holder.statusEditText.text.toString().trim()
            val houseNumber = house.houseNumber ?: return@setOnClickListener
            onSaveClick(houseNumber, status)
        }
    }

    override fun getItemCount(): Int = houseList.size
}