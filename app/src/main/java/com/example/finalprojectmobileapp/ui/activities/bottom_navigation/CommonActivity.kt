package com.example.finalprojectmobileapp.ui.activities.bottom_navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.ui.activities.bottom_navigation.fragments.AddFragmentPage
import com.example.finalprojectmobileapp.ui.activities.bottom_navigation.fragments.DashboardFragmentPage
import com.example.finalprojectmobileapp.ui.activities.bottom_navigation.fragments.MoreFragmentPage
import com.google.android.material.bottomnavigation.BottomNavigationView

class CommonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation_layout)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Check for destination passed from SplashActivity or other Activity
        val destination = intent.getStringExtra("destination")

        when (destination) {
            "dashboard" -> {
                replaceFragment(DashboardFragmentPage())
                bottomNavigationView.selectedItemId = R.id.nav_dashboard
            }
            "add" -> {
                replaceFragment(AddFragmentPage())
                bottomNavigationView.selectedItemId = R.id.nav_add
            }
            "more" -> {
                replaceFragment(MoreFragmentPage())
                bottomNavigationView.selectedItemId = R.id.nav_more
            }
            else -> {
                // Default to Dashboard if nothing is passed
                replaceFragment(DashboardFragmentPage())
                bottomNavigationView.selectedItemId = R.id.nav_dashboard
            }
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> replaceFragment(DashboardFragmentPage())
                R.id.nav_add -> replaceFragment(AddFragmentPage())
                R.id.nav_more -> replaceFragment(MoreFragmentPage())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
