package com.example.finalprojectmobileapp.logic

import com.example.finalprojectmobileapp.models.Workout

class WorkoutManager {
    private val workoutList = mutableListOf<Workout>()

    fun addWorkout(workout: Workout) {
        workoutList.add(workout)
    }

    fun getWorkouts(): List<Workout> {
        return workoutList
    }
}