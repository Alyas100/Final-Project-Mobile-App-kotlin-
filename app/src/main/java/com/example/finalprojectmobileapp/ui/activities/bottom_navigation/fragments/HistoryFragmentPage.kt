package com.example.finalprojectmobileapp.ui.activities.bottom_navigation.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.ui.activities.LogCaloriesActivity
import com.example.finalprojectmobileapp.ui.activities.LogWorkoutActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class HistoryFragmentPage : Fragment() {
    private lateinit var rvWorkoutHistory: RecyclerView
    private lateinit var rvCalorieHistory: RecyclerView
    private lateinit var btnAddWorkout: Button
    private lateinit var btnAddCalories: Button
    private lateinit var db: FirebaseFirestore
    private val workoutList = mutableListOf<String>()
    private val calorieList = mutableListOf<String>()
    private lateinit var workoutAdapter: HistoryAdapter
    private lateinit var calorieAdapter: HistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history_page, container, false)

        // Initialize Firestore
        db = FirebaseFirestore.getInstance()

        // Initialize Views
        rvWorkoutHistory = view.findViewById(R.id.rvWorkoutHistory)
        rvCalorieHistory = view.findViewById(R.id.rvCalorieHistory)
        btnAddWorkout = view.findViewById(R.id.btnAddWorkout)
        btnAddCalories = view.findViewById(R.id.btnAddCalories)

        // Setup RecyclerViews
        workoutAdapter = HistoryAdapter(workoutList)
        calorieAdapter = HistoryAdapter(calorieList)
        rvWorkoutHistory.layoutManager = LinearLayoutManager(requireContext())
        rvCalorieHistory.layoutManager = LinearLayoutManager(requireContext())
        rvWorkoutHistory.adapter = workoutAdapter
        rvCalorieHistory.adapter = calorieAdapter

        // Load Data from Firestore
        loadHistory()

        // Add More Workout Button
        btnAddWorkout.setOnClickListener {
            startActivity(Intent(requireContext(), LogWorkoutActivity::class.java))
        }

        // Add More Calorie Button
        btnAddCalories.setOnClickListener {
            startActivity(Intent(requireContext(), LogCaloriesActivity::class.java))
        }

        return view
    }

    private fun loadHistory() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Load Workout History
        db.collection("users").document(userId).collection("workouts").get()
            .addOnSuccessListener { result ->
                workoutList.clear()  // Clear previous data
                for (doc in result) {
                    val workoutName = doc.getString("workoutType") ?: "Unknown Workout"
                    val duration = doc.getLong("duration") ?: 0
                    val workoutEntry = "$workoutName - Duration: $duration min"
                    workoutList.add(workoutEntry)
                }
                workoutAdapter.notifyDataSetChanged()
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load workouts", Toast.LENGTH_SHORT).show()
                Log.e("HistoryFragmentPage", "Error loading workouts", e)
            }

        // Load Calorie History
        db.collection("users").document(userId).collection("calories").get()
            .addOnSuccessListener { result ->
                calorieList.clear()  // Clear previous data
                for (doc in result) {
                    val foodType = doc.getString("foodName") ?: "Unknown food"
                    val calorieCount = doc.getLong("calories") ?: 0
                    val calorieEntry = "$foodType - Calories: $calorieCount kcal"
                    calorieList.add(calorieEntry)
                }
                calorieAdapter.notifyDataSetChanged()
            }.addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load calories", Toast.LENGTH_SHORT).show()
                Log.e("HistoryFragmentPage", "Error loading calories", e)
            }
    }

}
