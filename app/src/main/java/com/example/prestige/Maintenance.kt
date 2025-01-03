package com.example.prestige

data class Maintenance(
    val houseNumber: String, // House number (e.g., G1, 101, etc.)
    var status: String       // Status (e.g., "Paid" or "Not Paid")
)