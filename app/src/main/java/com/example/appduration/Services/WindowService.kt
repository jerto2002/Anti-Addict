package com.example.appduration

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.example.testtekenoverandereapps.Window


class WindowService : Service() {
    override fun onBind(intent: Intent): IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        val window = Window(this)
        window.open()
        return super.onStartCommand(intent, flags, startId);
    }
}