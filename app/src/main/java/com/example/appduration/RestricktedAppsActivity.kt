package com.example.appduration

import CustomAdapter
import SaveAndloadApplistFile.Companion.getContentOutOfFile
import SaveAndloadApplistFile.Companion.writeToFile
import TestcommonFunctions
import TestcommonFunctions.Companion.getAllAppsAndTimeStamps
import TestcommonFunctions.Companion.getTotalTimeApps
import android.annotation.SuppressLint
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appduration.databinding.ActivityRestricktedBinding
import kotlinx.coroutines.*
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.ZoneId


class RestricktedAppsActivity : AppCompatActivity() {
    var Applist: ArrayList<App> = ArrayList()
    var ApplicationNames: ArrayList<String> = ArrayList()
    private lateinit var binding: ActivityRestricktedBinding
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRestricktedBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        binding.progressBar.visibility = View.VISIBLE;
        doWorkAsync(MainScope())//https://stackoverflow.com/questions/57770131/create-async-function-in-kotlin
    }
    fun doWorkAsync(scope: CoroutineScope): Deferred<Unit> = scope.async {
        addViews()
        return@async
    }

   @SuppressLint("SuspiciousIndentation")
    private suspend fun addViews(){
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

       for (app in Applist){
           if(!resolveInfoList.map { it.activityInfo.packageName }.contains(app.packageName)){
               Applist.remove(app);

           }
       }
       //Applist.sortWith(compareBy({it.packageName}))
       //Applist.sortWith(compareBy({it.Blocked.toString().length}, { TestcommonFunctions.getAppname(it.packageName, packageManager ) })) // zorgt voor laden
       ApplicationNames = LoadAllNames();
       if(ApplicationNames.size != resolveInfoList.size){
           getAllAppNames()
           ApplicationNames = LoadAllNames();
       }
       testRescyler();

        /*for (i in 0 until resolveInfoList.size) {
            var inflater = LayoutInflater.from(this).inflate(R.layout.showappblock, null)
            binding.userLinearview.addView(inflater, binding.userLinearview.childCount)
            if(!Appnames.contains(resolveInfoList.get(i).activityInfo.packageName)){
                Applist.add(App(resolveInfoList.get(i).activityInfo.packageName , false))
            }
        }*/
       /* writeToFile(applicationContext, Applist);
        //resolveInfoList.sortBy { TestcommonFuntins.getAppname(it.activityInfo.packageName, packageManager ) }

       AddAllAppsonScreen.setAppsonscreen(Applist, packageManager, binding, applicationContext);
        val test = "";*/
    }
    fun LoadAllNames(): ArrayList<String> {
        var Applist: ArrayList<String> = ArrayList()
        val path = applicationContext.filesDir
        val readFrom = File(path, "names.txt")
        val content = ByteArray(readFrom.length().toInt())
        var stream: FileInputStream? = null
        try {
            stream = FileInputStream(readFrom)
            stream.read(content)
            var s = String(content)
            s = s.substring(1, s.length - 1)
            val split = s.split(", ").toTypedArray()
            split.forEach {
                if(it != ""){
                    Applist.add(it);
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return Applist
    }
    fun getAllAppNames(){
        ApplicationNames = ArrayList();
        for(App in Applist){
            ApplicationNames.add(TestcommonFunctions.getAppname(App.packageName, packageManager));
        }
        ApplicationNames.sort();

        val path = applicationContext.filesDir
        try {
            val writer = FileOutputStream(File(path, "names.txt"))
            writer.write(ApplicationNames.toString().toByteArray());
            writer.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    suspend fun testRescyler() {
        // getting the recyclerview by its id
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)
        // ArrayList of class ItemsViewModel
        val data = ArrayList<ItemsViewModel>()
        // This loop will create 20 Views containing
        // the image with the count of view
        for (i in 0 until  Applist.size) {
            //var d = null;
            //var d = packageManager.getApplicationIcon("com.google.android.youtube")
            var t = System.currentTimeMillis();
            data.add(ItemsViewModel(ApplicationNames.get(i), null))
        }
        for (i in 0 until  5) {
            var t = System.currentTimeMillis();
            data.get(i).d = packageManager.getApplicationIcon(Applist.get(i).packageName)
        }
        // This will pass the ArrayList to our Adapter
        val adapter = CustomAdapter(data)
        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter;
        var number = 25;
        while(number < Applist.size){
            doWorkAsync2(data , recyclerview, number)
            number += 20;
        }
        doWorkAsync2(data , recyclerview, Applist.size)
        binding.progressBar.visibility = View.INVISIBLE;
    }

    suspend fun doWorkAsync2(data: ArrayList<ItemsViewModel>, recyclerview: RecyclerView, number: Int) = coroutineScope{

        launch {
            for (i in number -20 until  number) {
                data.get(i).d = packageManager.getApplicationIcon(Applist.get(i).packageName)
            }
            val adapter = CustomAdapter(data)
            // Setting the Adapter with the recyclerview
            recyclerview.adapter = adapter
        }
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

data class ItemsViewModel(var text: String, var d: Drawable?)
