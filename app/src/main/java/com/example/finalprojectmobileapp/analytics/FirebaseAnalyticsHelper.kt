package com.example.finalprojectmobileapp.analytics

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase

object FirebaseAnalyticsHelper {
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    // Initialize Firebase Analytics
    fun init(context: Context) {
        firebaseAnalytics = Firebase.analytics
    }

    // Function to log events
    fun logEvent(eventName: String, params: Bundle? = null) {
        firebaseAnalytics.logEvent(eventName, params)
    }

    // Function to log screen views
    fun logScreenView(screenName: String, context: Context) {
        val bundle = Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, context.javaClass.simpleName)
        }
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)
    }
}

