package com.example.finalprojectmobileapp.sensors

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.finalprojectmobileapp.analytics.FirebaseAnalyticsHelper
import com.example.finalprojectmobileapp.R
import com.google.firebase.analytics.FirebaseAnalytics

// TestingAndroidSensor implements SensorEventListener interface, and implement all of its abstract method even just declared without actually using it because it is a must when implementing interface.
class TestingAndroidSensor : AppCompatActivity(), SensorEventListener,
    com.example.finalprojectmobileapp.sensors.Sensor {

    private var initialStepCount: Int = -1  // Stores the first detected step count
    private var totalStepsToday: Int = 0


    // A simple key-value pair local android storage for preferences
    private lateinit var sharedPreferences: android.content.SharedPreferences



    /*

    // Declare stepCount as a global variable
    // Used with 'Sensor.TYPE_STEP_DETECTOR' inside onSensorChanged()
    private var stepCount: Int = 0

     */

    // Declares a read-only property (val) to access the step counter sensor
    // lazy means it only called automatically the first time the variable is accessed and remember the result to not run again (ensures better performance)
    // This is a lazy-initialized property that holds the SensorManager instance, which is used to access the device's sensors.
    private val sensorManager by lazy {
        this.getSystemService(SENSOR_SERVICE) as SensorManager
    }


    // This is a lazy-initialized property that holds a reference to the step counter sensor (TYPE_STEP_COUNTER), retrieved using sensorManager.
    private val sensor: Sensor? by lazy {
        sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    }



    companion object {
        private const val REQUEST_CODE_ACTIVITY_RECOGNITION = 101  // ADDED: Permission request code
    }







    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing_android_sensor)

        // Here for function for requesting permissions to access the step counter sensor in user device


        // Backward compatibility check for Android 10–13 (API 29–33)
        if (Build.VERSION.SDK_INT in 29..33) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                    REQUEST_CODE_ACTIVITY_RECOGNITION
                )
            }
        }



        // Initialize SharedPreferences (for simple storage purpose)
        sharedPreferences = getSharedPreferences("StepCounterPrefs", Context.MODE_PRIVATE)


        // Retrieve saved initial step count
        initialStepCount = sharedPreferences.getInt("initialStepCount", -1)


        // Check if the step counter sensor is available on the device
        val textView = findViewById<TextView>(R.id.sensorStatusText)
        textView.text = if (sensor == null)
            "Step detector sensor is not present on this device"
        else
            "Step detector sensor detected!"


        askForStepCounterPermission()
        }




    // ADDED: Function to show a pop-up asking the user for permission
    private fun askForStepCounterPermission() {
        val userAllowed = sharedPreferences.getBoolean("stepCounterAllowed", false)

        if (!userAllowed) {  // If user hasn't granted permission before
            AlertDialog.Builder(this)
                .setTitle("Allow Step Counting?")
                .setMessage("This app can count your steps. Do you want to allow this?")
                .setPositiveButton("Allow") { _, _ ->
                    sharedPreferences.edit().putBoolean("stepCounterAllowed", true).apply()
                    registerStepCounter()
                }
                .setNegativeButton("Deny") { _, _ ->
                    sharedPreferences.edit().putBoolean("stepCounterAllowed", false).apply()
                }
                .setCancelable(false)
                .show()
        } else {
            registerStepCounter()
        }
    }



    // Function to register the step counter sensor if user allowed it
    private fun registerStepCounter() {
        sensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }




    // To unregister the sensor when the app is closed
    // But if user switch to another app or lock the screen, the activity is just paused—not destroyed
    // So to prevent battery drain, need to implement onPause()
    // onDestroy(), onPause(), onResume() are automatically triggered by android when appropriate event happens
    @Override
    override fun onDestroy() {  // The function is wrapped with override because it changing the behavior of its parent class in this case is appcompatactivity() class.
        // Calls the parent class's onDestroy() original logic (non-override one) so it can avoid unexpected issues with activity not saving its ui state destroyed properly (the animations) before doing the custom logic below
        super.onDestroy()
        sensorManager.unregisterListener(this) // Unregister sensor when service stops
    }


    // Is needed to stop sensor updates when the app is not in the foreground, preventing battery drain.
    @Override
    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this) // Stops tracking to save battery
    }


    // Ensures the sensor starts tracking again when the user returns to the app.
    @Override
    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)

        // Log screen view event in Firebase Analytics
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "TestingAndroidSensor")
        FirebaseAnalyticsHelper.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }


/*

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_DETECTOR) {
            stepCount += 1  // Manually count steps
            Log.d("SensorDebug", "Step detected! Total: $stepCount")
            findViewById<TextView>(R.id.stepCountTextView).text = "Steps: $stepCount"
        }
    }

 */



    // this below is used for 'Sensor.TYPE_STEP_COUNTER'
    override fun onSensorChanged(event: SensorEvent?) {
        val userAllowed = sharedPreferences.getBoolean("stepCounterAllowed", false)

        if (userAllowed && event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            val totalStepsSinceReboot = event.values[0].toInt()

            // If first time, store the initial value
            if (initialStepCount == -1) {
                initialStepCount = totalStepsSinceReboot
                sharedPreferences.edit().putInt("initialStepCount", initialStepCount).apply()
            }


                // Calculate today's steps
            totalStepsToday = totalStepsSinceReboot - initialStepCount

            // debug statement to show steps today
            Log.d("StepCounter", "Today's steps: $totalStepsToday")


        }
    }


    // Update the UI with Total Steos
    // provide concrete implementation of the interface
    @Override
    override fun updateUI() {
        // Update UI
        findViewById<TextView>(R.id.stepCountTextView).text = "Steps: $totalStepsToday"
    }



    // this only need to be implemented if the app needed high accuracy like health tracking compared to above one.
    // this is no need to use right now but must be declared when implementing interface
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}


}










