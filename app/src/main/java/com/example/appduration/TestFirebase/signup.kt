package com.example.appduration

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth

//https://www.youtube.com/watch?v=miJooBq9iwE&list=PLHQRWugvckFry9Q1OT6hLNfyUizT73PwX&index=4

class SignupActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var firebaseAuth: FirebaseAuth
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        firebaseAuth = FirebaseAuth.getInstance();

        binding.textView.setOnClickListener{
            val intent = Intent(this, SigninActivity::class.java);
            startActivity(intent);
        }
        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString();
            val pass = binding.passET.text.toString();
            val confirmPass = binding.confirmPassEt.text.toString();

            if (email.isNotEmpty() && pass.isNotEmpty() && confirmPass.isNotEmpty()){
                if (pass.equals(confirmPass)){

                    firebaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener {
                        if (it.isSuccessful) {
                            sendEmailVerification();
                            firebaseAuth.signOut();
                            val intent = Intent(this, SigninActivity::class.java);
                            startActivity(intent);
                            Toast.makeText(this, "account created", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT)
                                .show();
                        }
                    }

                }else{
                    Toast.makeText(this, "passwords not matching", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "not all parameters are filled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private fun sendEmailVerification() {
        //get current user
        val firebaseUser = firebaseAuth.currentUser

        //send email verification
        firebaseUser!!.sendEmailVerification()
            .addOnSuccessListener {
                Toast.makeText(applicationContext, "Instructions Sent...", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(applicationContext, "Failed to send due to " + e.message, Toast.LENGTH_SHORT).show()
            }

    }
}