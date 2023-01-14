package com.example.testmvc.Model

import android.app.usage.UsageEvents
import android.app.usage.UsageEvents.Event
import android.app.usage.UsageStatsManager
import android.content.pm.PackageManager
import com.example.appduration.Functions.GetInstalledApplicationsInfo.Companion.getAllAppsAndTimeStamps
import com.example.appduration.Functions.GetInstalledApplicationsInfo.Companion.getTotalTimeApps
import com.example.testmvc.Controller.MainController
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class HomeScreenModel {
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
        var result = getTotalTimeApps(data, start, currentTime); // kijk common functions
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
            //AddtimeForWhenOverHour(datatime, i)
            AddtimeForWhenOverLong(datatime, i);
            var result = getTotalTimeApps(datatime, start, currentTime);
            var to = (result.values.sum() / 60000).toFloat()
            /*for(y in 0 until fullTimeslots.size){
                if((i-1) < fullTimeslots[y] && (i-1) >= 0 && (i - 1) < results.size){
                    if(endTimeslots.size < (y+1) && results[i -1] == 0F){
                        results[i -1] = 60F;
                    }else if(y < endTimeslots.size){
                        if((i-1) < endTimeslots[y] && results[i -1] == 0F) {
                            results[i - 1] = 60F;
                        }
                    }
                }
            }*/
            for((k, v) in ListLong) {
                for (y in 0 until v.size -1 step 1) {
                    if(v.get(y).eventType == 1 && v.get(y + 1).eventType == 2 && (v.get(y+  1).timeStamp - v.get(y).timeStamp) > 60000){
                        var time = (v.get(y + 1).timeStamp - v.get(y).timeStamp) / 60000;
                        for(y in 0 until time step 1){
                            if(y >=0 && results.size > y){
                                results[((results.size - 1 - y).toInt())] = 60F;
                            }
                        }
                    }
                }
                if(v.get(v.size-1).eventType == 2){
                    for(z in i downTo  1){
                        if(results[z-1] == 0F){
                            results[z-1] = 60F;
                        }
                    }
                }
            }
            //ListLong.clear();
            results.add(to);
        }
        controller.OnFillBarchart(results);
    }
    var ListLong = java.util.HashMap<String, java.util.ArrayList<Event>> ();
    fun AddtimeForWhenOverLong(
        data: java.util.HashMap<String, java.util.ArrayList<UsageEvents.Event>>,
        i: Int, ) {
        for((k, v) in data){
            if(v.get(0).eventType == 2 && !k.contains("launcher") && !k.contains("sdk")){
                if(ListLong.get(k) == null){
                    ListLong.put(k, ArrayList<Event>(Arrays.asList(v.get(0))));
                }else{
                    data.get(k)?.add(v.get(0));
                }
            }
            if(v.get(v.size -1).eventType == 1 && !k.contains("launcher") && !k.contains("sdk")) {
                if(ListLong.get(k) == null){
                    ListLong.put(k, ArrayList<Event>(Arrays.asList(v.get(v.size -1))));
                }else{
                    data.get(k)?.add(v.get(v.size -1));
                }
            }
        }
    }

    var fullTimeslots = ArrayList<Int>();
    var endTimeslots =  ArrayList<Int>();
    fun AddtimeForWhenOverHour(
        data: java.util.HashMap<String, java.util.ArrayList<UsageEvents.Event>>,
        i: Int, ) {
        for((k, v) in data){
            if(v.get(0).eventType == 2 && !k.contains("launcher") && !k.contains("sdk") ){//&& !k.contains("appduration")

                endTimeslots.add(i);
            }
            if(v.get(v.size -1).eventType == 1 && !k.contains("launcher") && !k.contains("sdk")) {
                fullTimeslots.add(i);
            }
        }
    }

    /*fun removeWhenUnevenTimeStamps(datatime: java.util.HashMap<String, java.util.ArrayList<UsageEvents.Event>>): java.util.HashMap<String, java.util.ArrayList<UsageEvents.Event>> {
        var indexes: ArrayList<String> = ArrayList();
        for (data in datatime){
            if(data.value.size % 2 != 0){
                indexes.add(data.key);
            }
        }
        for(index in indexes){
            datatime.remove(index)
        }
        return datatime;
    }*/

}