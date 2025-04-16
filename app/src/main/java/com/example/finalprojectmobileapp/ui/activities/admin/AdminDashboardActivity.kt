package com.example.finalprojectmobileapp.ui.activities.admin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.auth.activities.LoginActivity
import com.example.finalprojectmobileapp.ui.activities.admin.DeleteUserActivity
import com.example.finalprojectmobileapp.ui.activities.admin.EditDeleteWorkoutActivity
import com.example.finalprojectmobileapp.ui.activities.admin.ViewUserWorkoutsActivity
import com.example.finalprojectmobileapp.ui.activities.admin.ViewUsersActivity
import com.google.firebase.auth.FirebaseAuth

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        findViewById<Button>(R.id.btnViewAllUsers).setOnClickListener {
            startActivity(Intent(this, ViewUsersActivity::class.java))
        }


        findViewById<Button>(R.id.btnDeleteUsers).setOnClickListener {
            startActivity(Intent(this, DeleteUserActivity::class.java))
        }


        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            logoutAdmin()
        }
    }

    private fun logoutAdmin() {
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}
