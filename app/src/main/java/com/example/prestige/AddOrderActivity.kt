package com.example.prestige

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
class AddOrderActivity : AppCompatActivity() {
    private lateinit var houseNumberInput: TextInputEditText
    private lateinit var orderDetailsInput: TextInputEditText
    private lateinit var submitOrderButton: Button
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_order)
        databaseReference = FirebaseDatabase.getInstance().getReference("Orders")
        houseNumberInput = findViewById(R.id.houseNumberInput)
        orderDetailsInput = findViewById(R.id.orderDetailsInput)
        submitOrderButton = findViewById(R.id.submitOrderButton)
        submitOrderButton.setOnClickListener {


            val houseNumber = houseNumberInput.text.toString().trim()
            val orderDetails = orderDetailsInput.text.toString().trim()
            if (houseNumber.isEmpty() || orderDetails.isEmpty()) {
                Toast.makeText(this, "please fill all the details", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val orderId = databaseReference.push().key ?: return@setOnClickListener
            val order = Order(houseNumber, orderDetails)
            databaseReference.child(orderId).setValue(order).addOnSuccessListener {
                Toast.makeText(this, "Order Placed Successfully", Toast.LENGTH_SHORT).show()
                houseNumberInput.text?.clear()
                orderDetailsInput.text?.clear()
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
            }

        }
    }
}
data class Order(
    val houseNumber: String="",
    val orderDetails: String=""
)

