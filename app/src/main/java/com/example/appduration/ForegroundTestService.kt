package com.example.appduration

import AddAllAppsonScreenPoging2.Companion.Applist
import SaveAndloadApplistFile.Companion.getContentOutOfFile
import TestcommonFunctions.Companion.getAllAppsAndTimeStamps
import TestcommonFunctions.Companion.getTotalTimeApps
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId


class ForegroundTestService: Service() {//https://www.youtube.com/watch?v=bA7v1Ubjlzw
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        /*Thread(
            object : Runnable {
                override fun run() {
                    Log.d("TAG", "testmessage")
                try{
                    Thread.sleep(2000);
                } catch (e: InterruptedException) {
                    e.printStackTrace();
                }
            }
        }).start();
        val CHANNEL_ID = "foreground Service ID"
        var channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_ID,
            NotificationManager.IMPORTANCE_HIGH
        );

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel);
        val notif = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Sample Title")
            .setContentText((60 - calculateUsedTime()).toString() + " min left")
            .setSmallIcon(R.drawable.ic_launcher_background);

        startForeground(10001 , notif.build())*/

        startForegroundService();

        return super.onStartCommand(intent, flags, startId)
    }
    fun testvoortijd() {//https://medium.com/huawei-developers/foreground-services-with-notification-channel-in-android-7a272f07ad1
        CoroutineScope(Dispatchers.Main).launch {
            for (i in 0 until 10 step 1) {
                Log.d("TAG", ("test $i"));
                //Log.d("TAG", (60 - calculateUsedTime()).toString());
                try{
                    Thread.sleep(6000);
                } catch (e: InterruptedException) {
                    e.printStackTrace();
                }
            }
        }
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
                    /*Log.d("TAG", ("test $i"));
                    //Log.d("TAG", (60 - calculateUsedTime()).toString());
                    val text = "Some text that will update the notification"
                    val CHANNEL_ID = "foreground Service ID"
                    var channel = NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_ID,
                        NotificationManager.IMPORTANCE_HIGH
                    );
                    getSystemService(NotificationManager::class.java).createNotificationChannel(
                        channel
                    );
                    var UsageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
                    val notif = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                        .setContentTitle("Sample Title")

                        .setContentText((60 - calculateUsedTime() ).toString() + " min left")
                        //.setContentText((i).toString() + " min left")
                        .setSmallIcon(R.drawable.ic_launcher_background);

                    startForeground(10001, notif.build());
                    i++;*/

                    builder.setContentText((60 - calculateUsedTime() ).toString() + " min left");
                    startForeground(10001, builder.build());
                    mainHandler.postDelayed(this, 10000)
                }
            })
        }
    }
    private fun startForegroundService() {
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
    override fun onTaskRemoved(rootIntent: Intent?) {
        val intent = Intent("com.example.appduration")
        sendBroadcast(intent)
        super.onTaskRemoved(rootIntent)
    }

    fun calculateUsedTime(): Int{
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
        return time.toInt(); // fix tijd wanneer app nog aan het runnen is.
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}