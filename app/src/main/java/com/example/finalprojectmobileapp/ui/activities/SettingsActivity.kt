package com.example.finalprojectmobileapp.ui.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.finalprojectmobileapp.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var notificationsSwitch: Switch
    private lateinit var darkModeSwitch: Switch
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_page)

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE)
        editor = sharedPreferences.edit()

        // Notification switch logic
        notificationsSwitch = findViewById(R.id.switch_notifications)
        val isNotificationsEnabled = sharedPreferences.getBoolean("notifications_enabled", true)
        notificationsSwitch.isChecked = isNotificationsEnabled

        notificationsSwitch.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean("notifications_enabled", isChecked).apply()
            if (isChecked) {
                enableNotifications()
            } else {
                disableNotifications()
            }
        }

        // Dark mode switch logic
        darkModeSwitch = findViewById(R.id.switch_dark_mode)
        val isDarkModeEnabled = sharedPreferences.getBoolean("dark_mode", false)
        darkModeSwitch.isChecked = isDarkModeEnabled

        darkModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                Toast.makeText(this, "Dark Mode Enabled", Toast.LENGTH_SHORT).show()
                editor.putBoolean("dark_mode", true).apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                Toast.makeText(this, "Light Mode Enabled", Toast.LENGTH_SHORT).show()
                editor.putBoolean("dark_mode", false).apply()
            }
        }
    }

    private fun enableNotifications() {
        Toast.makeText(this, "Notifications Enabled", Toast.LENGTH_SHORT).show()
        // You can add logic here to register notifications or alarms
    }

    private fun disableNotifications() {
        Toast.makeText(this, "Notifications Disabled", Toast.LENGTH_SHORT).show()
        // You can add logic here to cancel notifications or alarms
    }
}
