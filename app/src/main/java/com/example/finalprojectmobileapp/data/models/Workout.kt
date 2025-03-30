package com.example.finalprojectmobileapp.data.models

// This class is used for storing data as format before got referenced by repo layer to store in firebase
data class Workout(
    // Belows are default values to allow firestore deserialize the object correctly
    val workoutType: String = "",
    val duration: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
