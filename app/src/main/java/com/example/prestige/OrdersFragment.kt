package com.example.prestige

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class OrdersFragment : Fragment() {

    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var ordersAdapter: OrdersAdapter
    private lateinit var ordersList: MutableList<Orderr>
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var houseNumber: String
    private lateinit var auth: FirebaseAuth
    private val TAG = "OrdersFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

        // Initialize Authentication
        auth = FirebaseAuth.getInstance()
        
        // Check if user is logged in
        if (auth.currentUser == null) {
            Log.e(TAG, "User is not authenticated")
            Toast.makeText(requireContext(), "Please log in again", Toast.LENGTH_SHORT).show()
            return view
        }

        // Initialize RecyclerView
        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        ordersList = mutableListOf()
        ordersAdapter = OrdersAdapter(ordersList)
        ordersRecyclerView.adapter = ordersAdapter

        // Initialize SharedPreferences to get the logged-in user's house number
        sharedPreferences =
            requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        houseNumber = sharedPreferences.getString("houseNumber", null) ?: ""

        if (houseNumber.isEmpty()) {
            Toast.makeText(
                requireContext(),
                "Failed to retrieve user data. Please log in again.",
                Toast.LENGTH_SHORT
            ).show()
            return view
        }

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders")
        Log.d(TAG, "Using authentication token: ${auth.currentUser?.uid}")
        
        // Fetch orders for the current user's house number
        fetchOrdersForHouse()

        return view
    }

    private fun fetchOrdersForHouse() {
        if (auth.currentUser == null) {
            Log.e(TAG, "Cannot fetch orders: User not authenticated")
            Toast.makeText(requireContext(), "Authentication error. Please log in again.", Toast.LENGTH_SHORT).show()
            return
        }
        
        Log.d(TAG, "Fetching orders for house: $houseNumber with auth: ${auth.currentUser?.uid}")
        
        databaseReference.orderByChild("houseNumber").equalTo(houseNumber)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    ordersList.clear() // Clear the list before adding new data
                    if (snapshot.exists()) {
                        for (orderSnapshot in snapshot.children) {
                            val order = orderSnapshot.getValue(Orderr::class.java)
                            order?.let { ordersList.add(it) }
                        }
                        ordersAdapter.notifyDataSetChanged()
                        Log.d(TAG, "Successfully fetched ${ordersList.size} orders")
                    } else {
                        Log.d(TAG, "No orders found for house: $houseNumber")
                        Toast.makeText(
                            requireContext(),
                            "No orders found for your house.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "Failed to fetch orders: ${error.message}")
                    Toast.makeText(
                        requireContext(),
                        "Failed to fetch orders: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}