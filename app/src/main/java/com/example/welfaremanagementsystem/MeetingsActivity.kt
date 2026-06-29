package com.example.welfaremanagementsystem

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MeetingsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var meetingListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meetings)

        meetingListContainer = findViewById(R.id.meetingListContainer)

        loadMeetings()
    }

    private fun loadMeetings() {
        db.collection("meetings")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                meetingListContainer.removeAllViews()

                if (snapshots != null) {
                    for (doc in snapshots) {
                        val title = doc.getString("title") ?: ""
                        val date = doc.getString("date") ?: ""
                        val time = doc.getString("time") ?: ""
                        val statusText = doc.getString("status") ?: "New"

                        addMeetingCard(title, "$date • $time", statusText)
                    }
                }
            }
    }

    private fun addMeetingCard(titleText: String, infoText: String, statusValue: String) {
        val card = LinearLayout(this)
        card.orientation = LinearLayout.HORIZONTAL
        card.gravity = Gravity.CENTER_VERTICAL
        card.setPadding(dp(16), dp(10), dp(14), dp(10))
        card.background = getDrawable(R.drawable.white_card)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(76)
        )
        params.setMargins(0, 0, 0, dp(14))
        card.layoutParams = params

        val textBox = LinearLayout(this)
        textBox.orientation = LinearLayout.VERTICAL
        textBox.layoutParams = LinearLayout.LayoutParams(
            0,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f
        )

        val title = TextView(this)
        title.text = titleText
        title.textSize = 14f
        title.setTextColor(Color.parseColor("#222222"))
        title.setTypeface(null, Typeface.BOLD)

        val info = TextView(this)
        info.text = infoText
        info.textSize = 10f
        info.setTextColor(Color.parseColor("#6F7A73"))

        textBox.addView(title)
        textBox.addView(info)

        val status = TextView(this)
        status.text = statusValue
        status.gravity = Gravity.CENTER
        status.textSize = 10f
        status.setTypeface(null, Typeface.BOLD)
        status.setTextColor(Color.WHITE)
        status.background = getDrawable(R.drawable.green_button)
        status.layoutParams = LinearLayout.LayoutParams(dp(70), dp(32))

        card.addView(textBox)
        card.addView(status)
        meetingListContainer.addView(card)
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}