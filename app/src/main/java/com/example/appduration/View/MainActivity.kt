package com.example.appduration.View

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
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

}
