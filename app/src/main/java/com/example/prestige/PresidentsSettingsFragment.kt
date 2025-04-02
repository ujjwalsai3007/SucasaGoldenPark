package com.example.prestige

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth

class PresidentsSettingsFragment : Fragment() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_presidents_settings, container, false)
        
        // Find the Logout button
        val logoutButton: Button = view.findViewById(R.id.btnLogout)
        
        // Set up the Logout button click listener
        logoutButton.setOnClickListener {
            logoutUser()
        }
        
        return view
    }
    
    private fun logoutUser() {
        try {
            // Sign out from Firebase
            auth.signOut()
            
            // Show success message
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            
            // Create intent for SignIn activity
            val intent = Intent(requireContext(), SignIn::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            
            // Start SignIn activity and finish all activities in the stack
            startActivity(intent)
            requireActivity().finishAffinity()
        } catch (e: Exception) {
            // Handle any potential errors
            Toast.makeText(requireContext(), "Error during logout: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}