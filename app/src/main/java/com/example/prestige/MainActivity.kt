package com.example.prestige

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.prestige.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if the user is already logged in
        val sharedPreferences: SharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        val role = sharedPreferences.getString("role", null)

        if (isLoggedIn && role != null) {
            redirectToRoleScreen(role)
            finish() // End MainActivity to skip animation
            return
        }

        // If not logged in, proceed with the animation
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load animations
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val floatingAnimation = AnimationUtils.loadAnimation(this, R.anim.floating)

        // Apply animations
        binding.welcomeText.startAnimation(fadeInAnimation)
        binding.logoImage.startAnimation(floatingAnimation)

        // Background gradient animation
        val background = binding.root.background as AnimationDrawable
        background.setEnterFadeDuration(3000)
        background.setExitFadeDuration(3000)
        background.start()

        // Button click listener
        binding.letsGoButton.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }

    private fun redirectToRoleScreen(role: String) {
        val intent = when (role) {
            "Resident" -> Intent(this, ResidentsScreen::class.java)
            "President" -> Intent(this, PresidentsScreen::class.java)
            "Security Guard" -> Intent(this, SecurityGuardScreen::class.java)
            else -> {
                val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
                sharedPreferences.edit().clear().apply() // Log out invalid roles
                Intent(this, SignIn::class.java)
            }
        }
        startActivity(intent)
    }
}