package com.example.appduration

import TestcommonFuntins
import android.annotation.SuppressLint
import android.content.Intent
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


class RestricktedAppsActivity : AppCompatActivity() {
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
        }
        resolveInfoList.sortBy { TestcommonFuntins.getAppname(it.activityInfo.packageName, packageManager ) }
        val count = binding.userLinearview.childCount
        var v: View?
        for (i in 0 until resolveInfoList.size) {
            v = binding.userLinearview.getChildAt(i)
            //var d = packageManager.getApplicationIcon(resolveInfoList.get(i).resolvePackageName)
            val icon: ImageView = v.findViewById(R.id.iconapp)
            val name: TextView = v.findViewById(R.id.txtappnameRestrickted)
            val Button: Button = v.findViewById(R.id.btnrestricktedApp)
            icon.setImageDrawable(resolveInfoList.get(i).activityInfo.loadIcon(packageManager))
            name.text = TestcommonFuntins.getAppname(resolveInfoList.get(i).activityInfo.packageName, packageManager );
            Button.setOnClickListener {
                if(Button.text == "Block"){
                    Button.text = "unBlock"
                    val tv = resolveInfoList.get(i).activityInfo.packageName;
                    var position = 0
                    var tag = v as View
                    if (v.tag is Int) {
                        position = v.tag as Int
                    }
                    for (i in 0 until  binding.userLinearview.childCount){
                        var view = binding.userLinearview.getChildAt(i);
                        var textname: TextView = view.findViewById(R.id.txtappnameRestrickted)
                        if(textname.text == name.text){
                            val test = textname.text;
                            val tt =textname.text;
                            binding.userLinearview.removeViewAt(i);
                            binding.userLinearview.addView(view as View, 0);
                        }
                    }
                    /*var view = binding.userLinearview.getChildAt(i);
                        binding.userLinearview.removeViewAt(i);
                    binding.userLinearview.addView(view as View, 0);
                    val test = "";*/
                }else{
                    Button.text = "Block"
                }
            }
           // val name = d.toString();
        }

        binding.progressBar.visibility = View.INVISIBLE;
    }
}