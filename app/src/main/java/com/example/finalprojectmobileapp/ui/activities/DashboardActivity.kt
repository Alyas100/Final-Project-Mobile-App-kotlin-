package com.example.finalprojectmobileapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.example.finalprojectmobileapp.R
import com.google.android.material.navigation.NavigationView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.example.finalprojectmobileapp.analytics.FirebaseAnalyticsHelper
import com.google.firebase.analytics.FirebaseAnalytics

class DashboardActivity : AppCompatActivity() {

    private lateinit var btnLogWorkout: Button
    private lateinit var btnLogCalories: Button
    private lateinit var stepCountTextView: TextView
    private lateinit var calorieTextView: TextView

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)


        // Log screen view event in Firebase Analytics
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "DashboardActivity")
        FirebaseAnalyticsHelper.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)


        // Initialize UI components
        btnLogWorkout = findViewById(R.id.btnLogWorkout)
        btnLogCalories = findViewById(R.id.btnLogCalories)

        // Initialize DrawerLayout and NavigationView
        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.navigation_view)

        // Initialize Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Setup ActionBarDrawerToggle
        toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        // Enable home button to show the hamburger icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle menu item clicks
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                R.id.nav_logout -> {
                    Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
                    // Handle logout logic here
                    true
                }
                else -> false
            }
        }

        // Navigate to LogWorkoutActivity when button is clicked
        btnLogWorkout.setOnClickListener {
            val intent = Intent(this, LogWorkoutActivity::class.java)
            startActivity(intent)
        }

        // Navigate to LogCaloriesActivity when button is clicked
        btnLogCalories.setOnClickListener {
            val intent = Intent(this, LogCaloriesActivity::class.java)
            startActivity(intent)
        }
    }

    // Handle toolbar menu clicks
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
