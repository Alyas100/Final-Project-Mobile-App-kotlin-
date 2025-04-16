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
import com.example.finalprojectmobileapp.auth.activities.LoginActivity
import com.example.finalprojectmobileapp.ui.activities.HistoryActivity
import com.example.finalprojectmobileapp.ui.activities.RemindersActivity
import com.example.finalprojectmobileapp.ui.activities.SettingsActivity
import com.example.finalprojectmobileapp.ui.activities.ai.fragment.GeminiSidebarFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth

class MoreFragmentPage : Fragment() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_more_page, container, false)

        // 🎥 Set up Toolbar
        val toolbar: Toolbar = view.findViewById(R.id.toolbar)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        // Set up the drawer layout and toggle
        drawerLayout = view.findViewById(R.id.drawer_layout)
        navView = view.findViewById(R.id.navigation_view)

        toggle = ActionBarDrawerToggle(requireActivity(), drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Handle navigation menu item clicks
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

        // Handle button clicks for navigating to Reminders and History activities
        view.findViewById<TextView>(R.id.reminders).setOnClickListener {
            startActivity(Intent(requireContext(), RemindersActivity::class.java))
        }

        view.findViewById<TextView>(R.id.history).setOnClickListener {
            startActivity(Intent(requireContext(), HistoryActivity::class.java))
        }

        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu with the Gemini icon
        inflater.inflate(R.menu.drawer_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle the action bar item clicks
        if (::toggle.isInitialized && toggle.onOptionsItemSelected(item)) {
            return true
        }

        return when (item.itemId) {
            R.id.action_gemini -> {
                // Show Gemini Sidebar Fragment when the icon is clicked
                val sidebar = GeminiSidebarFragment()
                sidebar.show(parentFragmentManager, "GeminiSidebar")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showLogoutConfirmationDialog() {
        // Show a dialog confirming the logout
        AlertDialog.Builder(requireContext())
            .setTitle("Logout")
            .setMessage("Are you sure you want to log out?")
            .setPositiveButton("Yes") { _, _ -> performLogout() }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performLogout() {
        // Perform logout from Firebase and Google SignIn
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
