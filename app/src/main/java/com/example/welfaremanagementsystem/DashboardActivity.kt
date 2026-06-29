package com.example.welfaremanagementsystem

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView


class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboardMain)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        val cardMembers = findViewById<LinearLayout>(R.id.cardMembers)
        val cardFees = findViewById<LinearLayout>(R.id.cardFees)
        val cardMeetings = findViewById<LinearLayout>(R.id.cardMeetings)
        val cardSMS = findViewById<LinearLayout>(R.id.cardSMS)

        cardMembers.setOnClickListener {
            startActivity(Intent(this, MembersActivity::class.java))
        }

        cardFees.setOnClickListener {
            startActivity(Intent(this, RecordFeeActivity::class.java))
        }

        cardMeetings.setOnClickListener {
            startActivity(Intent(this, CreateMeetingActivity::class.java))
        }

        cardSMS.setOnClickListener {
            startActivity(Intent(this, SmsReminderActivity::class.java))
        }
        findViewById<TextView>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        findViewById<TextView>(R.id.navMembers).setOnClickListener {
            startActivity(Intent(this, MembersActivity::class.java))
        }

        findViewById<TextView>(R.id.navFees).setOnClickListener {
            startActivity(Intent(this, FeeRecordsActivity::class.java))
        }

        findViewById<TextView>(R.id.navMore).setOnClickListener {
            Toast.makeText(this, "More clicked", Toast.LENGTH_SHORT).show()
        }
    }
}