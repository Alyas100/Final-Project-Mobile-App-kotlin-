package com.example.finalprojectmobileapp.ui.activities.admin

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectmobileapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ViewUsersActivity : AppCompatActivity() {

    private lateinit var txtUsers: TextView
    private val TAG = "ViewUsersActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_users)

        txtUsers = findViewById(R.id.txtUsers)

        val db = FirebaseFirestore.getInstance()
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            Log.d(TAG, "Logged in UID: ${currentUser.uid}")

            db.collection("users").get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val userIds = querySnapshot.documents.map { it.id }
                        Log.d(TAG, "User IDs: $userIds")
                        txtUsers.text = userIds.joinToString("\n")
                    } else {
                        txtUsers.text = "No users found."
                        Log.d(TAG, "No users found.")
                    }
                }
                .addOnFailureListener { e ->
                    txtUsers.text = "Error fetching users."
                    Log.e(TAG, "Error: ", e)
                }
        } else {
            txtUsers.text = "Not logged in."
            Log.d(TAG, "User is null")
        }
    }
}

