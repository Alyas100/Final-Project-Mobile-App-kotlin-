package com.example.finalprojectmobileapp.auth.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.ui.activities.DashboardActivity
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import androidx.activity.result.IntentSenderRequest
import com.example.finalprojectmobileapp.analytics.FirebaseAnalyticsHelper
import com.example.finalprojectmobileapp.auth.email_logic.EmailAuthManager
import com.example.finalprojectmobileapp.ui.activities.admin.AdminDashboardActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var signInClient: SignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)


        // Log screen view event in Firebase Analytics
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "LoginActivity")
        FirebaseAnalyticsHelper.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)


        // Initialize the Google Identity client
        signInClient = Identity.getSignInClient(this)

        // Google Sign-In button
        findViewById<Button>(R.id.btnGoogleSignIn).setOnClickListener {
            signInWithGoogle()
        }




        // Email/Password Authentication
        val emailAuthManager = EmailAuthManager(this)
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.etPassword).text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                checkIfAdmin(email, password, emailAuthManager)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Check if the entered email belongs to an admin before logging in.
     */
    private fun checkIfAdmin(email: String, password: String, emailAuthManager: EmailAuthManager) {
        db.collection("admins").whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val adminData = documents.documents[0]
                    val storedPassword = adminData.getString("password")

                    if (storedPassword == password) {
                        Toast.makeText(this, "Admin login successful!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, AdminDashboardActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Invalid admin credentials", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // If not an admin, proceed with normal user login
                    loginAsUser(email, password, emailAuthManager)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error checking admin credentials", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Log in as a regular user if they are not an admin.
     */
    private fun loginAsUser(email: String, password: String, emailAuthManager: EmailAuthManager) {
        emailAuthManager.loginWithEmail(email, password) { success ->
            if (success) {
                Toast.makeText(this, "User login successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, DashboardActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Google Sign-In Handler
     */
    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = signInClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                val email = credential.id // Get Google account email

                if (idToken != null) {
                    checkIfGoogleAdmin(email, idToken)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Sign in failed", e)
            }
        }
    }

    /**
     * Initiates Google Sign-In
     */
    private fun signInWithGoogle() {
        val request = GetSignInIntentRequest.builder()
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()

        signInClient.getSignInIntent(request)
            .addOnSuccessListener { pendingIntent ->
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(pendingIntent.intentSender).build()
                    signInLauncher.launch(intentSenderRequest)
                } catch (e: Exception) {
                    Log.e(TAG, "Error launching sign-in intent", e)
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error getting sign-in intent", e)
            }
    }

    /**
     * Check if the Google Sign-In email belongs to an admin
     */
    private fun checkIfGoogleAdmin(email: String?, idToken: String) {
        if (email == null) return

        db.collection("admins").whereEqualTo("email", email).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    Toast.makeText(this, "Admin login successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, AdminDashboardActivity::class.java))
                    finish()
                } else {
                    handleGoogleToken(idToken)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error checking admin credentials", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * Handles Google login for regular users
     */
    private fun handleGoogleToken(idToken: String) {
        Log.d(TAG, "Got ID token: $idToken")
        startActivity(Intent(this, DashboardActivity::class.java))
        finish()
    }
}
