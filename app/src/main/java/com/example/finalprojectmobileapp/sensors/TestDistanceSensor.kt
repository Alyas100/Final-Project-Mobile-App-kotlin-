package com.example.finalprojectmobileapp.sensors

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
import com.example.finalprojectmobileapp.R
import com.google.android.gms.location.*
import com.google.firebase.analytics.FirebaseAnalytics

class TestDistanceSensor : AppCompatActivity(), SensorEventListener,
    com.example.finalprojectmobileapp.sensors.Sensor {

    // Displays the total distance covered
    private lateinit var distanceTextView: TextView

    // Display a message if sensors are missing
    private lateinit var sensorStatusText: TextView





    // GPS Variables
    private lateinit var fusedLocationClient: FusedLocationProviderClient   // Manages GPS location updates using Google's Fused Location Api
    private lateinit var locationCallback: LocationCallback   // Receives location updates from the GPS sensor
    private var lastLocation: Location? = null   // Stores the last known GPS location to calculate distance (cause the gps interact with the satellite when user move so the latitude and longitude changed there)
    private var totalDistanceGPS = 0.0   // Stores the total distance calculated from the GPS (in meters)

    // * NOTE on how GPS distance is calculated---
    // Each time a new location update is received when the user moves, the gps sensor will compares last location with current location of the users using the 'Haversine formula'
    // Then it adds the calculated distance to the totalDistanceGPS that declared above





    // These one below using step counter(pedometer) from the sensor for indoor tracking purpose
    // Step Counter Variables
    private lateinit var sensorManager: SensorManager   // Handles access to hardware sensors on the device
    private var stepCounter: Sensor? = null   // A step sensor that counts the total number of steps taken
    private var lastStepCount: Int = 0   // Stores the previous step count to detect changes
    private var totalDistanceSteps = 0.0  // Stores the total distance based on steps × stride length
    private val strideLength = 0.75  // The assumed average stride length in meters which is 0.75 meters per step

    // * NOTE on how Step-Based distance is calculated---
    // Each time the step counter detects movement, it gets the new step count.
    // Then it will finds the differences between new and old step counts.
    // And finally it will multiplies the step difference by stride length and adds it to the totalDistanceStep that declared above.




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_testing_android_sensor_distance)


        distanceTextView = findViewById(R.id.distanceTextView)
        sensorStatusText = findViewById(R.id.sensorStatusText)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)    // Initialize GPS fusedLocationClient, and also allows app to request the user's location
        checkLocationPermission()    // This checks if the app has permission to access the device's location, and it will request permission if user hasnt granted permission.
        setupLocationUpdates()    // This function configures & starts GPS tracking, and tells the system how often to update the location and what accuracy level to use




        // Initialize Step Counter
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager    //  gets the system's SensorManager so that we can access the device's sensors
        stepCounter = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)    // This retrieves the step counter sensor from the device

        // Checks if Step Counter is available on the device
        if (stepCounter != null) {
            sensorStatusText.text = "Step Counter Available!"
            sensorManager.registerListener(this, stepCounter, SensorManager.SENSOR_DELAY_UI)
        } else {
            sensorStatusText.text = "Step Counter Not Available!"
        }
    }



    //  This will get called when the app comes to the foreground like when user opens the app or returns to it again.
   @Override
    override fun onResume() {
        super.onResume()
        // start the gps tracking again when the app is active
        startLocationUpdates()


        // Log screen view event in Firebase Analytics
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "TestDistanceSensor")
        FirebaseAnalyticsHelper.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }

    // This will get called when the app goes into the background for example when user switches apps or locks the screen
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



    // This function checks if the app has ACCESS_FINE_LOCATION permission.
    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1001
            )
        }
    }




    // Function for process GPS Updates & Calculate Distance
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




    // This function for start the gps tracking
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 5000  // Update every 5 seconds
            fastestInterval = 2000   // get te faster update if possible
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY   // use high accuracy mode which means it uses GPS instead of wifi or mobile networks
        }

        // Checks location permission again before starting GPS updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)   // Starts tracking location changes
        }
    }




    // this function responsible for stopping GPS tracking (used when the app is paused or no longer needs location updates to saves user's device battery)
    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }




    // This function responsible to calculate Distance Between Two GPS Points
    private fun haversine(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // Earth's radius in meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        val distance = R * c // Distance in meters

        // Ignore small movements to avoid GPS drift
        return if (distance < 1.5) 0.0 else distance  // Ignore movement <1.5 meters to prevent false movements, this is also important as gps is not always accurate and can detect movement even when stationary cause of the signal fluctuations
    }




    // Step counter logic
    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_STEP_COUNTER) {
            if (lastStepCount == 0) {
                lastStepCount = event.values[0].toInt()
            }
            val stepDifference = event.values[0].toInt() - lastStepCount
            totalDistanceSteps = stepDifference * strideLength
            updateUI()
        }
    }




    //
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No need to handle this for step counter
        // just leave it empty cause step counter doesnt rely on high precision so handling accuracy changes is not neccessary (but this method is declared anyway cause the parent class implment the interface class)
    }




    // Update the UI with Total Distance
    // provide concrete implementation of the interface
    @Override
    override fun updateUI() {
        val totalDistance = totalDistanceGPS + totalDistanceSteps
        distanceTextView.text = "Distance: %.2f meters".format(totalDistance)
    }
}
