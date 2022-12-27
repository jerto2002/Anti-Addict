package com.example.appduration

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.databinding.TestfirebaseBinding


class FirebaseActivity : AppCompatActivity() {
    private lateinit var binding: TestfirebaseBinding
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = TestfirebaseBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        testfirebase();
    }
    fun testfirebase(){
        //val firebase : DatabaseReference = FirebaseDatabase.getInstance().getReference();
        binding.btnfetch.setOnClickListener(){

        }
        binding.btninsert.setOnClickListener(){

        }
    }
}