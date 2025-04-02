package com.example.prestige

data class Maintenance(
    val houseNumber: String? = null, // House number (e.g., G1, 101, etc.)
    var status: String? = null       // Status (e.g., "Paid" or "Not Paid")
) {
    // No-argument constructor required by Firebase
    constructor() : this(null, null)
}
