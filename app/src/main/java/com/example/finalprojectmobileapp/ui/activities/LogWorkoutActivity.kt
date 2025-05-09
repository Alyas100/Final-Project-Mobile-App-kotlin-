package com.example.finalprojectmobileapp.ui.activities

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.analytics.FirebaseAnalyticsHelper
import com.example.finalprojectmobileapp.data.models.Workout
import com.example.finalprojectmobileapp.data.repositories.WorkoutRepo
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth

class LogWorkoutActivity : AppCompatActivity() {

    private lateinit var etWorkoutType: EditText
    private lateinit var etDuration: EditText
    private lateinit var btnSaveWorkout: Button
    private lateinit var workoutRepo: WorkoutRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_workout)

        // Log screen view event in Firebase Analytics
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "LogWorkoutActivity")
        FirebaseAnalyticsHelper.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)

        etWorkoutType = findViewById(R.id.etWorkoutType)
        etDuration = findViewById(R.id.etDuration)
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout)

        workoutRepo = WorkoutRepo()

        btnSaveWorkout.setOnClickListener {
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val userId = firebaseUser?.uid

            // Check if user ID is available
            if (userId != null) {
                saveWorkout(userId)
            } else {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                Log.e("LogWorkoutActivity", "Failed to get user ID: User not logged in")
            }
        }
    }

    private fun saveWorkout(userId: String) {
        val workoutType = etWorkoutType.text.toString().trim()
        val durationStr = etDuration.text.toString().trim()

        if (workoutType.isEmpty() || durationStr.isEmpty()) {
            Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val duration = durationStr.toIntOrNull()
        if (duration == null || duration <= 0) {
            Toast.makeText(this, "Please enter a valid duration", Toast.LENGTH_SHORT).show()
            return
        }

        val workout = Workout(workoutType, duration, System.currentTimeMillis())

        // Save workout using the repository
        workoutRepo.saveWorkout(
            userId,
            workout,
            onSuccess = {
                Toast.makeText(this, "Calories saved!", Toast.LENGTH_SHORT).show()

                // Add delay for smoother UX (1 second)
                Handler(Looper.getMainLooper()).postDelayed({
                    etWorkoutType.text.clear()
                    etDuration.text.clear()

                    finish()
                }, 1000) // 1000 milliseconds = 1 second
            },
            onFailure = { e ->
                Log.e("Firestore", "Error adding workout", e)
                Toast.makeText(this, "Failed to save workout", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
