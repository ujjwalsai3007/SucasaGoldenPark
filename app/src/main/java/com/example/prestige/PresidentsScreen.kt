package com.example.prestige

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.prestige.databinding.ActivityPresidentScreenBinding

class PresidentsScreen : AppCompatActivity() {
    private lateinit var binding: ActivityPresidentScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Binding setup
        binding = ActivityPresidentScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Default fragment
        replaceFragment(PresidentHomeFragment())

        // BottomNavigationView setup
        binding.presidentBottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_president_home -> replaceFragment(PresidentHomeFragment())
                R.id.nav_president_events -> replaceFragment(PresidentEventsFragment())
                R.id.nav_president_maintenance -> replaceFragment(PresidentMaintenanceFragment())
                R.id.nav_president_settings -> replaceFragment(PresidentsSettingsFragment())
                else -> false
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.president_fragment_container, fragment)
        fragmentTransaction.commit()
    }
}