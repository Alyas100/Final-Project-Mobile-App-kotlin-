package com.example.finalprojectmobileapp.ui.activities

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.analytics.FirebaseAnalyticsHelper
import com.example.finalprojectmobileapp.data.models.Calories
import com.example.finalprojectmobileapp.data.repositories.CaloriesRepo
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class LogCaloriesActivity : AppCompatActivity() {

    private lateinit var etFoodName: EditText
    private lateinit var etCaloriesIntake: EditText
    private lateinit var btnSaveCalories: Button

    private val caloriesRepo = CaloriesRepo() // Repository instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_calories)


        // Log screen view event in Firebase Analytics
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "LogCaloriesActivity")
        FirebaseAnalyticsHelper.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)


        etFoodName = findViewById(R.id.etFoodName)
        etCaloriesIntake = findViewById(R.id.etCalories)
        btnSaveCalories = findViewById(R.id.btnSaveFood)

        btnSaveCalories.setOnClickListener {
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            if (userId != null) {
                saveCalories(userId)
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveCalories(userId: String) {
        val foodName = etFoodName.text.toString().trim()
        val caloriesStr = etCaloriesIntake.text.toString().trim()

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

        val calorieEntry = Calories(foodName, calories, System.currentTimeMillis())

        caloriesRepo.saveCalories(userId, calorieEntry,
            onSuccess = {
                Toast.makeText(this, "Calories saved!", Toast.LENGTH_SHORT).show()
                etFoodName.text.clear()
                etCaloriesIntake.text.clear()
            },
            onFailure = { e ->
                Log.e("Firestore", "Error adding calories", e)
                Toast.makeText(this, "Failed to save calories", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
