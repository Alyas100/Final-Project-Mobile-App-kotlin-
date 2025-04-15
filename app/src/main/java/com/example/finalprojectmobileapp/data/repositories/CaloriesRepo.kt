package com.example.finalprojectmobileapp.data.repositories

import android.util.Log
import com.example.finalprojectmobileapp.data.models.Calories
import com.google.firebase.firestore.FirebaseFirestore

class CaloriesRepo {

    private val db = FirebaseFirestore.getInstance()

    fun saveCalories(userId: String, calorieEntry: Calories, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("users").document(userId).collection("calories")
            .add(calorieEntry)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Calorie entry added with ID: ${documentReference.id}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding calories", e)
                onFailure(e)
            }
    }

        // Get last calorie entry (most recent entry)
        fun getLastCalories(userId: String, onComplete: (Calories) -> Unit) {
            db.collection("users").document(userId).collection("calories")
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(1)  // Get the most recent calorie entry
                .get()
                .addOnSuccessListener { documents ->
                    if (documents.isEmpty) {
                        // Handle case when no calorie entries exist
                        onComplete(Calories()) // Return a default empty calories entry
                    } else {
                        val calories = documents.documents[0].toObject(Calories::class.java)
                        calories?.let {
                            onComplete(it)  // Pass the calories data
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("CaloriesRepo", "Error fetching last calories", e)
                }
        }
    }
