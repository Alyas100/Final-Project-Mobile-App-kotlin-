package com.example.finalprojectmobileapp.ui.activities.ai.fragment

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.example.finalprojectmobileapp.R
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class GeminiSidebarFragment : DialogFragment() {

    private lateinit var chatOutput: TextView
    private lateinit var chatInput: EditText
    private lateinit var sendButton: Button

    // Gemini AI model instance
    private val generativeModel by lazy {
        GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyBLLoTipv9QKZR96Xn0e7j-kaBa2prTY9k" // Replace with actual API key
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gemini_sidebar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        chatOutput = view.findViewById(R.id.chatOutput)
        chatInput = view.findViewById(R.id.chatInput)
        sendButton = view.findViewById(R.id.sendButton)

        sendButton.setOnClickListener {
            val userMessage = chatInput.text.toString()
            if (userMessage.isNotEmpty()) {
                chatOutput.append("\nYou: $userMessage")
                chatInput.text.clear()
                getAiResponse(userMessage)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout((resources.displayMetrics.widthPixels * 0.45).toInt(), ViewGroup.LayoutParams.MATCH_PARENT)
            setGravity(Gravity.END) // Sidebar opens from the right
        }
    }

    private fun getAiResponse(userMessage: String) {
        lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(userMessage)
                val aiResponse = response.text ?: "No response"

                chatOutput.append("\nGemini AI: $aiResponse")
            } catch (e: Exception) {
                chatOutput.append("\nError: ${e.message}")
            }
        }
    }
}
