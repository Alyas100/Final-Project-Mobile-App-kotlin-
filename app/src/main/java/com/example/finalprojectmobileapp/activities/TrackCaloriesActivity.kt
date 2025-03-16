package com.example.finalprojectmobileapp.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectmobileapp.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class TrackCaloriesActivity : AppCompatActivity() {

    // Firestore instance
    private val db = Firebase.firestore

    private lateinit var etFoodName: EditText
    private lateinit var etCaloriesIntake: EditText
    private lateinit var btnSaveCalories: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_calories)

        etFoodName = findViewById(R.id.etFoodName) // Added food name input
        etCaloriesIntake = findViewById(R.id.etCalories)
        btnSaveCalories = findViewById(R.id.btnSaveFood)

        btnSaveCalories.setOnClickListener {
            saveCalories()
        }
    }

    private fun saveCalories() {

        val foodName = etFoodName.text.toString().trim()
        val caloriesStr = etCaloriesIntake.text.toString().trim()

        // Input validation
        if (foodName.isEmpty()) {
            Toast.makeText(this, "Please enter food name", Toast.LENGTH_SHORT).show()
            return
        }
        if (caloriesStr.isEmpty()) {
            Toast.makeText(this, "Please enter calories intake", Toast.LENGTH_SHORT).show()
            return
        }

        val calories = caloriesStr.toIntOrNull()
        if (calories == null || calories <= 0) {
            Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            return
        }

        // Create calorie log data
        val calorieData = hashMapOf(
            "foodName" to foodName, // Added food name
            "calories" to calories,
            "timestamp" to System.currentTimeMillis()
        )

        // Save to Firestore
        db.collection("calories")
            .add(calorieData)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Calorie entry added with ID: ${documentReference.id}")
                Toast.makeText(this, "Calories saved!", Toast.LENGTH_SHORT).show()
                etFoodName.text.clear()
                etCaloriesIntake.text.clear()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding calories", e)
                Toast.makeText(this, "Failed to save calories", Toast.LENGTH_SHORT).show()
            }
    }
}
