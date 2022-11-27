
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.App

class SaveAndloadAppNames : AppCompatActivity() {
    companion object {//https://stackoverflow.com/questions/53802632/reuse-methods-in-kotlin-android
        fun getAllAppNames(ApplicationNames: ArrayList<String>, Applist: ArrayList<App>, packageManager: PackageManager, applicationContext: Context) {
            for(App in Applist){
                App.AppName = TestcommonFunctions.getAppname(App.packageName, packageManager);
            }
        }
    }
}