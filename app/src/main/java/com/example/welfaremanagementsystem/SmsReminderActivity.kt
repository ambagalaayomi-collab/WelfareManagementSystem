package com.example.welfaremanagementsystem

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class SmsReminderActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private val meetingTitles = ArrayList<String>()
    private val meetingDates = ArrayList<String>()
    private val meetingTimes = ArrayList<String>()

    // Replace with your NEW API Token
    private val apiToken = "5671|C8eFVnn52F3e5UHYp4enwMNz3Uwp5I4XwfioOi082b07ce36"

    // Your Sender ID (TextLKDemo or your approved Sender ID)
    private val senderId = "TextLKDemo"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sms_reminder)

        val spinnerMeeting = findViewById<Spinner>(R.id.spinnerMeeting)
        val edtPhoneNumber = findViewById<EditText>(R.id.edtPhoneNumber)
        val txtPreview = findViewById<TextView>(R.id.txtMessagePreview)
        val btnSendSms = findViewById<Button>(R.id.btnSendSms)

        edtPhoneNumber.setTextColor(Color.BLACK)
        edtPhoneNumber.setHintTextColor(Color.GRAY)
        txtPreview.setTextColor(Color.BLACK)

        loadMeetings(spinnerMeeting, txtPreview)

        spinnerMeeting.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {

                    if (position == 0) {
                        txtPreview.text = "Message Preview"
                    } else {
                        txtPreview.text = createMessage(position - 1)
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }

        btnSendSms.setOnClickListener {

            var phone = edtPhoneNumber.text.toString().trim()
            if (phone.startsWith("0")) {
                phone = "94" + phone.substring(1)
            }
            if (spinnerMeeting.selectedItemPosition == 0) {
                Toast.makeText(this, "Select meeting", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val message =
                createMessage(spinnerMeeting.selectedItemPosition - 1)

            sendSmsUsingTextLK(phone, message)
        }
    }

    private fun loadMeetings(spinner: Spinner, preview: TextView) {

        db.collection("meetings")
            .get()
            .addOnSuccessListener { result ->

                meetingTitles.clear()
                meetingDates.clear()
                meetingTimes.clear()

                val spinnerItems = ArrayList<String>()
                spinnerItems.add("Select meeting")

                for (doc in result) {

                    val title = doc.getString("title") ?: ""
                    val date = doc.getString("date") ?: ""
                    val time = doc.getString("time") ?: ""

                    if (title.isNotEmpty()) {

                        meetingTitles.add(title)
                        meetingDates.add(date)
                        meetingTimes.add(time)

                        spinnerItems.add(title)
                    }
                }

                val adapter = object : ArrayAdapter<String>(
                    this,
                    android.R.layout.simple_spinner_item,
                    spinnerItems
                ) {

                    override fun getView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {

                        val tv = super.getView(
                            position,
                            convertView,
                            parent
                        ) as TextView

                        tv.setTextColor(Color.BLACK)
                        tv.textSize = 14f

                        return tv
                    }

                    override fun getDropDownView(
                        position: Int,
                        convertView: View?,
                        parent: ViewGroup
                    ): View {

                        val tv = super.getDropDownView(
                            position,
                            convertView,
                            parent
                        ) as TextView

                        tv.setTextColor(Color.BLACK)
                        tv.setBackgroundColor(Color.WHITE)
                        tv.textSize = 14f

                        return tv
                    }
                }

                adapter.setDropDownViewResource(
                    android.R.layout.simple_spinner_dropdown_item
                )

                spinner.adapter = adapter

                preview.text = "Message Preview"
            }
    }

    private fun createMessage(position: Int): String {

        return """
Dear Member,

This is a reminder that the meeting "${meetingTitles[position]}" will be held on ${meetingDates[position]} at ${meetingTimes[position]}.

Your participation is highly appreciated.

Thank You.
Welfare Management Committee
""".trimIndent()
    }

    private fun sendSmsUsingTextLK(phone: String, message: String) {
        Thread {
            try {
                val client = OkHttpClient()

                val json = JSONObject().apply {
                    put("recipient", phone)
                    put("sender_id", senderId)
                    put("type", "plain")
                    put("message", message)
                }

                val body = json.toString()
                    .toRequestBody("application/json".toMediaType())

                val request = Request.Builder()
                    .url("https://app.text.lk/api/v3/sms/send")
                    .addHeader("Authorization", "Bearer $apiToken")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseText = response.body?.string() ?: "No response"

                runOnUiThread {
                    Toast.makeText(this, responseText, Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "SMS failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}