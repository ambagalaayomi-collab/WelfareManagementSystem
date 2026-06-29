package com.example.welfaremanagementsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AddMemberActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_member)

        val edtName = findViewById<EditText>(R.id.edtName)
        val edtNic = findViewById<EditText>(R.id.edtNic)
        val edtPhone = findViewById<EditText>(R.id.edtPhone)
        val edtAddress = findViewById<EditText>(R.id.edtAddress)
        val edtJoinDate = findViewById<EditText>(R.id.edtJoinDate)
        val btnSave = findViewById<Button>(R.id.btnSaveMember)

        val db = FirebaseFirestore.getInstance()

        val isUpdate = intent.getBooleanExtra("isUpdate", false)
        val memberId = intent.getStringExtra("memberId")

        if (isUpdate) {
            btnSave.text = "UPDATE MEMBER"

            edtName.setText(intent.getStringExtra("fullName") ?: "")
            edtNic.setText(intent.getStringExtra("nic") ?: "")
            edtPhone.setText(intent.getStringExtra("phone") ?: "")
            edtAddress.setText(intent.getStringExtra("address") ?: "")
            edtJoinDate.setText(intent.getStringExtra("joinDate") ?: "")
        }

        btnSave.setOnClickListener {
            val memberData = hashMapOf(
                "fullName" to edtName.text.toString().trim(),
                "nic" to edtNic.text.toString().trim(),
                "phone" to edtPhone.text.toString().trim(),
                "address" to edtAddress.text.toString().trim(),
                "joinDate" to edtJoinDate.text.toString().trim()
            )

            if (memberData.values.any { it.isEmpty() }) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isUpdate && !memberId.isNullOrEmpty()) {
                db.collection("members").document(memberId)
                    .set(memberData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Member updated", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MemberDetailsActivity::class.java)
                        intent.putExtra("memberId", memberId)
                        startActivity(intent)
                        finish()
                    }
            } else {
                db.collection("members")
                    .add(memberData)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Member saved", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MembersActivity::class.java))
                        finish()
                    }
            }
        }
    }
}