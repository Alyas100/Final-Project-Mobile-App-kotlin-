package com.example.finalprojectmobileapp.ui.activities.admin

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectmobileapp.R
import com.google.firebase.firestore.FirebaseFirestore

class ViewUsersActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_users)

        db = FirebaseFirestore.getInstance()
        val txtUsers = findViewById<TextView>(R.id.txtUsers)

        db.collection("users").get()
            .addOnSuccessListener { result ->
                val userList = mutableListOf<String>()
                for (document in result) {
                    val userName = document.getString("name") ?: "Unknown User"
                    userList.add(userName)
                }
                txtUsers.text = userList.joinToString("\n")
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show()
            }
    }
}
