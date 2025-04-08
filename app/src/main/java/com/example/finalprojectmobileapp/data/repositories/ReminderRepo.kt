package com.example.finalprojectmobileapp.data.repositories

import android.util.Log
import com.example.finalprojectmobileapp.data.models.Reminder
import com.example.finalprojectmobileapp.data.models.Workout
import com.google.firebase.firestore.FirebaseFirestore


class ReminderRepo {

    private val db = FirebaseFirestore.getInstance()


    fun saveReminder(userId: String, reminder: Reminder, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {

        // Exact same structure as your WorkoutRepo
        db.collection("users").document(userId).collection("reminders")
            .add(reminder)
            .addOnSuccessListener { documentReference ->
                Log.d("Firestore", "Reminder added with ID: ${documentReference.id}")
                onSuccess()
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding reminder", e)
                onFailure(e)
            }
    }
}
