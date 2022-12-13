package com.example.appduration

import AddAllAppsonScreenPoging2.Companion.Applist
import SaveAndloadApplistFile.Companion.getContentOutOfFile
import TestcommonFunctions.Companion.getAllAppsAndTimeStamps
import TestcommonFunctions.Companion.getTotalTimeApps
import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.ZoneId


class CheckUseBlockedAppsService: Service() {//https://www.youtube.com/watch?v=bA7v1Ubjlzw
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTestForegroundService(); // start de fourground service meer uitleg op https://developer.android.com/guide/components/foreground-services
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startTestForegroundService() {  // methode voor het starten van de voorgrond service hier stellen we id in en de text van de notificatie
        val CHANNEL_ID = "foreground Service ID"
        var channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_ID,
            NotificationManager.IMPORTANCE_HIGH
        );

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel);
        val notif = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Sample Title")
            .setContentText((1).toString() + " min left")
            .setOnlyAlertOnce(true)
            .setSmallIcon(R.drawable.ic_launcher_background);
        //.setContentText((60 - calculateUsedTime()).toString() + " min left")
        startForeground(10001 , notif.build());
        startTimer(notif);
    }

    /**
     * Starts the timer for the tracking.
     */

    val mainHandler = Handler(Looper.getMainLooper())
    private fun startTimer(builder: NotificationCompat.Builder) {

        CoroutineScope(Dispatchers.Main).launch {
            var i = 0
            mainHandler.post(object : Runnable {
                override fun run() {
                    if(!isAppInForeground){
                        builder.setContentText((60 - calculateUsedTime() ).toString() + " min left");
                        startForeground(10001, builder.build()); // update text notification
                    }
                    mainHandler.postDelayed(this, 10000) // zet hoger bij lagere battery en fix main activity
                    Log.d("MainActivity",  i.toString());
                    i++;
                }
            })
        }
    }

    companion object { //https://stackoverflow.com/questions/57326315/how-to-check-in-foreground-service-if-the-app-is-running
        // this can be used to check if the app is running or not
        @JvmField  var isAppInForeground: Boolean = false // see if resticked app activity is running , moet wss veranderen als ik dit in aparte activity zet.
        @JvmField  var testheropstart: Boolean = false
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        val intent = Intent("com.example.appduration") // laat deze service runnen ookal word de app gesloten.
        sendBroadcast(intent)
        super.onTaskRemoved(rootIntent)
    }

    fun calculateUsedTime(): Int{ // fix dubbel func in add apps on screen 2
        var UsageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        Applist = getContentOutOfFile(applicationContext);
        var Appnames = ArrayList<String>();
        for(app in Applist){
            if(app.Blocked){
                Appnames.add(app.packageName)
            }
        }
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
        time = time /60000;
        if(time > 2){
            var timeSaved = getSavedTime(applicationContext);
            Log.d("test", isAppInForeground.toString())
            if(checkIfAppRunning(applicationContext)){ // vervang later
                timeSaved = time;
            }
            if(testheropstart){
                timeSaved = time;
                testheropstart =false;
            }
            //Toast.makeText(applicationContext,  windowSerciceRunning().toString(), Toast.LENGTH_SHORT).show();
            if(timeSaved < time && !windowSerciceRunning()){ // maak scherm als het nog niet bestaat en de tijd veranderd is.
                startServiceDrawing();
            }
            SaveTime(applicationContext, time);
        }
        return time.toInt();
    }


    @Suppress("DEPRECATION") // getRunningServices werkt enkel nog voor eigen services
    private fun windowSerciceRunning(): Boolean{ // kijk of de window service (en window) al bestaat
        var activitymanager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager;
        for(servise in activitymanager.getRunningServices(Integer.MAX_VALUE)){
            if(WindowService::class.java.name.equals(servise.service.className)){
                return true;
            }
        }
        return false;

    }

    fun SaveTime(applicationContext: Context, Time: Double, ){ // sla de overgebleven tijd op zodat we kunnen controleren wanneer een geblokeerde app gebruikt word.
        val path = applicationContext.filesDir
        try {
            val writer = FileOutputStream(File(path, "time.txt"))
            writer.write(Time.toString().toByteArray());
            writer.close();
            //Toast.makeText(applicationContext, "saved", Toast.LENGTH_SHORT).show();
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //getContentOutOfFile(applicationContext, Applist)
    }

    fun getSavedTime(applicationContext : Context): Double { // haal de tijd opgeslagen in het bestand
        val path = applicationContext.filesDir
        val readFrom = File(path, "time.txt")
        val content = ByteArray(readFrom.length().toInt())
        var stream: FileInputStream? = null
        try {
            stream = FileInputStream(readFrom)
            stream.read(content)
            var s = String(content)
            return s.toDouble();
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return 0.0
    }

    public fun checkIfAppRunning(applicationContext : Context): Boolean { // kijkt naar volledige app
        var activityManager =  applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager;
        var appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        var packageName = applicationContext.getPackageName();
        for (appProcess in appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }


    // method for starting the service for drawing over other apps
    fun startServiceDrawing() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // check if the user has already granted
            // the Draw over other apps permission
            if (Settings.canDrawOverlays(this)) {
                // start thstartForegroundServicee service based on the android version
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    var serviceIntent = Intent(this,WindowService::class.java);
                    startService(serviceIntent);
                    //startForegroundService(Intent(this, ForegroundService::class.java)
                } else {
                    startService(Intent(this, WindowService::class.java))
                }
            }
        } else {
            startService(Intent(this, WindowService::class.java))
        }
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}