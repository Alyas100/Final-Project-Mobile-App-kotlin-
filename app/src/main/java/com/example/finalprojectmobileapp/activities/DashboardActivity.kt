package com.example.finalprojectmobileapp.activities

import android.content.Intent
import android.health.connect.HealthConnectManager

import androidx.lifecycle.lifecycleScope
import com.example.finalprojectmobileapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.ZoneOffset
import java.time.Duration

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.health.platform.client.permission.Permission
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.launch

class DashboardActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
    }
}





