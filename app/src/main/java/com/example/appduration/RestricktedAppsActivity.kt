package com.example.appduration

import TestcommonFuntins
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.databinding.ActivityRestricktedBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream


class RestricktedAppsActivity : AppCompatActivity() {
    var Applist: ArrayList<App> = ArrayList()
    private lateinit var binding: ActivityRestricktedBinding
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRestricktedBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        binding.progressBar.visibility = View.VISIBLE;
        doWorkAsync(MainScope(), "test")//https://stackoverflow.com/questions/57770131/create-async-function-in-kotlin
    }

    fun doWorkAsync(scope: CoroutineScope, msg: String): Deferred<Int> = scope.async {
        addViews()
        println("$msg - Work done")
        return@async 42
    }
    @SuppressLint("SuspiciousIndentation")
    private fun addViews(){
        var packageManager = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        var resolveInfoList = packageManager.queryIntentActivities(intent, 0) //https://stackoverflow.com/questions/10696121/get-icons-of-all-installed-apps-in-android
        for (i in 0 until resolveInfoList.size) {
            var inflater = LayoutInflater.from(this).inflate(R.layout.showappblock, null)
            binding.userLinearview.addView(inflater, binding.userLinearview.childCount)
            val input = System.nanoTime()
            Applist.add(App(resolveInfoList.get(i).activityInfo.packageName , false))
        }
        writeToFile();
        //resolveInfoList.sortBy { TestcommonFuntins.getAppname(it.activityInfo.packageName, packageManager ) }
        setAppsonscreen();
        binding.progressBar.visibility = View.INVISIBLE;
    }

    private fun setAppsonscreen(){

        Applist.sortWith(compareBy({it.Blocked.toString().length}, { TestcommonFuntins.getAppname(it.packageName, packageManager ) }))
        //it.Blocked.toString()
        var v: View?
        for (i in 0 until Applist.size) {
            v = binding.userLinearview.getChildAt(i)
            var d = packageManager.getApplicationIcon(Applist.get(i).packageName)
            val icon: ImageView = v.findViewById(R.id.iconapp)
            val name: TextView = v.findViewById(R.id.txtappnameRestrickted)
            val Button: Button = v.findViewById(R.id.btnrestricktedApp)
            icon.setImageDrawable(d)
            name.text = TestcommonFuntins.getAppname(Applist.get(i).packageName, packageManager );
            if(Applist.get(i).Blocked == true){
                Button.text = "unBlock"
                Button.setTextColor(Color.parseColor("#fc031c"))
            }else{
                Button.text = "Block"
                Button.setTextColor(Color.WHITE)
            }
            Button.setOnClickListener {
                if(Button.text == "Block"){
                    Button.text = "unBlock"
                    Button.setTextColor(Color.parseColor("#fc031c"))
                    Applist.get(i).Blocked = true;
                    setAppsonscreen();
                    /*for (i in 0 until  binding.userLinearview.childCount){
                        var view = binding.userLinearview.getChildAt(i);
                        var textname: TextView = view.findViewById(R.id.txtappnameRestrickted)
                        if(textname.text == name.text){
                            val test = textname.text;
                            val tt =textname.text;
                            binding.userLinearview.removeViewAt(i);
                            binding.userLinearview.addView(view as View, 0);
                        }
                    }*/
                }else{
                    Button.text = "Block"
                    Button.setTextColor(Color.WHITE)
                    Applist.get(i).Blocked = false;
                    setAppsonscreen();

                }
            }
            // val name = d.toString();
        }
    }
    private fun writeToFile(){
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
        loadContent()
    }
    fun loadContent() {
        Applist = ArrayList();
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
val test = Applist;
            val re ="";
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}

class App(val packageName : String, var Blocked : Boolean = false)