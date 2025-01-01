package com.example.prestige

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.prestige.databinding.ActivityResidentsScreenBinding

class ResidentsScreen : AppCompatActivity() {
    private lateinit var binding: ActivityResidentsScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResidentsScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Default fragment
        replaceFragment(HomeFragment())

        // Bottom navigation listener
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> replaceFragment(HomeFragment())
                R.id.nav_activity-> replaceFragment(ResidentsActivityFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment)
        fragmentTransaction.commit()
    }
}