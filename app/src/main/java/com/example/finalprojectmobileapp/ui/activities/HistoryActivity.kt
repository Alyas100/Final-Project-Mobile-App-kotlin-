package com.example.finalprojectmobileapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.data.repositories.CaloriesRepo
import com.example.finalprojectmobileapp.data.repositories.WorkoutRepo
import com.example.finalprojectmobileapp.ui.activities.LogCaloriesActivity
import com.example.finalprojectmobileapp.ui.activities.LogWorkoutActivity
import com.example.finalprojectmobileapp.ui.activities.bottom_navigation.fragments.HistoryAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

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
    }

    private fun loadHistory() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Load Workout History
        db.collection("users").document(userId).collection("workouts").get()
            .addOnSuccessListener { result ->
                workoutList.clear()
                for (doc in result) {
                    val workoutName = doc.getString("workoutType") ?: "Unknown Workout"
                    val duration = doc.getLong("duration") ?: 0
                    val workoutEntry = "$workoutName - Duration: $duration min"
                    workoutList.add(workoutEntry)
                }
                workoutAdapter.notifyDataSetChanged()
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load workouts", Toast.LENGTH_SHORT).show()
                Log.e("HistoryActivity", "Error loading workouts", e)
            }

        // Load Calorie History
        db.collection("users").document(userId).collection("calories").get()
            .addOnSuccessListener { result ->
                calorieList.clear()
                for (doc in result) {
                    val foodType = doc.getString("foodName") ?: "Unknown food"
                    val calorieCount = doc.getLong("calories") ?: 0
                    val calorieEntry = "$foodType - Calories: $calorieCount kcal"
                    calorieList.add(calorieEntry)
                }
                calorieAdapter.notifyDataSetChanged()
            }.addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load calories", Toast.LENGTH_SHORT).show()
                Log.e("HistoryActivity", "Error loading calories", e)
            }
    }
}
