package com.example.finalprojectmobileapp.ui.activities

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.data.models.Reminder
import com.example.finalprojectmobileapp.data.repositories.ReminderRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RemindersActivity : AppCompatActivity() {

    private lateinit var reminderRecyclerView: RecyclerView
    private lateinit var reminderRepo: ReminderRepo
    private lateinit var reminderAdapter: ReminderAdapter

    companion object {
        private const val NOTIFICATION_PERMISSION_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reminders)

        val addReminderButton = findViewById<ImageView>(R.id.addReminderButton)

        addReminderButton.setOnClickListener {
            val intent = Intent(this, AddReminderActivity::class.java)
            startActivity(intent)
        }

        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            finish()
        }


        // Setup RecyclerView
        reminderRecyclerView = findViewById(R.id.reminderListRecyclerView)
        reminderRecyclerView.layoutManager = LinearLayoutManager(this)

        reminderRepo = ReminderRepo()
        reminderAdapter = ReminderAdapter(this)
        reminderRecyclerView.adapter = reminderAdapter

        fetchReminders()
        createNotificationChannel()
        requestNotificationPermissionIfNeeded()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "ReminderChannel"
            val descriptionText = "Channel for Reminder Notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("reminderChannel", name, importance).apply {
                description = descriptionText
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Notification permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Notification permission denied. Reminders might not show.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchReminders() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            FirebaseFirestore.getInstance()
                .collection("users")
                .document(userId)
                .collection("reminders")
                .get()
                .addOnSuccessListener { documents ->
                    val remindersList = mutableListOf<Reminder>()
                    for (document in documents) {
                        val reminder = document.toObject(Reminder::class.java)
                        remindersList.add(reminder)
                    }
                    reminderAdapter.setReminders(remindersList)
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error getting reminders", exception)
                }
        }
    }
}
