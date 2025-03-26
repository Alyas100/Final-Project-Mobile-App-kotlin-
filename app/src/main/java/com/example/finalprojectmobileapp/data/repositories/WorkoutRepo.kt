package com.example.finalprojectmobileapp.data.repositories

import android.util.Log
import com.example.finalprojectmobileapp.data.models.Workout
import com.google.firebase.firestore.FirebaseFirestore

class WorkoutRepo {

    private val db = FirebaseFirestore.getInstance()

    fun saveWorkout(userId: String, workout: Workout, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(userId).collection("workouts")
            .add(workout)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Workout added with ID: ${documentReference.id}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding workout", e)
                onFailure(e)
            }
    }
}