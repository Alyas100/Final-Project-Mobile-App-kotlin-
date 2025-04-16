package com.example.finalprojectmobileapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.ui.activities.LogCaloriesActivity
import com.example.finalprojectmobileapp.ui.activities.LogWorkoutActivity
import com.example.finalprojectmobileapp.ui.activities.bottom_navigation.fragments.HistoryAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class HistoryActivity : AppCompatActivity() {
    private lateinit var rvWorkoutHistory: RecyclerView
    private lateinit var rvCalorieHistory: RecyclerView
    private lateinit var btnAddWorkout: Button
    private lateinit var btnAddCalories: Button
    private lateinit var db: FirebaseFirestore
    private val workoutList = mutableListOf<String>()
    private val calorieList = mutableListOf<String>()
    private lateinit var workoutAdapter: HistoryAdapter
    private lateinit var calorieAdapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_history_page) // reuse the same layout

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize Views
        rvWorkoutHistory = findViewById(R.id.rvWorkoutHistory)
        rvCalorieHistory = findViewById(R.id.rvCalorieHistory)
        btnAddWorkout = findViewById(R.id.btnAddWorkout)
        btnAddCalories = findViewById(R.id.btnAddCalories)

        // Setup RecyclerViews
        workoutAdapter = HistoryAdapter(workoutList)
        calorieAdapter = HistoryAdapter(calorieList)
        rvWorkoutHistory.layoutManager = LinearLayoutManager(this)
        rvCalorieHistory.layoutManager = LinearLayoutManager(this)
        rvWorkoutHistory.adapter = workoutAdapter
        rvCalorieHistory.adapter = calorieAdapter

        // Load Data from Firestore
        loadHistory()

        // Add Workout
        btnAddWorkout.setOnClickListener {
            startActivity(Intent(this, LogWorkoutActivity::class.java))
        }

        // Add Calories
        btnAddCalories.setOnClickListener {
            startActivity(Intent(this, LogCaloriesActivity::class.java))
        }

        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }

    }

    private fun loadHistory() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // ================== Workouts ==================
        val groupedWorkouts = mutableMapOf<String, MutableList<String>>()

        db.collection("users").document(userId).collection("workouts")
            .orderBy("timestamp") // Ensure "timestamp" field exists
            .get()
            .addOnSuccessListener { result ->
                workoutList.clear()
                for (doc in result) {
                    val workoutName = doc.getString("workoutType") ?: "Unknown Workout"
                    val duration = doc.getLong("duration") ?: 0
                    val timestamp = doc.getLong("timestamp") // Retrieve as a Long (Unix timestamp in ms)

                    // Convert the Unix timestamp to Date
                    val dateKey = if (timestamp != null) {
                        val date = Date(timestamp) // Create Date object using the Unix timestamp
                        android.text.format.DateFormat.format("yyyy-MM-dd", date).toString()
                    } else {
                        "Unknown Date"
                    }

                    val entry = "$workoutName - Duration: $duration min"
                    groupedWorkouts.getOrPut(dateKey) { mutableListOf() }.add(entry)
                }

                val sortedWorkoutDates = groupedWorkouts.keys.sortedDescending()
                for (date in sortedWorkoutDates) {
                    workoutList.add("📅 $date")
                    groupedWorkouts[date]?.forEach { entry ->
                        workoutList.add("  • $entry")
                    }
                }

                workoutAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load workouts", Toast.LENGTH_SHORT).show()
                Log.e("HistoryActivity", "Error loading workouts", e)
            }

        // ================== Calories ==================
        val groupedCalories = mutableMapOf<String, MutableList<String>>()

        db.collection("users").document(userId).collection("calories")
            .orderBy("timestamp") // Ensure "timestamp" field exists
            .get()
            .addOnSuccessListener { result ->
                calorieList.clear()
                for (doc in result) {
                    val foodName = doc.getString("foodName") ?: "Unknown Food"
                    val calories = doc.getLong("calories") ?: 0
                    val timestamp = doc.getLong("timestamp") // Retrieve as a Long (Unix timestamp in ms)

                    // Convert the Unix timestamp to Date
                    val dateKey = if (timestamp != null) {
                        val date = Date(timestamp) // Create Date object using the Unix timestamp
                        android.text.format.DateFormat.format("yyyy-MM-dd", date).toString()
                    } else {
                        "Unknown Date"
                    }

                    val entry = "$foodName - Calories: $calories kcal"
                    groupedCalories.getOrPut(dateKey) { mutableListOf() }.add(entry)
                }

                val sortedCalorieDates = groupedCalories.keys.sortedDescending()
                for (date in sortedCalorieDates) {
                    calorieList.add("📅 $date")
                    groupedCalories[date]?.forEach { entry ->
                        calorieList.add("  • $entry")
                    }
                }

                calorieAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load calories", Toast.LENGTH_SHORT).show()
                Log.e("HistoryActivity", "Error loading calories", e)
            }
    }
}
