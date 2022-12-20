package com.example.appduration

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.databinding.SettingsBinding


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingsBinding
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = SettingsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        nav();
    }


    override fun onStart() {
        super.onStart()
        CheckUseBlockedAppsService.isAppInForeground = true;
    }

    override fun onStop() {
        super.onStop()
        CheckUseBlockedAppsService.isAppInForeground = false;
    }
    fun nav(){
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.blocked -> OpenHome();
                R.id.settings -> OpenMainActivity();
                else -> {}
            }
            true;
        }
    }
    public fun OpenHome() { // open andere pagina
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent);
        overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in,
            com.google.android.material.R.anim.abc_fade_out);
    }
    public fun OpenMainActivity() { // open andere pagina
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent);

        overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in,
            com.google.android.material.R.anim.abc_fade_out);
    }
}