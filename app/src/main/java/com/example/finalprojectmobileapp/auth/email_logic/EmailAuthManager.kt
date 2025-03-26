package com.example.finalprojectmobileapp.auth.email_logic

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

// This class is responsible for handling email authentication
class EmailAuthManager(private val context: Context) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun loginWithEmail(email: String, password: String, callback: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    Log.d("EmailAuth", "Login successful!")
                    Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()

                    // Call callback with success
                    callback(true)
                } else {
                    Log.e("EmailAuth", "Login failed", task.exception)
                    Toast.makeText(context, "Login Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()

                    // Call callback with failure
                    callback(false)
                }
            }
    }
}