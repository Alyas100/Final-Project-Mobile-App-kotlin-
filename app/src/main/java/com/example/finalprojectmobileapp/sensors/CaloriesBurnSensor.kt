package com.example.finalprojectmobileapp.sensors

import com.example.finalprojectmobileapp.R

import android.Manifest
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.finalprojectmobileapp.analytics.FirebaseAnalyticsHelper
import com.google.android.gms.location.*
import com.google.firebase.analytics.FirebaseAnalytics

class CaloriesBurnSensor : AppCompatActivity(), SensorEventListener,
    com.example.finalprojectmobileapp.sensors.Sensor {

    private lateinit var distanceTextView: TextView
    private lateinit var stepCountTextView: TextView
    private lateinit var calorieTextView: TextView

    // Step Counter Variables
    private lateinit var sensorManager: SensorManager
    private var stepCounter: Sensor? = null
    private var lastStepCount: Int = 0
    private var totalSteps = 0
    private val strideLength = 0.75 // Average stride length in meters

    // GPS Variables
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null
    private var totalDistanceGPS = 0.0 // Distance in meters

    // User Weight (Modify to allow user input later)
    private val userWeight = 70.0 // Default weight in kg

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calories_burn)

        // Initialize UI elements
        distanceTextView = findViewById(R.id.distanceTextView)
        stepCountTextView = findViewById(R.id.stepCountTextView)
        calorieTextView = findViewById(R.id.calorieTextView)

        // Initialize Step Counter
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounter != null) {
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI)
        } else {
            stepCountTextView.text = "Step Counter Not Available!"
        }

        // Initialize GPS Location Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkLocationPermission()
        setupLocationUpdates()
    }

    @Override
    override fun onResume() {
        super.onResume()
        startLocationUpdates()


        // Log screen view event in Firebase Analytics
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "CaloriesBurnSensor")
        FirebaseAnalyticsHelper.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    @Override
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    // Did not used here
    @Override
    override fun onDestroy() {
        super.onDestroy()
    }


    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
    }

    // GPS-Based Distance Calculation
    private fun setupLocationUpdates() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (lastLocation != null) {
                        val distance = haversine(
                            lastLocation!!.latitude, lastLocation!!.longitude,
                            location.latitude, location.longitude
                        )
                        totalDistanceGPS += distance
                        updateUI()
                    }
                    lastLocation = location
                }
            }
        }
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000 // Update every 5 seconds
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    // Step Counter Logic
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            if (lastStepCount == 0) {
                lastStepCount = event.values[0].toInt()
            }
            val stepDifference = event.values[0].toInt() - lastStepCount
            totalSteps = stepDifference
            updateUI()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No need to handle this for step counter
    }

    // Haversine Formula for GPS Distance Calculation
    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // Earth's radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distance = R * c // Distance in meters

        return if (distance < 1.5) 0.0 else distance
    }

    // Update UI with Distance, Steps, and Calories
    @Override
    override fun updateUI() {
        val totalDistance = totalDistanceGPS + (totalSteps * strideLength)

        val caloriesBurned = (totalSteps * 0.04 * userWeight) + // Step-based calories
                (totalDistance / 1000 * 3.5 * userWeight * 1.05) // GPS-based calories

        distanceTextView.text = "Distance: %.2f meters".format(totalDistance)
        stepCountTextView.text = "Steps: $totalSteps"
        calorieTextView.text = "Calories Burned: %.2f kcal".format(caloriesBurned)
    }
}
