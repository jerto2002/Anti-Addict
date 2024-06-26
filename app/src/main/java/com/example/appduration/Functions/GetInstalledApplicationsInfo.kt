package com.example.appduration.Functions

import android.annotation.SuppressLint
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.example.appduration.View.App
import java.util.*


class GetInstalledApplicationsInfo() {
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

        /* https://stackoverflow.com/questions/59113756/android-get-usagestats-per-hour */
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
                    if(event.packageName.contains("maps")){
                        var test ="";
                    }
                    if(data.get(event.packageName) == null){
                        data.put(event.packageName, ArrayList<UsageEvents.Event>(Arrays.asList(event)));
                    }else{
                        data.get(event.packageName)?.add(event);
                    }
                }
            }
            return data;
        }
        fun getTotalTimeApps(
            data: HashMap<String, ArrayList<UsageEvents.Event>>,
            start: Long,
            currentTime: Long
        ) : HashMap<String, Double> {
            var result = HashMap<String, Double>();
            for((k, v) in data){
                var time = 0.0;
                checkTimeStamp(v,k,0, start, currentTime);
                for (i in 0 until v.size -1 step 1) {
                    checkTimeStamp(v,k,i, start, currentTime);
                    if(v.get(i).eventType == 1 && v.get(i + 1).eventType == 2){
                        time += (v.get(i + 1).timeStamp - v.get(i).timeStamp);
                    }
                }
                if(v.get(0).eventType == 2){
                    time += v.get(0).timeStamp - start;
                }
                if(v.get(v.size -1).eventType == 1) {
                    time += (currentTime - v.get(v.size -1).timeStamp)
                }
                if(k.contains("launcher") || k.contains("sdk")){
                    time = 0.0;
                }
                result.put(k,   time);
            }
            return result;
        }

        fun checkTimeStamp(
            v: ArrayList<UsageEvents.Event>,
            k: String,
            i: Int,
            start: Long,
            currentTime: Long
        ) {
            if(v.get(i).timeStamp < start || v.get(i).timeStamp > currentTime){
                throw Exception("Invalid Timestamp")
            }
        }
        fun getAllAppNames(ApplicationNames: ArrayList<String>, Applist: ArrayList<App>, packageManager: PackageManager, applicationContext: Context) {
            for(App in Applist){
                App.AppName = getAppname(App.packageName, packageManager);
            }
        }


    }
}