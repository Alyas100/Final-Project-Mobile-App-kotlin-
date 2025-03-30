package com.example.finalprojectmobileapp.gemini_AI

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.finalprojectmobileapp.R
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch


class GeminiActivity : AppCompatActivity() {
    // Declare UI elements
    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var chatOutput: TextView

    // Create Gemini AI model
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-pro",
            apiKey = "AIzaSyBLLoTipv9QKZR96Xn0e7j-kaBa2prTY9k" // Replace with your actual API key
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ai_toolbar_menu)

        // Initialize UI elements
        messageInput = findViewById(R.id.chatInput)
        sendButton = findViewById(R.id.sendButton)
        chatOutput = findViewById(R.id.chatOutput)

        // Set up send button click listener
        sendButton.setOnClickListener {
            val userMessage = messageInput.text.toString()
            if (userMessage.isNotEmpty()) {
                // Display user message
                chatOutput.append("\nYou: $userMessage")

                // Send message to AI and get response
                sendMessageToAI(userMessage)

                // Clear input field
                messageInput.text.clear()
            }
        }
    }

    private fun sendMessageToAI(message: String) {
        lifecycleScope.launch {
            try {
                // Generate AI response
                val response = generativeModel.generateContent(message)

                // Display AI response
                val aiResponse = response.text ?: "No response"
                runOnUiThread {
                    chatOutput.append("\nAI: $aiResponse")
                }
            } catch (e: Exception) {
                // Handle any errors
                runOnUiThread {
                    chatOutput.append("\nError: ${e.message}")
                }
            }
        }
    }
}