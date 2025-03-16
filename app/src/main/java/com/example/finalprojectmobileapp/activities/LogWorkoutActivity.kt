package com.example.finalprojectmobileapp.activities

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectmobileapp.R
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class LogWorkoutActivity : AppCompatActivity() {

    // Access a Cloud Firestore instance from your Activity
    val db = Firebase.firestore

    private lateinit var etWorkoutType: EditText
    private lateinit var etDuration: EditText
    private lateinit var btnSaveWorkout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_workout)

        etWorkoutType = findViewById(R.id.etWorkoutType)
        etDuration = findViewById(R.id.etDuration)
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout)


        btnSaveWorkout.setOnClickListener {

            saveWorkout()
        }
    }

    private fun saveWorkout() {
        val workoutType = etWorkoutType.text.toString().trim()
        val durationStr = etDuration.text.toString().trim()

        // Input validation
        if (workoutType.isEmpty() || durationStr.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val duration = durationStr.toIntOrNull()
        if (duration == null || duration <= 0) {
            Toast.makeText(this, "Please enter a valid duration", Toast.LENGTH_SHORT).show()
            return
        }

        // Create workout data
        val workout = hashMapOf(
            "workoutType" to workoutType,
            "duration" to duration,
            "timestamp" to System.currentTimeMillis()
        )

        // Save to Firestore
        db.collection("workouts")
            .add(workout)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Workout added with ID: ${documentReference.id}")
                Toast.makeText(this, "Workout saved!", Toast.LENGTH_SHORT).show()
                etWorkoutType.text.clear()
                etDuration.text.clear()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding workout", e)
                Toast.makeText(this, "Failed to save workout", Toast.LENGTH_SHORT).show()
            }
    }
}