package com.example.prestige

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class Orderr(
    val houseNumber: String = "",
    val orderDetails: String = ""
)

class OrdersAdapter(
    private val ordersList: List<Orderr>
) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderDetailsTextView: TextView = view.findViewById(R.id.orderDetailsText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = ordersList[position]
        holder.orderDetailsTextView.text = order.orderDetails
    }

    override fun getItemCount(): Int = ordersList.size
}