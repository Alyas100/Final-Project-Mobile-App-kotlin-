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
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import androidx.activity.result.IntentSenderRequest
import com.example.finalprojectmobileapp.analytics.FirebaseAnalyticsHelper
import com.example.finalprojectmobileapp.auth.email_logic.EmailAuthManager
import com.example.finalprojectmobileapp.ui.activities.admin.AdminDashboardActivity
import com.example.finalprojectmobileapp.ui.activities.bottom_navigation.CommonActivity
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private lateinit var signInClient: SignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "LoginActivity")
        FirebaseAnalyticsHelper.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)

        signInClient = Identity.getSignInClient(this)

        findViewById<Button>(R.id.btnGoogleSignIn).setOnClickListener {
            signInWithGoogle()
        }

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
                    loginAsUser(email, password, emailAuthManager)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error checking admin credentials", Toast.LENGTH_SHORT).show()
            }
    }

    /**
     * loginAsUser - Attempts to log in as a regular user
     * Redirects to the common activity upon success
     */
    private fun loginAsUser(email: String, password: String, emailAuthManager: EmailAuthManager) {
        emailAuthManager.loginWithEmail(email, password) { success ->
            if (success) {
                Toast.makeText(this, "User login successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, CommonActivity::class.java).apply {
                    putExtra("destination", "dashboard")
                })
                finish()
            } else {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = signInClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                val email = credential.id

                if (idToken != null) {
                    checkIfGoogleAdmin(email, idToken)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Sign in failed", e)
            }
        }
    }

    /**
     * signInWithGoogle - Initiates Google Sign-In process
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
     * checkIfGoogleAdmin - Checks if the signed-in Google account belongs to an admin
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
     * handleGoogleToken - Handles successful Google login for regular users
     */
    private fun handleGoogleToken(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in successful
                    Log.d(TAG, "Google sign-in to Firebase successful")
                    Toast.makeText(this, "Google sign-in successful!", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, CommonActivity::class.java))
                    finish()
                } else {
                    // Sign-in failed
                    Log.e(TAG, "Google sign-in to Firebase failed", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

}
