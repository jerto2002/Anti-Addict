package com.example.appduration

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class ForegroundTestService: Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Thread(
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
            NotificationManager.IMPORTANCE_LOW
        );
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel);
        val notif = NotificationCompat.Builder(this,CHANNEL_ID)
            .setContentTitle("Sample Title")
            .setContentText("This is sample body notif")
            .setSmallIcon(R.drawable.ic_launcher_background);

        startForeground(10001 , notif.build())
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}