package com.example.finalprojectmobileapp.ui.activities.bottom_navigation.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.analytics.FirebaseAnalyticsHelper
import com.example.finalprojectmobileapp.auth.activities.LoginActivity
import com.example.finalprojectmobileapp.ui.activities.SettingsActivity
import com.example.finalprojectmobileapp.ui.activities.ai.fragment.GeminiSidebarFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class DashboardFragmentPage : Fragment() {

    private lateinit var stepCountTextView: TextView
    private lateinit var calorieTextView: TextView
    private lateinit var distanceTextView: TextView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var youTubePlayerView: YouTubePlayerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        // Firebase Analytics screen view
        val bundle = Bundle()
        bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "DashboardFragment")
        FirebaseAnalyticsHelper.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle)

        // CardView TextViews
        stepCountTextView = view.findViewById(R.id.tvSteps)
        calorieTextView = view.findViewById(R.id.tvCalories)
        distanceTextView = view.findViewById(R.id.distanceTextView)

        // YouTube PlayerView
        youTubePlayerView = view.findViewById(R.id.youtubeWebView)
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                val videoId = "aADukThvjXQ" // YouTube Video ID only
                youTubePlayer.cueVideo(videoId, 0f)
            }
        })

        // DrawerLayout and Toolbar
        drawerLayout = view.findViewById(R.id.drawer_layout)
        navView = view.findViewById(R.id.navigation_view)
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)

        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_settings -> {
                    startActivity(Intent(requireContext(), SettingsActivity::class.java))
                    true
                }
                R.id.nav_logout -> {
                    showLogoutConfirmationDialog()
                    true
                }
                else -> false
            }
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.toolbar_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true

        return when (item.itemId) {
            R.id.action_gemini -> {
                val sidebar = GeminiSidebarFragment()
                sidebar.show(parentFragmentManager, "GeminiSidebar")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ -> performLogout() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        FirebaseAuth.getInstance().signOut()

        val googleSignInClient = GoogleSignIn.getClient(
            requireContext(),
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
        )

        googleSignInClient.signOut().addOnCompleteListener {
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
