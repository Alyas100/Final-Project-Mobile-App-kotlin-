package com.example.finalprojectmobileapp.ui.activities.bottom_navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.ui.activities.bottom_navigation.fragments.AddFragmentPage
import com.example.finalprojectmobileapp.ui.activities.bottom_navigation.fragments.DashboardFragmentPage
import com.example.finalprojectmobileapp.ui.activities.bottom_navigation.fragments.MoreFragmentPage
import com.google.android.material.bottomnavigation.BottomNavigationView


// this activity contains bottom navigation bar, and then use Fragments to display the different pages (Dashboard, Diary, etc.)
class CommonActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom_navigation_layout)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        // Set default fragment
        replaceFragment(DashboardFragmentPage())

        // Check if there's a destination passed from the other page
        val destination = intent.getStringExtra("destination")

        // Set the default fragment based on the destination
        if (destination == "adminDashboard") {
            replaceFragment(DashboardFragmentPage())
            bottomNavigationView.selectedItemId = R.id.nav_dashboard
        } else {
            // Default to Dashboard if no destination is specified
            replaceFragment(DashboardFragmentPage())
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
