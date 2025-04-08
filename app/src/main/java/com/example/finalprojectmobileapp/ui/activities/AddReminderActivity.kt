package com.example.finalprojectmobileapp.ui.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.data.models.Reminder
import com.example.finalprojectmobileapp.data.repositories.ReminderRepo
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class AddReminderActivity : AppCompatActivity() {

    private lateinit var activityInput: EditText
    private lateinit var timeInput: EditText
    private lateinit var saveReminderButton: Button
    private lateinit var reminderRepo: ReminderRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_reminder)

        activityInput = findViewById(R.id.activityInput)
        timeInput = findViewById(R.id.timeInput)
        saveReminderButton = findViewById(R.id.saveReminderButton)

        reminderRepo = ReminderRepo()

        saveReminderButton.setOnClickListener {
            val activity = activityInput.text.toString().trim()
            val time = timeInput.text.toString().trim()
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            val userId = firebaseUser?.uid

            if (activity.isNotEmpty() && time.isNotEmpty() && userId != null) {
                // Parse the time string to get hour and minute
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                val calendar = Calendar.getInstance()

                try {
                    val date = timeFormat.parse(time)
                    calendar.time = date
                    val hour = calendar.get(Calendar.HOUR_OF_DAY)
                    val minute = calendar.get(Calendar.MINUTE)

                    // Generate a unique ID for the reminder
                    val reminderId = UUID.randomUUID().toString()

                    // Create the Reminder object
                    val reminder = Reminder(
                        id = reminderId, // Unique ID
                        activity = activity,
                        time = time,
                        title = title.toString(),
                        hour = hour,
                        minute = minute,
                        isEnabled = false // Assuming the reminder is initially disabled
                    )

                    // Save reminder to the repository
                    reminderRepo.saveReminder(
                        userId,
                        reminder,
                        onSuccess = {
                            Toast.makeText(this, "Reminder saved!", Toast.LENGTH_SHORT).show()
                            activityInput.text.clear()
                            timeInput.text.clear()
                            startActivity(Intent(this, RemindersActivity::class.java))
                        },
                        onFailure = { e ->
                            Toast.makeText(this, "Failed to save reminder", Toast.LENGTH_SHORT).show()
                        }
                    )

                } catch (e: Exception) {
                    Toast.makeText(this, "Invalid time format", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
