
import SaveAndloadAppNames.Companion.getAllAppNames
import SaveAndloadApplistFile.Companion.getContentOutOfFile
import SaveAndloadApplistFile.Companion.writeToFile
import TestcommonFunctions.Companion.getAllAppsAndTimeStamps
import TestcommonFunctions.Companion.getTotalTimeApps
import android.annotation.SuppressLint
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appduration.App
import com.example.appduration.ItemsViewModel
import com.example.appduration.databinding.ActivityRestricktedBinding
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class AddAllAppsonScreenPoging2 : AppCompatActivity() {

    companion object {//https://stackoverflow.com/questions/53802632/reuse-methods-in-kotlin-android
    var Applist: ArrayList<App> = ArrayList()
        var ApplicationNames: ArrayList<String> = ArrayList()
    @SuppressLint("SuspiciousIndentation")
    public suspend fun addViews(
        packageManager: PackageManager,
        applicationContext: Context,
        recyclerview: RecyclerView,
        progressBar: ProgressBar,
        UsageStatsManager: UsageStatsManager,
        binding: ActivityRestricktedBinding
    ) {
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

        var indexes = -1;
        for (app in Applist){
            if(!resolveInfoList.map { it.activityInfo.packageName }.contains(app.packageName)){
                //Applist.remove(app); geeft oneindige lus
                indexes = Applist.indexOf(app);
            }
            if(app.AppName == ""){
                getAllAppNames(ApplicationNames, Applist, packageManager, applicationContext)
            }
        }
        if( indexes >= 0){ // fix als je app verwijdert brijdt uit
            Applist.removeAt(indexes);
        }

        /*if(ApplicationNames.size != resolveInfoList.size){

        }*/
        //var mp = Applist;
        InsertContentToViews(packageManager, applicationContext, recyclerview, progressBar, UsageStatsManager, binding);
    }
        suspend fun InsertContentToViews(
            packageManager: PackageManager,
            applicationContext: Context,
            recyclerview: RecyclerView,
            progressBar: ProgressBar,
            UsageStatsManager: UsageStatsManager,
            binding: ActivityRestricktedBinding
        ) {
            progressBar.visibility = View.VISIBLE;
            // getting the recyclerview by its id

            // this creates a vertical layout Manager
            recyclerview.layoutManager = LinearLayoutManager(applicationContext)
            // ArrayList of class ItemsViewModel
            val data = ArrayList<ItemsViewModel>()
            // This loop will create 20 Views containing
            // the image with the count of view
            for (i in 0 until  Applist.size) {
                //var d = null;
                //var d = packageManager.getApplicationIcon("com.google.android.youtube")
                var t = Applist;
                data.add(ItemsViewModel(Applist.get(i).AppName, null, Applist, packageManager, applicationContext, i, recyclerview, progressBar, UsageStatsManager, binding))//names
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
                LoadmoreApps(data , recyclerview, number, packageManager)
                number += 20;
            }
            LoadmoreApps(data , recyclerview, Applist.size, packageManager)
            progressBar.visibility = View.INVISIBLE;
            calculateUsedTime(UsageStatsManager, binding);
            //binding.progressBar.visibility = View.INVISIBLE;
        }
        fun calculateUsedTime(UsageStatsManager: UsageStatsManager, binding: ActivityRestricktedBinding){ // fix dubbel func in ForegroundTestService
            val currentTime = System.currentTimeMillis()
            val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
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
            time = time /60000
            binding.txtTimeRemaining.text = "Time remaining: " + (60 - time).toInt().toString() +"min";
        }
       /* fun calculateUsedTime(UsageStatsManager: UsageStatsManager, applicationContext: Context){
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
            time = time /60000
            var t = "";
        }*/
        suspend fun LoadmoreApps(data: ArrayList<ItemsViewModel>, recyclerview: RecyclerView, number: Int, packageManager: PackageManager) = coroutineScope{
            launch {
                for (i in number -20 until  number) {
                    data.get(i).d = packageManager.getApplicationIcon(Applist.get(i).packageName)
                }
                val adapter = CustomAdapter(data)
                // Setting the Adapter with the recyclerview
                recyclerview.adapter = adapter
            }
        }
    }
}