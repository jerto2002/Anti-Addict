package com.example.appduration

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.databinding.ActivityRestricktedBinding


class RestricktedAppsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestricktedBinding
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRestricktedBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
    }
}