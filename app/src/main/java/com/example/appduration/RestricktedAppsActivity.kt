package com.example.appduration

import SaveAndloadApplistFile.Companion.getContentOutOfFile
import SaveAndloadApplistFile.Companion.writeToFile
import TestcommonFunctions.Companion.getAllAppsAndTimeStamps
import TestcommonFunctions.Companion.getTotalTimeApps
import android.annotation.SuppressLint
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.databinding.ActivityRestricktedBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import java.time.LocalDate
import java.time.ZoneId


class RestricktedAppsActivity : AppCompatActivity() {
    var Applist: ArrayList<App> = ArrayList()
    private lateinit var binding: ActivityRestricktedBinding
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRestricktedBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        binding.progressBar.visibility = View.INVISIBLE;
        doWorkAsync(MainScope())//https://stackoverflow.com/questions/57770131/create-async-function-in-kotlin
    }
    fun doWorkAsync(scope: CoroutineScope): Deferred<Unit> = scope.async {
        addViews()
        return@async
    }

   @SuppressLint("SuspiciousIndentation")
    private fun addViews(){
        var packageManager = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        var resolveInfoList = packageManager.queryIntentActivities(intent, 0) //https://stackoverflow.com/questions/10696121/get-icons-of-all-installed-apps-in-android
        Applist = getContentOutOfFile(applicationContext);
       var Appnames = Applist.map { it.packageName }
       for (app in Applist){
           if(!resolveInfoList.map { it.activityInfo.packageName }.contains(app.packageName)){
               Applist.remove(app);
               writeToFile(applicationContext, Applist);
           }
       }
        /*for (i in 0 until resolveInfoList.size) {
            var inflater = LayoutInflater.from(this).inflate(R.layout.showappblock, null)
            binding.userLinearview.addView(inflater, binding.userLinearview.childCount)
            if(!Appnames.contains(resolveInfoList.get(i).activityInfo.packageName)){
                Applist.add(App(resolveInfoList.get(i).activityInfo.packageName , false))
            }
        }*/
       /* writeToFile(applicationContext, Applist);
        //resolveInfoList.sortBy { TestcommonFuntins.getAppname(it.activityInfo.packageName, packageManager ) }
       Applist.sortWith(compareBy({it.Blocked.toString().length}, { TestcommonFunctions.getAppname(it.packageName, packageManager ) })) // zorgt voor laden
       AddAllAppsonScreen.setAppsonscreen(Applist, packageManager, binding, applicationContext);
        val test = "";*/
    }
    fun makeViews(scope: CoroutineScope): Deferred<Unit> = scope.async {
        addViews()
        return@async
    }

    fun calculateUsedTime(){
        val currentTime = System.currentTimeMillis()
        val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        var Appnames = ArrayList<String>();
        for(app in Applist){
            if(app.Blocked){
                Appnames.add(app.packageName)
            }
        }
        var UsageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        var datatime = getAllAppsAndTimeStamps(start = start, currentTime = currentTime, UsageStatsManager);
        var results = getTotalTimeApps(datatime);
        var time = 0.0;
        for(result in results) {
            if (Appnames.contains(result.key)){
                var er = result.key;
                time = result.value;
                var l ="";
            }
        }
        time = time /60000
        binding.txtTimeRemaining.text = "Time remaining: " + (60 - time).toInt().toString() +"min";
    }
}

class App(val packageName : String, var Blocked : Boolean = false)
