package com.example.appduration

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
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
    private fun addViews(){
        var packageManager = packageManager
        val intent = Intent(Intent.ACTION_MAIN, null)
        intent.addCategory(Intent.CATEGORY_LAUNCHER)
        val resolveInfoList = packageManager.queryIntentActivities(intent, 0) //https://stackoverflow.com/questions/10696121/get-icons-of-all-installed-apps-in-android
        for (i in 0 until resolveInfoList.size) {
            var inflater = LayoutInflater.from(this).inflate(R.layout.showappblock, null)
            binding.userLinearview.addView(inflater, binding.userLinearview.childCount)
        }

        val count = binding.userLinearview.childCount
        var v: View?

        for (i in 0 until resolveInfoList.size) {
            v = binding.userLinearview.getChildAt(i)
            //var d = packageManager.getApplicationIcon(resolveInfoList.get(i).resolvePackageName)
            val icon: ImageView = v.findViewById(R.id.iconapp)
            icon.setImageDrawable(resolveInfoList.get(i).activityInfo.loadIcon(packageManager))
           // val name = d.toString();
        }

        binding.progressBar.visibility = View.INVISIBLE;
    }
}

/*public fun AddUsersToScreen() {
    /*for(user in articles){
        var inflater = LayoutInflater.from(this).inflate(R.layout.activity_restrickted, null)
        binding.userLinearview.addView(inflater, binding.userLinearview.childCount)
    }
    val packages: MutableList<PackageInfo> = packageManager.getInstalledPackages(0)
    for(i in 0 until packages.size){

            var inflater = LayoutInflater.from(this).inflate(R.layout.showappblock, null)
            binding.userLinearview.addView(inflater, binding.userLinearview.childCount)

    }

    val count = binding.userLinearview.childCount
    var v: View?
    for (i in 0 until count) {
        v = binding.userLinearview.getChildAt(i)
        var d = packageManager.getApplicationIcon(packages.get(i).packageName)
        val icon: ImageView = v.findViewById(R.id.iconapp)
        icon.setImageDrawable(d)

        val name = d.toString();

    }

}*/