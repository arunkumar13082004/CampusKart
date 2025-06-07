package com.campuskart.app.fragment

import android.app.ProgressDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.campuskart.app.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class RegisterFragment : Fragment() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var confirmPassword: EditText
    private lateinit var signupButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    // EmailJS Constants
    private val SERVICE_ID = "service_y8o8ezw"
    private val TEMPLATE_ID = "template_89qybqa"
    private val USER_ID = "bzeVepbIDvJN-z5jT"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_registerfrag, container, false)
        (activity as AppCompatActivity?)?.supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance("https://campusx-a99d3-default-rtdb.us-central1.firebasedatabase.app").reference

        email = view.findViewById(R.id.editTextEmail)
        password = view.findViewById(R.id.editTextTextPassword)
        confirmPassword = view.findViewById(R.id.editTextTextPassword2)
        signupButton = view.findViewById(R.id.button)

        signupButton.setOnClickListener {
            registerUser()
        }

        return view
    }

    private fun registerUser() {
        val emailText = email.text.toString().trim()
        val passwordText = password.text.toString().trim()
        val confirmPasswordText = confirmPassword.text.toString().trim()

        var isValid = true

        if (emailText.isEmpty()) {
            email.error = "Please enter a valid email"
            isValid = false
        } else if (!emailText.contains("nie.ac.in")) {
            email.error = "Please use your institute email (nie.ac.in)"
            isValid = false
        }

        if (passwordText.isEmpty()) {
            password.error = "Password can't be empty"
            isValid = false
        } else if (passwordText.length < 6) {
            password.error = "Password must be at least 6 characters"
            isValid = false
        }

        if (confirmPasswordText != passwordText) {
            confirmPassword.error = "Passwords do not match"
            isValid = false
        }

        if (!isValid) {
            if (isAdded) {
                Toast.makeText(requireContext(), "Please fix the above errors", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val progressDialog = ProgressDialog(context).apply {
            setMessage("Sit back and relax, we are processing...")
            setCancelable(false)
            show()
        }

        auth.createUserWithEmailAndPassword(emailText, passwordText)
            .addOnCompleteListener { task ->
                progressDialog.dismiss()
                if (task.isSuccessful) {
                    val otp = (1000..9999).random().toString()
                    val currentUser = auth.currentUser
                    currentUser?.let { user ->
                        sendEmailWithEmailJS(emailText, otp)
                        saveUserInfo(emailText, passwordText, user.uid, otp)
                        updateUI(user)
                    }
                } else {
                    if (isAdded) {
                        Toast.makeText(requireContext(), "Sign Up Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun sendEmailWithEmailJS(toEmail: String, otp: String) {
        val queue = Volley.newRequestQueue(requireContext())
        val url = "https://api.emailjs.com/api/v1.0/email/send"

        val calendar = Calendar.getInstance().apply { add(Calendar.MINUTE, 15) }
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val expiryTime = timeFormat.format(calendar.time)

        val requestBody = JSONObject().apply {
            put("service_id", SERVICE_ID)
            put("template_id", TEMPLATE_ID)
            put("user_id", USER_ID)
            put("template_params", JSONObject().apply {
                put("passcode", otp)         // OTP value
                put("time", expiryTime)      // Expiry time
                put("email", toEmail)        // Match your EmailJS template variable exactly
            })
        }

        val request = object : JsonObjectRequest(
            Method.POST, url, requestBody,
            {
                if (isAdded) Toast.makeText(requireContext(), "OTP sent via Email!", Toast.LENGTH_LONG).show()
            },
            { error ->
                if (isAdded) Toast.makeText(requireContext(), "Email sending failed: ${error.message}", Toast.LENGTH_LONG).show()
            }
        ) {
            override fun getHeaders(): Map<String, String> =
                mapOf("Content-Type" to "application/json")
        }

        queue.add(request)
    }

    private fun saveUserInfo(email: String, password: String, uid: String, otp: String) {
        val user = UserModel(email, password, null, null, null, otp, "no")
        database.child("Users").child(uid).setValue(user)
    }

    private fun updateUI(user: FirebaseUser?) {
        fragmentload(fragment_extradetails())
    }

    private fun fragmentload(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.authFrameLayout, fragment)
            .commit()
    }
}
