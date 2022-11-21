
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class TestcommonFuntins : AppCompatActivity() {
    companion object {//https://stackoverflow.com/questions/53802632/reuse-methods-in-kotlin-android
        fun getAppname(packagenaam: String, packageManager : PackageManager): String{ // https://stackoverflow.com/questions/11229219/android-how-to-get-application-name-not-package-name
        var applicationInfo: ApplicationInfo? = null
            try {
                applicationInfo = packageManager.getApplicationInfo(
                    packagenaam, 0
                )
            } catch (e: PackageManager.NameNotFoundException) {
                Log.d("TAG", "The package with the given name cannot be found on the system.")
            }
            if (applicationInfo != null) {
                return packageManager.getApplicationLabel(applicationInfo).toString()
            }
            return "";
        }
    }
}