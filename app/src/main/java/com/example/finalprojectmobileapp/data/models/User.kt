package com.example.finalprojectmobileapp.data.models

data class User(
    val userId: String = "", // Unique user ID
    val name: String = "",
    val email: String = "",
    val workouts: List<Workout> = emptyList()  // this workout list will contain all the workouts logged by each user
)
