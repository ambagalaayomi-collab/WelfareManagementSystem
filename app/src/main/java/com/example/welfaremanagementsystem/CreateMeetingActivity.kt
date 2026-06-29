package com.example.welfaremanagementsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class CreateMeetingActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_meeting)

        val edtTitle = findViewById<EditText>(R.id.edtMeetingTitle)
        val edtDate = findViewById<EditText>(R.id.edtMeetingDate)
        val edtTime = findViewById<EditText>(R.id.edtMeetingTime)
        val edtDescription = findViewById<EditText>(R.id.edtMeetingDescription)
        val btnCreate = findViewById<Button>(R.id.btnCreateMeeting)

        btnCreate.setOnClickListener {
            val title = edtTitle.text.toString().trim()
            val date = edtDate.text.toString().trim()
            val time = edtTime.text.toString().trim()
            val description = edtDescription.text.toString().trim()

            if (title.isEmpty() || date.isEmpty() || time.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val meeting = hashMapOf(
                "title" to title,
                "date" to date,
                "time" to time,
                "description" to description,
                "status" to "New"
            )

            db.collection("meetings")
                .add(meeting)
                .addOnSuccessListener {
                    Toast.makeText(this, "Meeting created successfully", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, MeetingsActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}