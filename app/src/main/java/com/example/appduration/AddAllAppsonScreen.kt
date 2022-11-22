
import android.content.pm.PackageManager
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.App
import com.example.appduration.R
import com.example.appduration.databinding.ActivityRestricktedBinding

class AddAllAppsonScreen : AppCompatActivity() {
    companion object {//https://stackoverflow.com/questions/53802632/reuse-methods-in-kotlin-android

        public fun setAppsonscreen(
            Applist: ArrayList<App>,
            packageManager: PackageManager,
            binding: ActivityRestricktedBinding,

            ){
            Applist.sortWith(compareBy({it.Blocked.toString().length}, { TestcommonFunctions.getAppname(it.packageName, packageManager ) }))
            //it.Blocked.toString()
            var v: View?
            for (i in 0 until Applist.size) {
                v = binding.userLinearview.getChildAt(i)
                var d = packageManager.getApplicationIcon(Applist.get(i).packageName)
                val icon: ImageView = v.findViewById(R.id.iconapp)
                val name: TextView = v.findViewById(R.id.txtappnameRestrickted)
                val Button: Button = v.findViewById(R.id.btnrestricktedApp)
                icon.setImageDrawable(d)
                name.text = TestcommonFunctions.getAppname(Applist.get(i).packageName, packageManager );
                if(Applist.get(i).Blocked){
                    Button.text = "unBlock"
                    Button.setTextColor(Color.parseColor("#fc031c"))
                }else{
                    Button.text = "Block"
                    Button.setTextColor(Color.WHITE)
                }
                Button.setOnClickListener {
                    //binding.progressBar.visibility = View.VISIBLE;
                    if(Button.text == "Block"){
                        Button.text = "unBlock"
                        Button.setTextColor(Color.parseColor("#fc031c"))
                        Applist.get(i).Blocked = true;

                    }else{
                        Button.text = "Block"
                        Button.setTextColor(Color.WHITE)
                        Applist.get(i).Blocked = false;
                    }
                    //writeToFile(); later
                    //setAppsonscreen();
                }
            }
            //calculateUsedTime() later
        }
    }
}


/*@SuppressLint("SuspiciousIndentation")
       fun addViews(
       Applistinserted: ArrayList<App>, packageManager: PackageManager, applicationContext: Context, binding: ActivityRestricktedBinding,
   ): ArrayList<App> {
           var Applist = Applistinserted;
       var packageManager = packageManager
           val intent = Intent(Intent.ACTION_MAIN, null)
           intent.addCategory(Intent.CATEGORY_LAUNCHER)
           var resolveInfoList = packageManager.queryIntentActivities(intent, 0) //https://stackoverflow.com/questions/10696121/get-icons-of-all-installed-apps-in-android
           Applist = getContentOutOfFile(applicationContext);
           var Appnames = Applist.map { it.packageName }
           for (app in Applist){
               if(!resolveInfoList.map { it.activityInfo.packageName }.contains(app.packageName)){
                   Applist.remove(app);
                   writeToFile(applicationContext, Applist);
               }
           }
           for (i in 0 until resolveInfoList.size) {
               var inflater = LayoutInflater.from(applicationContext).inflate(R.layout.showappblock, null)
               binding.userLinearview.addView(inflater, binding.userLinearview.childCount)
               val input = System.nanoTime()

               if(!Appnames.contains(resolveInfoList.get(i).activityInfo.packageName)){
                   Applist.add(App(resolveInfoList.get(i).activityInfo.packageName , false))
               }
           }
           writeToFile(applicationContext, Applist);
           //resolveInfoList.sortBy { TestcommonFuntins.getAppname(it.activityInfo.packageName, packageManager ) }
           //AddAllAppsonScreen.setAppsonscreen(Applist, packageManager, binding);
       return Applist;

       }*/