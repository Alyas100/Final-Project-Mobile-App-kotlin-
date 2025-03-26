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
}