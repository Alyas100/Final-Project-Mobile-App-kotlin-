package com.example.finalprojectmobileapp.ui.activities.bottom_navigation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.ui.activities.LogWorkoutActivity
import com.example.finalprojectmobileapp.ui.activities.RemindersActivity

class MoreFragmentPage : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_more_page, container, false)

        view.findViewById<TextView>(R.id.reminders).setOnClickListener {
            startActivity(Intent(requireContext(), RemindersActivity::class.java))
        }

        view.findViewById<TextView>(R.id.history).setOnClickListener {
            startActivity(Intent(requireContext(), HistoryFragmentPage::class.java))
        }

        return view
    }
}
