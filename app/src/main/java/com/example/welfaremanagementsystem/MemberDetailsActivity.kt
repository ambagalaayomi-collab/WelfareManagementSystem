package com.example.welfaremanagementsystem

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MemberDetailsActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var memberId: String

    private lateinit var edtName: EditText
    private lateinit var edtNic: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtAddress: EditText
    private lateinit var edtJoinDate: EditText
    private lateinit var btnEditMember: Button
    private lateinit var btnDeleteMember: Button

    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_details)

        db = FirebaseFirestore.getInstance()

        val id = intent.getStringExtra("memberId")
        if (id.isNullOrEmpty()) {
            Toast.makeText(this, "Member ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        memberId = id

        edtName = findViewById(R.id.edtName)
        edtNic = findViewById(R.id.edtNic)
        edtPhone = findViewById(R.id.edtPhone)
        edtAddress = findViewById(R.id.edtAddress)
        edtJoinDate = findViewById(R.id.edtJoinDate)
        btnEditMember = findViewById(R.id.btnEditMember)
        btnDeleteMember = findViewById(R.id.btnDeleteMember)

        setEditMode(false)
        loadMemberDetails()

        btnEditMember.setOnClickListener {
            if (!isEditMode) {
                setEditMode(true)
            } else {
                updateMember()
            }
        }

        btnDeleteMember.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Delete Member")
                .setMessage("Are you sure you want to delete this member?")
                .setPositiveButton("Delete") { _, _ -> deleteMember() }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    private fun loadMemberDetails() {
        db.collection("members").document(memberId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    findViewById<TextView>(R.id.txtMemberId).text = "Member ID: ${memberId.take(6)}"

                    edtName.setText(document.getString("fullName") ?: "")
                    edtNic.setText(document.getString("nic") ?: "")
                    edtPhone.setText(document.getString("phone") ?: "")
                    edtAddress.setText(document.getString("address") ?: "")
                    edtJoinDate.setText(document.getString("joinDate") ?: "")
                } else {
                    Toast.makeText(this, "Member not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updateMember() {
        val memberData = hashMapOf(
            "fullName" to edtName.text.toString().trim(),
            "nic" to edtNic.text.toString().trim(),
            "phone" to edtPhone.text.toString().trim(),
            "address" to edtAddress.text.toString().trim(),
            "joinDate" to edtJoinDate.text.toString().trim()
        )

        if (memberData.values.any { it.isEmpty() }) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("members").document(memberId)
            .set(memberData)
            .addOnSuccessListener {
                Toast.makeText(this, "Member updated successfully", Toast.LENGTH_SHORT).show()
                setEditMode(false)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Update failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setEditMode(enabled: Boolean) {
        isEditMode = enabled

        edtName.isEnabled = enabled
        edtNic.isEnabled = enabled
        edtPhone.isEnabled = enabled
        edtAddress.isEnabled = enabled
        edtJoinDate.isEnabled = enabled

        btnEditMember.text = if (enabled) "SAVE" else "UPDATE"
    }

    private fun deleteMember() {
        db.collection("members").document(memberId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Member deleted", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MembersActivity::class.java))
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Delete failed: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}