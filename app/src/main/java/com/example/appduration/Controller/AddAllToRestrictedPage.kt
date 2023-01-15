package com.example.appduration.Controller

import android.annotation.SuppressLint
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appduration.Functions.CustomAdapter
import com.example.appduration.Functions.GetInstalledApplicationsInfo.Companion.getAllAppNames
import com.example.appduration.Functions.GetInstalledApplicationsInfo.Companion.getAllAppsAndTimeStamps
import com.example.appduration.Functions.GetInstalledApplicationsInfo.Companion.getTotalTimeApps
import com.example.appduration.Functions.SaveAndloadApplistFile.Companion.getContentOutOfFile
import com.example.appduration.Functions.SaveAndloadApplistFile.Companion.writeToFile
import com.example.appduration.View.App
import com.example.appduration.View.ItemsViewModel
import com.example.appduration.databinding.ActivityRestricktedBinding
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class AddAllToRestrictedPage : AppCompatActivity() {


    companion object {//https://stackoverflow.com/questions/53802632/reuse-methods-in-kotlin-android

        var Applist: ArrayList<App> = ArrayList()
        var ApplicationNames: ArrayList<String> = ArrayList()
    @SuppressLint("SuspiciousIndentation")
    public suspend fun addViews(
        packageManager: PackageManager,
        applicationContext: Context,
        recyclerview: RecyclerView,
        progressBar: ProgressBar,
        UsageStatsManager: UsageStatsManager,
        binding: ActivityRestricktedBinding,
        ReaminingTime: Float
    ) {
        var packageManager = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        var resolveInfoList = packageManager.queryIntentActivities(intent, 0) //https://stackoverflow.com/questions/10696121/get-icons-of-all-installed-apps-in-android
        Applist = getContentOutOfFile(applicationContext);
        var t = Applist.size;
        var AppPackagenames = Applist.map { it.packageName }
        for (i in 0 until resolveInfoList.size) {
            if(!AppPackagenames.contains(resolveInfoList.get(i).activityInfo.packageName)){
                Applist.add(App(resolveInfoList.get(i).activityInfo.packageName , false));
            }
        }
        if(t != Applist.size){
            writeToFile(applicationContext, Applist, packageManager);
        }

        var indexes: ArrayList<Int> = ArrayList();
        for (app in Applist){
            if(!resolveInfoList.map { it.activityInfo.packageName }.contains(app.packageName)){
                //Applist.remove(app); geeft oneindige lus
                val test = Applist.indexOf(app) - indexes.size;
                indexes.add(Applist.indexOf(app) - indexes.size);
            }
            if(app.AppName == ""){
                getAllAppNames(ApplicationNames, Applist, packageManager, applicationContext)
            }
        }
        for(index in indexes){
            Applist.removeAt(index)
        }
        /*if(ApplicationNames.size != resolveInfoList.size){

        }*/
        //var mp = Applist;
        InsertContentToViews(packageManager, applicationContext, recyclerview, progressBar, UsageStatsManager, binding, ReaminingTime);
    }
        suspend fun InsertContentToViews(
            packageManager: PackageManager,
            applicationContext: Context,
            recyclerview: RecyclerView,
            progressBar: ProgressBar,
            UsageStatsManager: UsageStatsManager,
            binding: ActivityRestricktedBinding,
            ReaminingTime: Float
        ) { //https://www.geeksforgeeks.org/android-recyclerview-in-kotlin/
            progressBar.visibility = View.VISIBLE;
            // getting the recyclerview by its id

            // this creates a vertical layout Manager
            recyclerview.layoutManager = LinearLayoutManager(applicationContext)
            // ArrayList of class ItemsViewModel
            val data = ArrayList<ItemsViewModel>()

            var appdurationindex = -1;
            for(i in 0 until  Applist.size){
                if(Applist.get(i).AppName == "Anti Addict"){
                    appdurationindex = i;
                }
            }
            if(appdurationindex >= 0){
                Applist.removeAt(appdurationindex);
            }
            for (i in 0 until  Applist.size) {
                var t = Applist.get(i).AppName ;
                data.add(ItemsViewModel(Applist.get(i).AppName, null, Applist, packageManager, applicationContext, i, recyclerview, progressBar, UsageStatsManager, binding))//names

            }
            for (i in 0 until  5) {
                var t = System.currentTimeMillis();
                data.get(i).d = packageManager.getApplicationIcon(Applist.get(i).packageName)

            }
            // This will pass the ArrayList to our Adapter
            val adapter = CustomAdapter(data, ReaminingTime  )
            // Setting the Adapter with the recyclerview
            recyclerview.adapter = adapter;
            var number = 25;
            while(number < data.size){
                LoadmoreApps(data , recyclerview, number, packageManager, ReaminingTime)
                number += 20;
            }
            LoadmoreApps(data , recyclerview, data.size, packageManager, ReaminingTime)
            progressBar.visibility = View.INVISIBLE;
            var time = calculateUsedTime(UsageStatsManager);
            binding.txtTimeRemaining.text = "Time remaining: " + (ReaminingTime - time).toInt().toString() +"min";
            //binding.progressBar.visibility = View.INVISIBLE;
        }
        fun calculateUsedTime(
            UsageStatsManager: UsageStatsManager,
        ): Double {
            val currentTime = System.currentTimeMillis()
            val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            var Appnames = ArrayList<String>();
            for(app in Applist){
                if(app.Blocked){
                    Appnames.add(app.packageName)
                }
            }
            var datatime = getAllAppsAndTimeStamps(start = start, currentTime = currentTime, UsageStatsManager);
            var results = getTotalTimeApps(datatime, start, currentTime);
            var time = 0.0;
            for(result in results) {
                if (Appnames.contains(result.key)){
                    var er = result.key;
                    time += result.value;
                    var l ="";
                }
            }
            time = time /60000
            return time;
            //binding.txtTimeRemaining.text = "Time remaining: " + (ReaminingTime - time).toInt().toString() +"min";
        }

        suspend fun LoadmoreApps(
            data: ArrayList<ItemsViewModel>,
            recyclerview: RecyclerView,
            number: Int,
            packageManager: PackageManager,
            ReaminingTime: Float
        ) = coroutineScope{
            launch {
                for (i in number -20 until  number) {
                    if(i > 0){
                        if(Applist.get(i).AppName != "App duration") {
                            data.get(i).d =
                                packageManager.getApplicationIcon(Applist.get(i).packageName)
                        }
                    }
                }
                val adapter = CustomAdapter(data, ReaminingTime)
                // Setting the Adapter with the recyclerview
                recyclerview.adapter = adapter
            }
        }
    }
}