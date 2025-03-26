package com.example.finalprojectmobileapp.ui.activities.admin

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.finalprojectmobileapp.R
import com.google.firebase.firestore.FirebaseFirestore

class DeleteUserActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_users)

        db = FirebaseFirestore.getInstance()

        val edtUserId = findViewById<EditText>(R.id.edtUserId)
        val btnDelete = findViewById<Button>(R.id.btnDelete)

        btnDelete.setOnClickListener {
            val userId = edtUserId.text.toString().trim()
            if (userId.isNotEmpty()) {
                db.collection("users").document(userId).delete()
                    .addOnSuccessListener {
                        Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Enter User ID", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
