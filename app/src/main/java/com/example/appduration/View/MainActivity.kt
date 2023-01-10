package com.example.appduration.View

import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.R
import com.example.appduration.Services.CheckUseBlockedAppsService
import com.example.appduration.WindowService
import com.example.appduration.databinding.ActivityMainBinding
import com.example.testmvc.Controller.MainController
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


class MainActivity : AppCompatActivity() { //view
    private var mainController =  MainController(this);
    private lateinit var binding: ActivityMainBinding
    var piechart : PieChart? = null;
    var barchart : BarChart? = null;
    var textViews = ArrayList<TextView>()
    var iconimages = ArrayList<CircleImageView>()
    var textViewsSocials = ArrayList<TextView>()
    var iconimagesSocials = ArrayList<CircleImageView>()
    var MosedusedApps: RelativeLayout? = null;
    var MosedusedSocialApps: RelativeLayout? = null;
    var Middlesocials: View? = null;
    var SocailsVertical: View? = null;
    var title2 : TextView? = null;
    var title3 : TextView? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        startFourgroundService();
        bindMoseyedAppsLayout();
        var UsageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        if ( checkUsageStatsPermission() ) { // als we de toestemming hebben voor usage stats vind gebruik tijd voor alle apps
            mainController.FillPieChart(UsageStatsManager, packageManager);
            mainController.FillBarChart(UsageStatsManager);
        }
        else {
            // Navigate the user to the permission settings
            Intent( Settings.ACTION_USAGE_ACCESS_SETTINGS ).apply {
                startActivity( this )
            }
            while(!checkUsageStatsPermission()){}
            mainController.FillPieChart(UsageStatsManager, packageManager);
            mainController.FillBarChart(UsageStatsManager);
        }
        checkOverlayPermission();
        nav();

    }
    override fun onStart() {
        super.onStart();
        CheckUseBlockedAppsService.testheropstart = true;
        var serviceIntent = Intent(this, WindowService::class.java);
        stopService(serviceIntent);
    }

    private fun startFourgroundService(){
        if(!foregroundSerciceRunning()){ // start de CheckUseBlockedAppsService als deze nog niet is gestart (zet later in andere activity).
            var serviceIntent = Intent(this, CheckUseBlockedAppsService::class.java);// service for check time

            startForegroundService(serviceIntent);
            //https://stackoverflow.com/questions/33114063/how-do-i-properly-fire-action-request-ignore-battery-optimizations-intent
            //https://stackoverflow.com/questions/39256501/check-if-battery-optimization-is-enabled-or-not-for-an-app
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                val myIntent = Intent()
                myIntent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
                startActivity(myIntent)
            }
        }
    }
    private fun bindMoseyedAppsLayout(){
        piechart = binding.chart;
        barchart = binding.barchart;

        textViews.addAll(Arrays.asList(binding.txtapp1, binding.txtapp2, binding.txtapp3, binding.txtapp4));
        iconimages.addAll(Arrays.asList(binding.iconimage1,binding.iconimage2,binding.iconimage3,binding.iconimage4));
        textViewsSocials.addAll(Arrays.asList(binding.txtappSocial1, binding.txtappSocial2, binding.txtappSocial3, binding.txtappSocial4));
        iconimagesSocials.addAll(Arrays.asList(binding.iconimageSocial1, binding.iconimageSocial2, binding.iconimageSocial3, binding.iconimageSocial4));

        MosedusedApps = binding.MosedusedApps;
        MosedusedSocialApps = binding.MosedusedSocialApps;
        Middlesocials = binding.middlesocials;
        SocailsVertical = binding.socailsVertical;
        title2 = binding.title2;
        title3 = binding.title3;
    }

    private fun checkUsageStatsPermission() : Boolean { //bekijk UsageStatsPermission
        val appOpsManager = getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
        // `AppOpsManager.checkOpNoThrow` is deprecated from Android Q
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats",
                android.os.Process.myUid(), packageName
            )
        }
        else {
            appOpsManager.checkOpNoThrow(
                "android:get_usage_stats",
                android.os.Process.myUid(), packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }
    // method to ask user to grant the Overlay permission
    fun checkOverlayPermission() { // kijk voor toegang  zodat we over andere apps kunnen tekenen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(myIntent)
            }
        }
    }

    private fun foregroundSerciceRunning(): Boolean { // kijk of CheckUseBlockedAppsService is gestart.
        //https://www.youtube.com/watch?v=bA7v1Ubjlzw
        var activitymanager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager;
        for (servise in activitymanager.getRunningServices(Integer.MAX_VALUE)) {
            if (CheckUseBlockedAppsService::class.java.name.equals(servise.service.className)) {
                return true;
            }
        }
        return false;
    }


    fun nav(){
        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.blocked -> OpenRestrickedActivity();
                R.id.settings -> OpenSettingsActivity();
                else -> {}
            }
            true;
        }
    }

    public fun OpenRestrickedActivity() { // open andere pagina
        val intent = Intent(this, RestricktedAppsActivity::class.java);
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
