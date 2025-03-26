package com.example.finalprojectmobileapp.data.models

data class Calories(
    val foodName: String = "",
    val calories: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
