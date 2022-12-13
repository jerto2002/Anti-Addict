
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.appduration.CheckUseBlockedAppsService

public class Restarter: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("Broadcast Listened", "Service tried to stop");
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.O){
            context?.startForegroundService(Intent(context, CheckUseBlockedAppsService::class.java));
        }else{
            context?.startService(Intent(context, CheckUseBlockedAppsService::class.java));
        }
    }
}