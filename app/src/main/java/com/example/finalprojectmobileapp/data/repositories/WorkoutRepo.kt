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

        // Get last workout (most recent workout)
        fun getLastWorkout(userId: String, onComplete: (Workout) -> Unit) {
            db.collection("users").document(userId).collection("workouts")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)  // Get the most recent workout
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // Handle case when no workouts exist
                        onComplete(Workout()) // Return a default empty workout
                    } else {
                        val workout = documents.documents[0].toObject(Workout::class.java)
                        workout?.let {
                            onComplete(it)  // Pass the workout data
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("WorkoutRepo", "Error fetching last workout", e)
                }
        }
    }
