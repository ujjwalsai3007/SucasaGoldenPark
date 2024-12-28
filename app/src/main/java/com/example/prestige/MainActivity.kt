package com.example.prestige

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.prestige.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Load animations
        val fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val floatingAnimation = AnimationUtils.loadAnimation(this, R.anim.floating)

        // Apply animations
        binding.welcomeText.startAnimation(fadeInAnimation)
        //binding.logoImage.startAnimation(floatingAnimation)

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
}