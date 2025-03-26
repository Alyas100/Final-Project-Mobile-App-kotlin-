package com.example.finalprojectmobileapp.ui.activities.admin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectmobileapp.R
import com.google.firebase.firestore.FirebaseFirestore

class EditDeleteWorkoutActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_delete_workouts)

        db = FirebaseFirestore.getInstance()

        val edtWorkoutId = findViewById<EditText>(R.id.edtWorkoutId)
        val edtNewWorkoutName = findViewById<EditText>(R.id.edtNewWorkoutName)
        val btnEditWorkout = findViewById<Button>(R.id.btnEditWorkout)
        val btnDeleteWorkout = findViewById<Button>(R.id.btnDeleteWorkout)

        // Edit Workout
        btnEditWorkout.setOnClickListener {
            val workoutId = edtWorkoutId.text.toString().trim()
            val newWorkoutName = edtNewWorkoutName.text.toString().trim()

            if (workoutId.isNotEmpty() && newWorkoutName.isNotEmpty()) {
                db.collection("workouts").document(workoutId)
                    .update("name", newWorkoutName)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Workout updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to update workout", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Enter both Workout ID and New Name", Toast.LENGTH_SHORT).show()
            }
        }

        // Delete Workout
        btnDeleteWorkout.setOnClickListener {
            val workoutId = edtWorkoutId.text.toString().trim()

            if (workoutId.isNotEmpty()) {
                db.collection("workouts").document(workoutId)
                    .delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "Workout deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to delete workout", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Enter Workout ID", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
