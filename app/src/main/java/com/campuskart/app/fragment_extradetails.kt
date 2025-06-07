package com.campuskart.app

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class fragment_extradetails : Fragment() {

    private lateinit var phonenum: EditText
    private lateinit var name: EditText
    private lateinit var scholarid: EditText
    private lateinit var submit: Button
    private lateinit var otp: EditText
    private lateinit var dtb: DatabaseReference
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_extradetails, container, false)

        phonenum = view.findViewById(R.id.editTextphone)
        name = view.findViewById(R.id.editTextname)
        otp = view.findViewById(R.id.otpTextid)
        scholarid = view.findViewById(R.id.editTextid)
        submit = view.findViewById(R.id.button)

        dtb = FirebaseDatabase.getInstance("https://campusx-a99d3-default-rtdb.us-central1.firebasedatabase.app").reference
        auth = FirebaseAuth.getInstance()
        user = auth.currentUser!!

        submit.setOnClickListener {
            val phone = phonenum.text.toString().trim()
            val nametxt = name.text.toString().trim()
            val otpnum = otp.text.toString().trim()
            val id = scholarid.text.toString().trim()

            if (phone.isBlank() || nametxt.isBlank() || otpnum.isBlank()) {
                phonenum.error = "Required"
                name.error = "Required"
                otp.error = "Required"
                return@setOnClickListener
            }

            if (phone.length != 10) {
                phonenum.error = "Number should be 10 digits"
                return@setOnClickListener
            }

            if (id.isNotEmpty() && (id.length < 7 || id.length > 8)) {
                scholarid.error = "ID should be 7 or 8 digits"
                return@setOnClickListener
            }

            if (otpnum.length != 4) {
                otp.error = "OTP should be 4 digits"
                return@setOnClickListener
            }

            dtb.child("Users").child(user.uid).get().addOnSuccessListener { snapshot ->
                val storedOtp = snapshot.child("otp").value?.toString() ?: ""

                if (storedOtp == otpnum) {
                    dtb.child("Users").child(user.uid).apply {
                        child("infoentered").setValue("yes")
                        child("phonenum").setValue(phone)
                        child("name").setValue(nametxt)
                        child("scholarid").setValue(id)
                    }

                    if (activity?.intent?.hasExtra("extraDetails") == true) {
                        activity?.onBackPressed()
                    } else {
                        startActivity(Intent(requireContext(), MainActivity::class.java))
                        requireActivity().finish()
                    }
                } else {
                    otp.error = "OTP Mismatch"
                }
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to verify OTP. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
