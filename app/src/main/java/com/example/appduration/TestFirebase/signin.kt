package com.example.appduration

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.databinding.ActivitySignInBinding
import com.google.firebase.auth.FirebaseAuth

//https://www.youtube.com/watch?v=miJooBq9iwE&list=PLHQRWugvckFry9Q1OT6hLNfyUizT73PwX&index=4

class SigninActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        firebaseAuth = FirebaseAuth.getInstance();

        binding.textView.setOnClickListener{
            val intent = Intent(this, SignupActivity::class.java);
            startActivity(intent);
        }

        binding.button.setOnClickListener {
            val email = binding.emailEt.text.toString();
            val pass = binding.passET.text.toString();

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val intent = Intent(this, MainActivity::class.java);
                        startActivity(intent);
                        Toast.makeText(this, "succesfull login", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(this, "not all parameters are filled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if(firebaseAuth.currentUser != null){
            val intent = Intent(this, MainActivity::class.java);
            startActivity(intent);
        }
    }
}