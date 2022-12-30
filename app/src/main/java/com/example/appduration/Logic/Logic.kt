package com.example.appduration.Logic

import android.app.usage.UsageStatsManager
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.Logic.GetInstalledApplicationsInfo.Companion.getAllAppsAndTimeStamps
import com.example.appduration.Logic.GetInstalledApplicationsInfo.Companion.getTotalTimeApps
import java.time.LocalDate
import java.time.ZoneId

public class Logic : AppCompatActivity() {
        public fun findAppsDurationTimes(UsageStatsManager: UsageStatsManager): java.util.HashMap<String, Double> { // zoek gebruik tijden voor alle apps.
            var foregroundAppPackageName: String? = null
            val currentTime = System.currentTimeMillis()
            val start =
                LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            //https://stackoverflow.com/questions/59113756/android-get-usagestats-per-hour
            //bepaal tijd per app
            var data = getAllAppsAndTimeStamps(
                start = start,
                currentTime = currentTime,
                UsageStatsManager
            ); // kijk common functions
            var result = getTotalTimeApps(data); // kijk common functions
            result = result.toList().sortedBy { (_, value) -> value }.reversed()
                .toMap() as HashMap<String, Double>
            return result;
            //putinfoonscreen(result);
            //fillInChart(result);
            //barchart();
        }
}