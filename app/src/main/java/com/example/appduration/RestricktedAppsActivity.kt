package com.example.appduration

import AddAllAppsonScreen
import SaveAndloadApplistFile.Companion.getContentOutOfFile
import SaveAndloadApplistFile.Companion.writeToFile
import TestcommonFunctions.Companion.getAllAppsAndTimeStamps
import TestcommonFunctions.Companion.getTotalTimeApps
import android.annotation.SuppressLint
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
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
        doWorkAsync(MainScope(), "test")//https://stackoverflow.com/questions/57770131/create-async-function-in-kotlin
    }
    fun doWorkAsync(scope: CoroutineScope, msg: String): Deferred<Unit> = scope.async {
        addViews()
        //addViews(Applist, packageManager, applicationContext, binding)
        println("$msg - Work done")
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
        for (i in 0 until resolveInfoList.size) {
            var inflater = LayoutInflater.from(this).inflate(R.layout.showappblock, null)
            binding.userLinearview.addView(inflater, binding.userLinearview.childCount)
            val input = System.nanoTime()

            if(!Appnames.contains(resolveInfoList.get(i).activityInfo.packageName)){
                Applist.add(App(resolveInfoList.get(i).activityInfo.packageName , false))
            }
        }
        writeToFile(applicationContext, Applist);
        //resolveInfoList.sortBy { TestcommonFuntins.getAppname(it.activityInfo.packageName, packageManager ) }
        AddAllAppsonScreen.setAppsonscreen(Applist, packageManager, binding);

    }

    /*private fun setAppsonscreen(){
        Applist.sortWith(compareBy({it.Blocked.toString().length}, { TestcommonFunctions.getAppname(it.packageName, packageManager ) }))
        //it.Blocked.toString()
        var v: View?
        var bindingappblock = ShowappblockBinding.inflate(layoutInflater)
        for (i in 0 until Applist.size) {
            v = binding.userLinearview.getChildAt(i)
            var d = packageManager.getApplicationIcon(Applist.get(i).packageName)
            val icon: ImageView = v.findViewById(R.id.iconapp)
            val name: TextView = v.findViewById(R.id.txtappnameRestrickted)
            val Button: Button = v.findViewById(R.id.btnrestricktedApp)
            icon.setImageDrawable(d)
            name.text = TestcommonFunctions.getAppname(Applist.get(i).packageName, packageManager );
            if(Applist.get(i).Blocked){
                Button.text = "unBlock"
                Button.setTextColor(Color.parseColor("#fc031c"))
            }else{
                Button.text = "Block"
                Button.setTextColor(Color.WHITE)
            }
            Button.setOnClickListener {
                binding.progressBar.visibility = View.VISIBLE;
                if(Button.text == "Block"){
                    Button.text = "unBlock"
                    Button.setTextColor(Color.parseColor("#fc031c"))
                    Applist.get(i).Blocked = true;

                }else{
                    Button.text = "Block"
                    Button.setTextColor(Color.WHITE)
                    Applist.get(i).Blocked = false;
                }
                //writeToFile();
                //setAppsonscreen();
            }
        }
        calculateUsedTime()
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

    /*private fun writeToFile(){
        var textNeeded = "";
        for(app in Applist){
            textNeeded += app.packageName + ", " + app.Blocked + " | " ;
        }
        val path = applicationContext.filesDir
        try {
            val writer = FileOutputStream(File(path, "list.txt"))
            writer.write(textNeeded.toByteArray());
            writer.close()
            //Toast.makeText(getApplicationContext(), "wrote to file: " + path + "list.txt", Toast.LENGTH_SHORT).show();
        } catch (e: Exception) {
            e.printStackTrace()
        }
        getContentOutOfFile()
    }
    fun getContentOutOfFile() {
        Applist = ArrayList();
        val path = applicationContext.filesDir
        val readFrom = File(path, "list.txt")
        val content = ByteArray(readFrom.length().toInt())
        var stream: FileInputStream? = null
        try {
            stream = FileInputStream(readFrom)
            stream.read(content)
            var s = String(content)
            val split = s.split(" | ").toTypedArray()
            split.forEach {
                if(it != ""){
                    Applist.add(App(it.split(", ").get(0),it.split(", ").get(1).toBoolean()));
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }*/
}

class App(val packageName : String, var Blocked : Boolean = false)