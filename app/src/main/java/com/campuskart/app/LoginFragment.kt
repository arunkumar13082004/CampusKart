package com.campuskart.app

import android.app.ActionBar
import android.app.ProgressDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.campuskart.app.fragment.RegisterFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class LoginFragment : Fragment() {

    lateinit var register: TextView
    lateinit var actionbar: ActionBar
    private lateinit var email: EditText
    private lateinit var pass: EditText
    private lateinit var signinbtn: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var emailtxt:String
    private lateinit var passtxt:String
    private  lateinit var forgot:TextView
    private lateinit var dtb:DatabaseReference





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_login, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        register = view.findViewById(R.id.textViewregister)
       register.setPaintFlags(register.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        email = view.findViewById(R.id.editTextTextPersonName)

        pass = view.findViewById(R.id.editTextTextPassword)
        forgot = view.findViewById(R.id.textViewforgot)
        forgot.setPaintFlags(forgot.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
        signinbtn = view.findViewById(R.id.button)
        dtb = FirebaseDatabase.getInstance("https://campusx-a99d3-default-rtdb.us-central1.firebasedatabase.app").reference


        forgot.setOnClickListener {
            fragmentload(fragment_forgotpass())

        }

        auth = FirebaseAuth.getInstance()
        register.setOnClickListener {
            fragmentload(RegisterFragment())


        }
        signinbtn.setOnClickListener {
            emailtxt = email.text.toString().trim()
            passtxt = pass.text.toString().trim()
            var ct =0
            if(emailtxt.isBlank())
            {
                Toast.makeText(requireContext(), "Please, Enter Valid email", Toast.LENGTH_SHORT)
                    .show()
                email.setError("Please, Enter Valid email")
            }
            else
                ct++
            if(passtxt.isBlank()) {
                Toast.makeText(requireContext(), "Password can't be empty", Toast.LENGTH_SHORT)
                    .show()
                pass.setError("Password can't be empty")
            }
            else
                ct++
            if(ct==2) {
                val pd = ProgressDialog(context);
                pd.setMessage("Sit back and relax,we are processing");
                pd.show()
                pd.setCancelable(false)
                auth.signInWithEmailAndPassword(emailtxt, passtxt)
                    .addOnCompleteListener() { task ->
                        if (task.isSuccessful) {
                            pd.hide()
                            dtb.child("Users").child(auth.currentUser?.uid.toString()).get().addOnSuccessListener {
                                val check = it.child("infoentered").toString();

                                if(check.contains("yes")) {
                                    Toast.makeText(
                                        requireContext(), "Authentication Successful.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(requireContext(), MainActivity::class.java)
                                    startActivity(intent)


                                    activity?.finish()






                                }
                                else
                                {


                                    fragmentload(fragment_extradetails());

                                }

                            }.addOnFailureListener{

                            }


                            }



                         else {
                            // If sign in fails, display a message to the user.
                            println("Error reason" + task.exception)
                            Log.w(TAG, "signInWithEmail:failure", task.exception)
                            pd.hide()
                            Toast.makeText(
                                requireContext(), "Authentication failed." + task.exception,
                                Toast.LENGTH_SHORT
                            ).show()
                            // updateUI(null)
                        }
                    }
            }
        }




        return view

    }



    private fun fragmentload(fragment : Fragment)
    {

        val fragmentTransaction = parentFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.authFrameLayout, fragment)
        fragmentTransaction.commit()

    }






}

