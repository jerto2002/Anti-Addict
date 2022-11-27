
import SaveAndloadAppNames.Companion.getAllAppNames
import SaveAndloadApplistFile.Companion.getContentOutOfFile
import SaveAndloadApplistFile.Companion.writeToFile
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.appduration.App
import com.example.appduration.ItemsViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AddAllAppsonScreenPoging2 : AppCompatActivity() {

    companion object {//https://stackoverflow.com/questions/53802632/reuse-methods-in-kotlin-android
    var Applist: ArrayList<App> = ArrayList()
        var ApplicationNames: ArrayList<String> = ArrayList()
    @SuppressLint("SuspiciousIndentation")
    public suspend fun addViews(
        packageManager: PackageManager,
        applicationContext: Context,
        recyclerview: RecyclerView
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

        for (app in Applist){
            if(!resolveInfoList.map { it.activityInfo.packageName }.contains(app.packageName)){
                Applist.remove(app);

            }
            if(app.AppName == ""){
                getAllAppNames(ApplicationNames, Applist, packageManager, applicationContext)
            }
        }

        /*if(ApplicationNames.size != resolveInfoList.size){

        }*/
        //var mp = Applist;
        InsertContentToViews(packageManager, applicationContext, recyclerview);
    }
        suspend fun InsertContentToViews(
            packageManager: PackageManager,
            applicationContext: Context,
            recyclerview: RecyclerView
        ) {

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
                data.add(ItemsViewModel(Applist.get(i).AppName, null, Applist, packageManager, applicationContext, i, recyclerview))//names
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
                doWorkAsync2(data , recyclerview, number, packageManager)
                number += 20;
            }
            doWorkAsync2(data , recyclerview, Applist.size, packageManager)
            //binding.progressBar.visibility = View.INVISIBLE;
        }

        suspend fun doWorkAsync2(data: ArrayList<ItemsViewModel>, recyclerview: RecyclerView, number: Int, packageManager: PackageManager) = coroutineScope{
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