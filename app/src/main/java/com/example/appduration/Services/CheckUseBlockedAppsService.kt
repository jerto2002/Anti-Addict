package com.example.appduration.Services

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.*
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.example.appduration.Controller.AddAllToRestrictedPage.Companion.Applist
import com.example.appduration.Functions.GetInstalledApplicationsInfo.Companion.getAllAppsAndTimeStamps
import com.example.appduration.Functions.GetInstalledApplicationsInfo.Companion.getTotalTimeApps
import com.example.appduration.Functions.SaveAndloadApplistFile.Companion.getContentOutOfFile
import com.example.appduration.R
import com.example.appduration.TestFirebase.Backup
import com.example.appduration.View.App
import com.example.appduration.WindowService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.time.LocalDate
import java.time.ZoneId


class CheckUseBlockedAppsService: Service() {//https://www.youtube.com/watch?v=bA7v1Ubjlzw

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference;

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
            .setSilent(true)
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

        val mPrefstime = getSharedPreferences("time", 0)

        CoroutineScope(Dispatchers.Main).launch {
            var i = 0
            mainHandler.post(object : Runnable {
                override fun run() {
                    val ReaminingTime = mPrefstime.getFloat("savedtime", 100F)
                   // Log.d("fourground", isAppInForeground.toString());
                    if(!isAppInForeground){
                        builder.setContentText((ReaminingTime - CheckIfOverTime() ).toString() + " min left");
                        startForeground(10001, builder.build()); // update text notification
                        checkbackup();
                    }
                    var refreshTime = 100;
                    val mPrefsbattery = getSharedPreferences("battery", 0)
                    val batterySaveMode = mPrefsbattery.getBoolean("savemode", false)
                    if(batterySaveMode){
                        refreshTime = getbatteryper();
                    }


                    mainHandler.postDelayed(this, refreshTime.toLong()) // zet hoger bij lagere battery en fix main activity
                    Log.d("MainActivity",  i.toString());
                    i++;
                }
            })
        }
    }

    fun checkbackup(){
        val mPrefsbattery = getSharedPreferences("battery", 0)
        val batterySaveModePercent = mPrefsbattery.getFloat("percent", 50F)
        val batterySaveMode = mPrefsbattery.getBoolean("savemode", false)

        val mPrefstime = getSharedPreferences("time", 0)
        val ReaminingTime = mPrefstime.getFloat("savedtime", 100F)

        val mBackup = getSharedPreferences("backup", 0)
        val makeBackup = mBackup.getBoolean("backup", false)
        Applist = getContentOutOfFile(applicationContext);

        if(makeBackup){
            Log.d("testbackup", "yes");
            Backupbatterymode(batterySaveMode, ReaminingTime, batterySaveModePercent, Applist)
            val mEditor = mBackup.edit()
            mEditor.putBoolean("backup",  false).commit()
        }
    }

    //https://www.youtube.com/watch?v=miJooBq9iwE&list=PLHQRWugvckFry9Q1OT6hLNfyUizT73PwX&index=3
    private fun Backupbatterymode(isChecked: Boolean, RemaningTime: Float, batterySaveModePercent: Float, Applist: ArrayList<App>) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuth.getInstance().currentUser?.reload()?.addOnCompleteListener{
            if (firebaseAuth.currentUser?.isEmailVerified == true) {
                dbRef =
                    FirebaseDatabase.getInstance("https://appduration-default-rtdb.europe-west1.firebasedatabase.app")
                        .getReference("Backup");

                val userId = firebaseAuth.uid;
                val backup = Backup(userId, isChecked, batterySaveModePercent,RemaningTime, Applist);
                if (userId != null) {
                    dbRef.child(userId).setValue(backup).addOnCompleteListener{
                        Toast.makeText(this, "Data has been send", Toast.LENGTH_LONG).show()
                    }.addOnFailureListener{ err -> Toast.makeText(this, "Error ${err.message}", Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(this, "User isn't verified...", Toast.LENGTH_SHORT).show();
            }
        };
    }

    fun getbatteryper(): Int {
        var refreshTime =100;
        val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { ifilter ->
            applicationContext.registerReceiver(null, ifilter)
        }
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL
        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            level * 100 / scale.toFloat()
        }

        //Log.d("battery", batterySaveModePercent.toString());
        if (batteryPct != null) {
            val mPrefsbattery = getSharedPreferences("battery", 0)
            val batterySaveModePercent = mPrefsbattery.getFloat("percent", 50F)
            if(!isCharging && batteryPct < batterySaveModePercent){
                refreshTime = 10000;
            }
        }
        return refreshTime;
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

    fun CheckIfOverTime(): Int{ // fix dubbel func in add apps on screen 2
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
        var results = getTotalTimeApps(datatime, start, currentTime);
        var time = 0.0;
        for(result in results) {
            if (Appnames.contains(result.key)){
                var er = result.key;
                time = result.value;
                var l ="";
            }
        }
        time = time /60000;
        val mPrefstime = getSharedPreferences("time", 0)
        val ReaminingTime = mPrefstime.getFloat("savedtime", 100F)
        if(time > ReaminingTime){
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

    fun SaveTime(applicationContext: Context, Time: Double){ // sla de overgebleven tijd op zodat we kunnen controleren wanneer een geblokeerde app gebruikt word.
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