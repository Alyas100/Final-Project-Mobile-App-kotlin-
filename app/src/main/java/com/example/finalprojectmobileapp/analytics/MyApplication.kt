package com.example.finalprojectmobileapp.analytics

import android.app.Application

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseAnalyticsHelper.init(this)
    }
}
