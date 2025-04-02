package com.example.prestige

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.prestige.databinding.ActivityResidentsScreenBinding
import com.google.firebase.auth.FirebaseAuth

class ResidentsScreen : AppCompatActivity() {
    private lateinit var binding: ActivityResidentsScreenBinding
    private lateinit var auth: FirebaseAuth
    private val TAG = "ResidentsScreen"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResidentsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance()
        
        // Check if user is authenticated
        if (auth.currentUser == null) {
            Log.e(TAG, "User is not authenticated, redirecting to login")
            Toast.makeText(this, "Please log in to continue", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignIn::class.java))
            finish()
            return
        }
        
        Log.d(TAG, "User authenticated: ${auth.currentUser?.uid}")

        // Default fragment
        replaceFragment(HomeFragment())

        // Bottom navigation listener
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_activity -> replaceFragment(ResidentsActivityFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                logoutUser()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun logoutUser() {
        auth.signOut()
        Log.d(TAG, "User logged out")
        Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, SignIn::class.java))
        finish()
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }

    override fun onResume() {
        super.onResume()
        // Check authentication again when screen resumes
        if (auth.currentUser == null) {
            Log.e(TAG, "User authentication lost, redirecting to login")
            Toast.makeText(this, "Session expired. Please log in again", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, SignIn::class.java))
            finish()
        }
    }
}