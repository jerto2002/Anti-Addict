package com.example.appduration.View

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.appduration.Controller.AddAllToRestrictedPage.Companion.addViews
import com.example.appduration.Controller.AddAllToRestrictedPage.Companion.calculateUsedTime
import com.example.appduration.R
import com.example.appduration.Services.CheckUseBlockedAppsService
import com.example.appduration.databinding.ActivityRestricktedBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async


class RestricktedAppsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRestricktedBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRestricktedBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        binding.progressBar.visibility = View.VISIBLE;
        nav();
        doWorkAsync(MainScope())//https://stackoverflow.com/questions/57770131/create-async-function-in-kotlin
    }
    fun doWorkAsync(scope: CoroutineScope): Deferred<Unit> = scope.async { // voeg alle apps toe en stop het laden + bepaal totale tijd.
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        var UsageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val mPrefstime = getSharedPreferences("time", 0)
        val ReaminingTime = mPrefstime.getFloat("savedtime", 100F)
        addViews(packageManager, applicationContext, recyclerview, binding.progressBar, UsageStatsManager, binding, ReaminingTime)

        //addViews(packageManager, applicationContext, recyclerview, binding.progressBar, UsageStatsManager, binding, ReaminingTime)
        binding.progressBar.visibility = View.INVISIBLE;

        var time = calculateUsedTime(UsageStatsManager);
        binding.txtTimeRemaining.text = "Time remaining: " + (ReaminingTime - time).toInt().toString() +"min";
        return@async
    }

    override fun onStart() {
        super.onStart()
        CheckUseBlockedAppsService.isAppInForeground = true;
    }

    override fun onStop() {
        super.onStop()
        CheckUseBlockedAppsService.isAppInForeground = false;
        makeBackup();
    }
    fun makeBackup(){ // probeer in commom te krijgen
        val backup = getSharedPreferences("backup", 0)
        val mBackup = backup.edit()
        mBackup.putBoolean("backup", true).commit() //backup
    }

    fun nav(){ //https://www.youtube.com/watch?v=YlIHxIAoHzU&t=619s
        binding.bottomNavigationView.setSelectedItemId(R.id.blocked);
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> OpenHome();
                R.id.settings -> OpenSettingsActivity();
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
    public fun OpenSettingsActivity() { // open andere pagina
        val intent = Intent(this, SettingsActivity::class.java);
        startActivity(intent);

        overridePendingTransition(androidx.appcompat.R.anim.abc_fade_in,
            com.google.android.material.R.anim.abc_fade_out);
    }
}

class App(val packageName : String = "", var Blocked : Boolean = false, var AppName : String = "")
data class ItemsViewModel(
    var text: String,
    var d: Drawable?,
    val Applist: ArrayList<App>,
    val packageManager: PackageManager,
    val applicationContext: Context,
    val i: Int,
    val recyclerview: RecyclerView,
    val progressBar: ProgressBar,
    val UsageStatsManager : UsageStatsManager,
    val binding: ActivityRestricktedBinding
)
