package com.example.testmvc.Model

import android.app.usage.UsageStatsManager
import android.content.pm.PackageManager
import com.example.appduration.Functions.GetInstalledApplicationsInfo.Companion.getAllAppsAndTimeStamps
import com.example.appduration.Functions.GetInstalledApplicationsInfo.Companion.getTotalTimeApps
import com.example.testmvc.Controller.MainController
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class RestrictedAppsModel {
    var controller: MainController;

    constructor(controller: MainController){
        this.controller = controller;
    }


    fun findAppsDurationTimes(UsageStatsManager: UsageStatsManager, packageManager: PackageManager) { // zoek gebruik tijden voor alle apps.
        var foregroundAppPackageName : String? = null
        val currentTime = System.currentTimeMillis()
        val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        //https://stackoverflow.com/questions/59113756/android-get-usagestats-per-hour
        //bepaal tijd per app
        //var UsageStatsManager = getSystemService(AppCompatActivity.USAGE_STATS_SERVICE) as UsageStatsManager
        var data = getAllAppsAndTimeStamps(start = start, currentTime = currentTime, UsageStatsManager); // kijk common functions
        var result = getTotalTimeApps(data); // kijk common functions
        controller.onFillPieChart(result.toList().sortedBy { (_, value) -> value}.reversed().toMap() as HashMap<String, Double>, packageManager)
        controller.putinfoonscreen(result.toList().sortedBy { (_, value) -> value}.reversed().toMap() as HashMap<String, Double>, packageManager)
    }

    fun findDurationTimeOverHours(UsageStatsManager: UsageStatsManager){
        var results = ArrayList<Float>();
        for (i in 0 until 6 step 1) { // kijk tijd per uur
            val currentTime = System.currentTimeMillis() - (3600000 * i+ (LocalDateTime.now().second * 1000) + (LocalDateTime.now().minute * 60000));
            val start = System.currentTimeMillis() - (3600000 *(i+1) + (LocalDateTime.now().second * 1000) + (LocalDateTime.now().minute * 60000));//trek 1 h van de tijd af en zet de seconden en minuten op 0
            //https://stackoverflow.com/questions/59113756/android-get-usagestats-per-hour
            //bepaal tijd per app
            var datatime = getAllAppsAndTimeStamps(start = start, currentTime = currentTime, UsageStatsManager);
            var result = getTotalTimeApps(datatime);
            val to = (result.values.sum() / 60000).toFloat()
            results.add(to);
        }
        controller.OnFillBarchart(results);
    }

}