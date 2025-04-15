package com.example.finalprojectmobileapp.ui.activities.ai.fragment

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.finalprojectmobileapp.R
import com.example.finalprojectmobileapp.data.models.Calories
import com.example.finalprojectmobileapp.data.models.Workout
import com.example.finalprojectmobileapp.data.repositories.CaloriesRepo
import com.example.finalprojectmobileapp.data.repositories.WorkoutRepo
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class GeminiSidebarFragment : DialogFragment() {

    private lateinit var chatOutput: TextView
    private lateinit var chatInput: EditText
    private lateinit var sendButton: Button
    private lateinit var contextButton: Button  // Button to provide context

    // Gemini AI model instance
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash", // or "gemini-pro" depending on your use case
            apiKey = "AIzaSyBLLoTipv9QKZR96Xn0e7j-kaBa2prTY9k" // Replace with your actual API key
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gemini_sidebar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize UI elements from the Gemini sidebar layout
        chatOutput = view.findViewById(R.id.chatOutput)
        chatInput = view.findViewById(R.id.chatInput)
        sendButton = view.findViewById(R.id.sendButton)
        contextButton = view.findViewById(R.id.btnGetAISuggestion)  // Reusing the same ID here

        // Set up send button click listener (to send a custom message to the AI)
        sendButton.setOnClickListener {
            val userMessage = chatInput.text.toString()
            if (userMessage.isNotEmpty()) {
                chatOutput.append("\nYou: $userMessage")
                sendMessageToAI(userMessage)
                chatInput.text.clear()
            }
        }

        // Set up context button click listener (to provide a dynamic context)
        contextButton.setOnClickListener {
            provideContext()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            // Adjust the layout to make the sidebar appear from the right
            setLayout((resources.displayMetrics.widthPixels * 0.45).toInt(), ViewGroup.LayoutParams.MATCH_PARENT)
            setGravity(Gravity.END)
        }
    }

    private fun sendMessageToAI(message: String) {
        lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(message)
                val aiResponse = response.text ?: "No response"
                activity?.runOnUiThread {
                    chatOutput.append("\nAI: $aiResponse")
                }
            } catch (e: Exception) {
                activity?.runOnUiThread {
                    chatOutput.append("\nError: ${e.message}")
                }
            }
        }
    }

    // Generate dynamic context message based on the user's actual workout and meal history
    private fun generateDynamicContext(workout: Workout, calories: Calories): String {
        return """
        Based on your recent workout history and meal logs:
        - Last workout: ${workout.workoutType}, Duration: ${workout.duration} minutes
        - Last meal: ${calories.foodName}, Calories: ${calories.calories} kcal
        
        Suggestion: Consider adjusting your workout intensity or meal plan based on these recent logs.
    """.trimIndent()
    }

    // Provide dynamic context
    private fun provideContext() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            // Fetch workout and calorie history to generate dynamic context
            fetchUserData(userId) { workout, calories ->
                // Generate dynamic context message
                val contextMessage = generateDynamicContext(workout, calories)

                // Append the dynamic context to chat output
                chatOutput.append("\nContext: $contextMessage")

                // Now send the dynamic context to the AI
                sendMessageToAI(contextMessage)
            }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }

    // Fetch the most recent workout and calorie data
    private fun fetchUserData(userId: String, onComplete: (Workout, Calories) -> Unit) {
        val workoutRepo = WorkoutRepo()
        val caloriesRepo = CaloriesRepo()

        // Fetch the latest workout and calorie entries
        workoutRepo.getLastWorkout(userId) { workout ->
            caloriesRepo.getLastCalories(userId) { calories ->
                // Pass both workout and calories to generate the context
                onComplete(workout, calories)
            }
        }
    }
}
