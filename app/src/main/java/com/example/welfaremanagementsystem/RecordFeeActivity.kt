package com.example.welfaremanagementsystem

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore


class RecordFeeActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    private val memberIds = ArrayList<String>()
    private val memberNames = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_record_fee)

        val spinnerMember = findViewById<Spinner>(R.id.spinnerMember)
        val edtFeeAmount = findViewById<EditText>(R.id.edtFeeAmount)
        val edtMonth = findViewById<EditText>(R.id.edtMonth)
        val edtPaidDate = findViewById<EditText>(R.id.edtPaidDate)
        val btnRecord = findViewById<Button>(R.id.btnRecordPayment)
        val btnViewFees = findViewById<Button>(R.id.btnViewFees)

        btnViewFees.setOnClickListener {
            startActivity(Intent(this, FeeRecordsActivity::class.java))
        }

        loadMembers(spinnerMember)

        btnRecord.setOnClickListener {
            if (memberIds.isEmpty()) {
                Toast.makeText(this, "No members found", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val selectedIndex = spinnerMember.selectedItemPosition
            val memberId = memberIds[selectedIndex]
            val memberName = memberNames[selectedIndex]

            val amount = edtFeeAmount.text.toString().trim()
            val month = edtMonth.text.toString().trim()
            val paidDate = edtPaidDate.text.toString().trim()

            if (amount.isEmpty() || month.isEmpty() || paidDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val payment = hashMapOf(
                "memberId" to memberId,
                "memberName" to memberName,
                "amount" to amount,
                "month" to month,
                "paidDate" to paidDate,
                "status" to "Paid"
            )

            db.collection("fees")
                .add(payment)
                .addOnSuccessListener {
                    Toast.makeText(this, "Payment recorded successfully", Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this, FeeRecordsActivity::class.java))
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                }

        }
    }

    private fun loadMembers(spinner: Spinner) {
        db.collection("members")
            .get()
            .addOnSuccessListener { result ->
                memberIds.clear()
                memberNames.clear()

                for (doc in result) {
                    val name = doc.getString("fullName") ?: ""

                    if (name.isNotEmpty()) {
                        memberIds.add(doc.id)
                        memberNames.add(name)
                    }
                }

                if (memberNames.isEmpty()) {
                    memberNames.add("No members available")
                }

                val adapter = ArrayAdapter(
                    this,
                    android.R.layout.simple_spinner_dropdown_item,
                    memberNames
                )

                spinner.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to load members", Toast.LENGTH_SHORT).show()
            }
    }
}