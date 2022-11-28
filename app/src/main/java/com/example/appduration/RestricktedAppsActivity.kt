package com.example.appduration

import AddAllAppsonScreenPoging2.Companion.Applist
import AddAllAppsonScreenPoging2.Companion.addViews
import TestcommonFunctions.Companion.getAllAppsAndTimeStamps
import TestcommonFunctions.Companion.getTotalTimeApps
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.appduration.databinding.ActivityRestricktedBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import java.time.LocalDate
import java.time.ZoneId


class RestricktedAppsActivity : AppCompatActivity() {
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
        val recyclerview = findViewById<RecyclerView>(R.id.recyclerview)
        addViews(packageManager, applicationContext, recyclerview, binding.progressBar)
        binding.progressBar.visibility = View.INVISIBLE;
        return@async
    }

  /* @SuppressLint("SuspiciousIndentation")
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
       ApplicationNames = LoadAllNames(applicationContext);
       if(ApplicationNames.size != resolveInfoList.size){
           ApplicationNames = getAllAppNames(ApplicationNames, Applist, packageManager, applicationContext)
           ApplicationNames = LoadAllNames(applicationContext);
       }
       InsertContentToViews();
    }
    suspend fun InsertContentToViews() {
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
    }*/

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

class App(val packageName : String, var Blocked : Boolean = false, var AppName : String = "")

data class ItemsViewModel(
    var text: String,
    var d: Drawable?,
    val Applist: ArrayList<App>,
    val packageManager: PackageManager,
    val applicationContext: Context,
    val i: Int,
    val recyclerview: RecyclerView,
    val progressBar: ProgressBar
)
