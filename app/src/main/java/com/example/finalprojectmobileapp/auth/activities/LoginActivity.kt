package com.example.finalprojectmobileapp.auth.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

class LoginActivity : AppCompatActivity() {

    private lateinit var signInClient: SignInClient
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        // Firebase Analytics tracking
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "LoginActivity")
        FirebaseAnalyticsHelper.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)

        signInClient = Identity.getSignInClient(this)

        // Google Sign-In
        findViewById<Button>(R.id.btnGoogleSignIn).setOnClickListener {
            signInWithGoogle()
        }

        // Navigate to Signup
        findViewById<TextView>(R.id.btnSignup).setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        // Email login
        val emailAuthManager = EmailAuthManager(this)
        findViewById<Button>(R.id.btnLogin).setOnClickListener {
            val email = findViewById<EditText>(R.id.etEmail).text.toString().trim()
            val password = findViewById<EditText>(R.id.etPassword).text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginAsUser(email, password, emailAuthManager)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginAsUser(email: String, password: String, emailAuthManager: EmailAuthManager) {
        emailAuthManager.loginWithEmail(email, password) { success ->
            if (success) {
                checkIfUserIsAdmin(email)
            } else {
                Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkIfUserIsAdmin(email: String) {
        val adminEmail = getString(R.string.admin_email).lowercase()
        val currentEmail = email.lowercase()

        if (currentEmail == adminEmail) {
            Toast.makeText(this, "Welcome, Admin!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, AdminDashboardActivity::class.java))
        } else {
            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, CommonActivity::class.java).apply {
                putExtra("destination", "dashboard")
            })
        }

        finish()
    }

    private val signInLauncher = registerForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credential = signInClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                val email = credential.id

                if (idToken != null && email != null) {
                    handleGoogleToken(idToken, email)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Sign in failed", e)
            }
        }
    }

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

    private fun handleGoogleToken(idToken: String, email: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Google sign-in to Firebase successful")
                    Toast.makeText(this, "Google sign-in successful!", Toast.LENGTH_SHORT).show()
                    checkIfUserIsAdmin(email)
                } else {
                    Log.e(TAG, "Google sign-in to Firebase failed", task.exception)
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
