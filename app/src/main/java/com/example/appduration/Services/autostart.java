package com.example.appduration.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

public class autostart extends BroadcastReceiver //dit is normaal standaart maar heb het van: https://stackoverflow.com/questions/7690350/android-start-service-on-boot
{
    public void onReceive(Context context, Intent arg1)
    {
        Intent intent = new Intent(context,CheckUseBlockedAppsService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
        Log.i("Autostart", "started");
    }
}