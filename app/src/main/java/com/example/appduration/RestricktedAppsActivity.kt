package com.example.appduration

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
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
        AddUsersToScreen();
    }
    public fun AddUsersToScreen() {
        /*for(user in articles){
            var inflater = LayoutInflater.from(this).inflate(R.layout.activity_restrickted, null)
            binding.userLinearview.addView(inflater, binding.userLinearview.childCount)
        }*/
        var inflater = LayoutInflater.from(this).inflate(R.layout.showappblock, null)
        binding.userLinearview.addView(inflater, binding.userLinearview.childCount)
        inflater = LayoutInflater.from(this).inflate(R.layout.showappblock, null)
        binding.userLinearview.addView(inflater, binding.userLinearview.childCount)
        val count = binding.userLinearview.childCount
        var v: View?
        for (i in 0 until count) {
            v = binding.userLinearview.getChildAt(i)
        }
    }
}