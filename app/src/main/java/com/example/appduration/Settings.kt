package com.example.appduration

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.databinding.SettingsBinding
import com.google.android.material.slider.Slider


class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: SettingsBinding


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = SettingsBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        nav();
        settingsBattery();
        settingsTime();
        /*binding.btntest2.setOnClickListener(){
            val intent = Intent(this, FirebaseActivity::class.java);
            startActivity(intent);
            overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in,
                com.google.android.material.R.anim.abc_fade_out);
        }
        binding.btntest3.setOnClickListener(){
            val intent = Intent(this, SigninActivity::class.java);
            startActivity(intent);
            overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in,
                com.google.android.material.R.anim.abc_fade_out);
        }*/
    }

    fun settingsTime(){
        val mPrefs = getSharedPreferences("time", 0)
        val timeLeft = mPrefs.getFloat("savedtime", 100F)
        binding.batteryRemainingTime.value = timeLeft;
        binding.batteryRemainingTime.addOnChangeListener(object: Slider.OnChangeListener{
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                val mEditor = mPrefs.edit()
                mEditor.putFloat("savedtime",  binding.batteryRemainingTime.value).commit()
            }
        })
    }
    fun settingsBattery(){
        val mPrefs = getSharedPreferences("battery", 0)
        val batterySaveMode = mPrefs.getBoolean("savemode", false)
        val batterySaveModePercent = mPrefs.getFloat("percent", 50F)

        if(batterySaveMode){
            binding.BatterySwitch.isChecked = true
        }else{
            binding.Batterysliderlayout.visibility = View.INVISIBLE;
        }
        binding.batterySaveModePercet.value = batterySaveModePercent;

        binding.BatterySwitch.setOnCheckedChangeListener { _, isChecked ->
            val backup = getSharedPreferences("backup", 0)
            val mBackup = backup.edit()
            mBackup.putBoolean("backup", true).commit() //backup
            val mEditor = mPrefs.edit()
            mEditor.putBoolean("savemode", isChecked).commit()
            binding.Batterysliderlayout.visibility = View.INVISIBLE;
            if(isChecked == true){
                binding.Batterysliderlayout.visibility = View.VISIBLE;
            }
        }
        binding.batterySaveModePercet.addOnChangeListener(object: Slider.OnChangeListener{
            override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
                val mEditor = mPrefs.edit()
                mEditor.putFloat("percent",  binding.batterySaveModePercet.value).commit()
            }
        })
    }



    fun nav(){
        binding.bottomNavigationView.setSelectedItemId(R.id.settings);
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.blocked -> OpenBlocked();
                R.id.home -> OpenMainActivity();
                else -> {}
            }
            true;
        }
    }
    public fun OpenBlocked() { // open andere pagina
        val intent = Intent(this, RestricktedAppsActivity::class.java);
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