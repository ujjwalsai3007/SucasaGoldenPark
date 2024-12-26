package com.example.prestige

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.prestige.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val animation=AnimationUtils.loadAnimation(this,R.anim.fade_in)
        binding.logoImage.startAnimation(animation)

        binding.letsGoButton.setOnClickListener {
            val intent=Intent(this,Login_logup_screen::class.java)
            startActivity(intent)
        }


    }
}