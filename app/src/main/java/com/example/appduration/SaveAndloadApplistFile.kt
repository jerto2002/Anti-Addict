
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.App
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class SaveAndloadApplistFile : AppCompatActivity() {
    companion object {//https://stackoverflow.com/questions/53802632/reuse-methods-in-kotlin-android
        fun writeToFile(applicationContext : Context, Applist : ArrayList<App>){
            var textNeeded = "";
            for(app in Applist){
                textNeeded += app.packageName + ", " + app.Blocked + " | " ;
            }
            val path = applicationContext.filesDir
            try {
                val writer = FileOutputStream(File(path, "list.txt"))
                writer.write(textNeeded.toByteArray());
                writer.close()
                //Toast.makeText(getApplicationContext(), "wrote to file: " + path + "list.txt", Toast.LENGTH_SHORT).show();
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //getContentOutOfFile(applicationContext, Applist)
        }
        fun getContentOutOfFile(applicationContext : Context): ArrayList<App> {
            var Applist: ArrayList<App> = ArrayList()
            val path = applicationContext.filesDir
            val readFrom = File(path, "list.txt")
            val content = ByteArray(readFrom.length().toInt())
            var stream: FileInputStream? = null
            try {
                stream = FileInputStream(readFrom)
                stream.read(content)
                var s = String(content)
                val split = s.split(" | ").toTypedArray()
                split.forEach {
                    if(it != ""){
                        Applist.add(App(it.split(", ").get(0),it.split(", ").get(1).toBoolean()));
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return Applist
        }
    }
}