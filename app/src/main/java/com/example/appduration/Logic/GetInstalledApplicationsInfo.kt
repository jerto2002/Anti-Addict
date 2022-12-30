package com.example.appduration.Logic

import android.annotation.SuppressLint
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import java.util.*

class GetInstalledApplicationsInfo {
    companion object {//https://stackoverflow.com/questions/53802632/reuse-methods-in-kotlin-android
    @SuppressLint("SuspiciousIndentation")
    fun getAppname(packagenaam: String, packageManager : PackageManager): String{ // https://stackoverflow.com/questions/11229219/android-how-to-get-application-name-not-package-name
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo = packageManager.getApplicationInfo(
                packagenaam, 0
            )
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d("TAG", "The package with the given name cannot be found on the system.")
        }
        if (applicationInfo != null) {
            return packageManager.getApplicationLabel(applicationInfo).toString()
        }
        return "";
    }

        //https://stackoverflow.com/questions/59113756/android-get-usagestats-per-hour
        fun getAllAppsAndTimeStamps(start: Long, currentTime: Long, usageStatsManager: UsageStatsManager) : HashMap<String, ArrayList<UsageEvents.Event>> {
            //test niet als app in klein schermje zit (youtube of maps als voorbeeld) andere apps hebben dit ook
            val stats = usageStatsManager.queryEvents(start, currentTime)
            var data : HashMap<String, ArrayList<UsageEvents.Event>>
                    = HashMap<String,  ArrayList<UsageEvents.Event>> ();
            //get start en stop tijden
            while (stats.hasNextEvent()) {
                val event = UsageEvents.Event()
                stats.getNextEvent(event)
                if(event.eventType == UsageEvents.Event.ACTIVITY_RESUMED||
                    event.eventType == UsageEvents.Event.ACTIVITY_PAUSED){
                    if(data.get(event.packageName) == null){
                        data.put(event.packageName, ArrayList<UsageEvents.Event>(Arrays.asList(event)));
                    }else{
                        data.get(event.packageName)?.add(event);
                    }
                }
            }
            return data;
        }
        fun getTotalTimeApps(data: HashMap<String, ArrayList<UsageEvents.Event>>) : HashMap<String, Double> {
            var result = HashMap<String, Double>();
            for((k, v) in data){
                var time = 0.0;
                for (i in 0 until v.size -1 step 1) {
                    if(v.get(i).eventType == 1 && v.get(i + 1).eventType == 2){
                        time += (v.get(i + 1).timeStamp - v.get(i).timeStamp);
                    }
                }
                if(v.get(v.size -1).eventType == 1){
                    time += System.currentTimeMillis() - v.get(v.size -1).timeStamp;
                }
                result.put(k,   time);
            }
            return result;
        }
    }
}