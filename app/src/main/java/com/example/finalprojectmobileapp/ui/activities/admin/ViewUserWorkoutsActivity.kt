package com.example.finalprojectmobileapp.ui.activities.admin

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectmobileapp.R
import com.google.firebase.firestore.FirebaseFirestore

class ViewUserWorkoutsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_workouts)

        db = FirebaseFirestore.getInstance()
        val txtWorkouts = findViewById<TextView>(R.id.txtWorkouts)

        db.collection("workouts").get()
            .addOnSuccessListener { result ->
                val workouts = mutableListOf<String>()
                for (document in result) {
                    val workoutName = document.getString("name") ?: "Unnamed Workout"
                    workouts.add(workoutName)
                }
                txtWorkouts.text = workouts.joinToString("\n")
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load workouts", Toast.LENGTH_SHORT).show()
            }
    }
}
