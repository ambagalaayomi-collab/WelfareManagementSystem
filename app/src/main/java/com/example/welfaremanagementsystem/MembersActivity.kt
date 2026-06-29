package com.example.welfaremanagementsystem

import android.content.Intent
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
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore


class MembersActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var memberListContainer: LinearLayout
    private lateinit var edtSearch: EditText

    private val allMembers = ArrayList<HashMap<String, String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_members)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.membersMain)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
            insets
        }

        db = FirebaseFirestore.getInstance()

        edtSearch = findViewById(R.id.edtSearchMember)
        memberListContainer = findViewById(R.id.memberListContainer)

        val btnAddMember = findViewById<TextView>(R.id.btnAddMember)

        btnAddMember.setOnClickListener {
            startActivity(Intent(this, AddMemberActivity::class.java))
        }

        loadMembers()

        edtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterMembers(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadMembers() {
        db.collection("members")
            .addSnapshotListener { snapshots, error ->
                if (error != null) {
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                allMembers.clear()

                if (snapshots != null) {
                    for (doc in snapshots) {
                        val fullName = doc.getString("fullName") ?: ""

                        if (fullName.isNotEmpty()) {
                            val member = hashMapOf(
                                "id" to doc.id,
                                "fullName" to fullName,
                                "nic" to (doc.getString("nic") ?: ""),
                                "phone" to (doc.getString("phone") ?: ""),
                                "address" to (doc.getString("address") ?: ""),
                                "joinDate" to (doc.getString("joinDate") ?: "")
                            )
                            allMembers.add(member)
                        }
                    }
                }

                displayMembers(allMembers)
            }
    }

    private fun filterMembers(keyword: String) {
        val filtered = allMembers.filter {
            it["fullName"]!!.contains(keyword, ignoreCase = true) ||
                    it["nic"]!!.contains(keyword, ignoreCase = true) ||
                    it["phone"]!!.contains(keyword, ignoreCase = true)
        }

        displayMembers(ArrayList(filtered))
    }

    private fun displayMembers(members: ArrayList<HashMap<String, String>>) {
        memberListContainer.removeAllViews()

        for (member in members) {
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

            val name = TextView(this)
            name.text = member["fullName"]
            name.textSize = 14f
            name.setTextColor(Color.parseColor("#222222"))
            name.setTypeface(null, Typeface.BOLD)

            val details = TextView(this)
            details.text = "NIC: ${member["nic"]} • ${member["phone"]}"
            details.textSize = 10f
            details.setTextColor(Color.parseColor("#6F7A73"))

            textBox.addView(name)
            textBox.addView(details)

            val btnView = TextView(this)
            btnView.text = "View"
            btnView.gravity = Gravity.CENTER
            btnView.textSize = 10f
            btnView.setTypeface(null, Typeface.BOLD)
            btnView.setTextColor(Color.WHITE)
            btnView.background = getDrawable(R.drawable.green_button)

            val btnParams = LinearLayout.LayoutParams(dp(64), dp(32))
            btnView.layoutParams = btnParams

            btnView.setOnClickListener {
                val intent = Intent(this, MemberDetailsActivity::class.java)
                intent.putExtra("memberId", member["id"])
                startActivity(intent)
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

            card.addView(textBox)
            card.addView(btnView)
            memberListContainer.addView(card)
        }
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}