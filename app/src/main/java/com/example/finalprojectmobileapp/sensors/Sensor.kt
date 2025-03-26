package com.example.finalprojectmobileapp.sensors

// incorporate abstraction concept here
// incorporate polymorphism by method overriding by classes that implement this interface
interface Sensor {

    fun onResume()
    fun onPause()
    fun onDestroy()
    fun updateUI()


}
