package com.example.finalprojectmobileapp.data.models

data class Reminder(
    val activity: String = "",
    val time: String = "",
    val timestamp: Long = System.currentTimeMillis(),  // Store timestamp as Long
    val id: String,
    val title: String,
    val hour: Int = 0,
    val minute: Int = 0,
    var isEnabled: Boolean = false
) {
    // No-argument constructor for Firebase to use when deserializing
    constructor() : this("", "", System.currentTimeMillis(), "", "", 0, 0)
}
