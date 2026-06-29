package com.example.welfaremanagementsystem

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import android.content.Intent
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class FeeRecordsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var feeListContainer: LinearLayout
    private lateinit var edtSearchFee: EditText

    private val allFees = ArrayList<HashMap<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fee_records)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.feeRecordsMain)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        feeListContainer = findViewById(R.id.feeListContainer)
        edtSearchFee = findViewById(R.id.edtSearchFee)

        loadFees()

        edtSearchFee.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterFees(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })

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

    private fun loadFees() {
        db.collection("fees")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                allFees.clear()

                if (snapshots != null) {
                    for (doc in snapshots) {
                        val fee = hashMapOf(
                            "memberName" to (doc.getString("memberName") ?: ""),
                            "month" to (doc.getString("month") ?: ""),
                            "amount" to (doc.getString("amount") ?: ""),
                            "status" to (doc.getString("status") ?: "Paid")
                        )
                        allFees.add(fee)
                    }
                }

                displayFees(allFees)
            }
    }

    private fun filterFees(keyword: String) {
        val filtered = allFees.filter {
            it["memberName"]!!.contains(keyword, ignoreCase = true) ||
                    it["month"]!!.contains(keyword, ignoreCase = true)
        }

        displayFees(ArrayList(filtered))
    }

    private fun displayFees(fees: ArrayList<HashMap<String, String>>) {
        feeListContainer.removeAllViews()

        for (fee in fees) {
            val card = LinearLayout(this)
            card.orientation = LinearLayout.HORIZONTAL
            card.gravity = Gravity.CENTER_VERTICAL
            card.setPadding(dp(16), dp(10), dp(14), dp(10))
            card.background = getDrawable(R.drawable.white_card)

            val cardParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                dp(76)
            )
            cardParams.setMargins(0, 0, 0, dp(14))
            card.layoutParams = cardParams

            val textBox = LinearLayout(this)
            textBox.orientation = LinearLayout.VERTICAL
            textBox.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )

            val txtName = TextView(this)
            txtName.text = fee["memberName"]
            txtName.textSize = 14f
            txtName.setTextColor(Color.parseColor("#222222"))
            txtName.setTypeface(null, Typeface.BOLD)

            val txtInfo = TextView(this)
            txtInfo.text = "${fee["month"]} • Rs. ${fee["amount"]}"
            txtInfo.textSize = 10f
            txtInfo.setTextColor(Color.parseColor("#6F7A73"))

            textBox.addView(txtName)
            textBox.addView(txtInfo)

            val status = TextView(this)
            status.text = fee["status"]
            status.gravity = Gravity.CENTER
            status.textSize = 10f
            status.setTypeface(null, Typeface.BOLD)
            status.setTextColor(Color.WHITE)
            status.background = getDrawable(R.drawable.green_button)

            val statusParams = LinearLayout.LayoutParams(dp(68), dp(32))
            status.layoutParams = statusParams

            card.addView(textBox)
            card.addView(status)

            feeListContainer.addView(card)
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}