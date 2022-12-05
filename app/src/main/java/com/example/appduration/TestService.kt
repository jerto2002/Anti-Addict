package com.example.appduration

import Restarter
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class TestService: Service() {//https://www.youtube.com/watch?v=bA7v1Ubjlzw
    var counter = 0;

    override fun onCreate() {
        super.onCreate()

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O){
            startMyOwnForeground();
        }
        else{
            startForeground(1, Notification())
        }
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground(){
        var NOTIFICATION_CHANNEL_ID="example.permanece";
        var channelName = "Background Service";
        var chan = NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE) as NotificationChannel;
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE;

        var manager =  getSystemService(NotificationManager::class.java).createNotificationChannel(chan);
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        var notif = notificationBuilder.setOngoing(true)
            .setContentTitle("Sample Title")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build();
        //.setContentText((60 - calculateUsedTime()).toString() + " min left")
        startForeground(2 , notif);
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startTimer();
        return START_STICKY;
    }

    override fun onDestroy() {
        //stoptimertask();

        var broadcastIntent = Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter::class.java);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    val mainHandler = Handler(Looper.getMainLooper())
    var aantal =0;
    fun startTimer(){
        mainHandler.post(object : Runnable {
            override fun run() {
                //Toast.makeText(applicationContext, "test $aantal", Toast.LENGTH_SHORT).show()
                Log.d("TAG","test $aantal");
                aantal++;
                //Log.d("TAG", (60 - calculateUsedTime()).toString());
                mainHandler.postDelayed(this, 1000)
            }
        })
    }


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}