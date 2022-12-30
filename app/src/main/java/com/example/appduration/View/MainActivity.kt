package com.example.testmvvm.View

import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.Logic.GetInstalledApplicationsInfo
import com.example.appduration.Logic.Logic
import com.example.appduration.databinding.ActivityMainBinding
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate

/*
 *      MainActivity
 *      - opens our fragment which has the UI
 */
class MainActivity : AppCompatActivity() { //order code straks
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        if ( checkUsageStatsPermission() ) { // als we de toestemming hebben voor usage stats vind gebruik tijd voor alle apps
            var UsageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
            var logic = Logic();
            fillInChart(logic.findAppsDurationTimes(UsageStatsManager));
        }
        else {
            // Navigate the user to the permission settings
            Intent( Settings.ACTION_USAGE_ACCESS_SETTINGS ).apply {
                startActivity( this )
            }
            while(!checkUsageStatsPermission()){}
        }
    }


    private fun fillInChart(result: HashMap<String, Double>){ // vul de chart in //https://www.youtube.com/watch?v=S3zqxVoIUig
      val entries = ArrayList<PieEntry>();
      var other = 100F;
      var amount = 3;
      if(amount > result.size){
          amount = result.size;
      }
      for(i in 0 until amount){
          var name = GetInstalledApplicationsInfo.getAppname(result.keys.toList().get(i), packageManager );
          name = showPartOfstringWithDots(5,name);
          entries.add(PieEntry(((result.values.toList().get(i) / result.values.sum()) *100).toFloat(), name));
          other -= ((result.values.toList().get(i) / result.values.sum()) *100).toFloat()
      }
      entries.add(PieEntry(other, "other"));
      val colors = ArrayList<Int>();
      for(color in ColorTemplate.MATERIAL_COLORS){
          colors.add(color)
      }

      for(color in ColorTemplate.VORDIPLOM_COLORS){
          colors.add(color);
      }

      val dataSet = PieDataSet(entries, "Expense Category");
      dataSet.setColors(colors);

      val data = PieData(dataSet);
      data.setDrawValues(true);
      data.setValueFormatter(PercentFormatter());
      dataSet.setValueLinePart1OffsetPercentage(1F);
      dataSet.setValueLinePart1Length(0.1f);
      dataSet.setValueLinePart2Length(0f);
      dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
      dataSet.valueLineColor = Color.TRANSPARENT

      binding.chart.setData(data);
      binding.chart.invalidate();
      binding.chart.setHoleColor(Color.TRANSPARENT);
      binding.chart.setHoleRadius(85F);
      binding.chart.getData().setDrawValues(false);
      binding.chart.getDescription().setEnabled(false);
      binding.chart.setEntryLabelTextSize(16F)
      binding.chart.getLegend().setEnabled(false)
      val textcenter = ((result.values.sum() /60000 /60).toInt()).toString()+ "h " +   (60 * ("0." + (result.values.sum() /60000 /60).toString().split(".").get(1)).toDouble()).toInt() +"m";
      binding.chart.setCenterText(textcenter);
      binding.chart.setCenterTextSize(25f);
      binding.chart.setEntryLabelColor(Color.parseColor("#B6b6b7"));
      binding.chart.setCenterTextColor(Color.parseColor("#B6b6b7"));
      binding.chart.extraBottomOffset = 10f
      binding.chart.extraTopOffset = 10f
      binding.chart.extraLeftOffset = 30f
      binding.chart.extraRightOffset =30f
    }

    private fun showPartOfstringWithDots(maxlenght: Int, name: String): String { // toon puntjes achter string na bepaalde lengte
        if(name.length > maxlenght){
            return name.substring(0, maxlenght) + "..";
        }
        return name;
    }

    private fun checkUsageStatsPermission() : Boolean { //bekijk UsageStatsPermission
        val appOpsManager = getSystemService(AppCompatActivity.APP_OPS_SERVICE) as AppOpsManager
        // `AppOpsManager.checkOpNoThrow` is deprecated from Android Q
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOpsManager.unsafeCheckOpNoThrow(
                "android:get_usage_stats",
                android.os.Process.myUid(), packageName
            )
        }
        else {
            appOpsManager.checkOpNoThrow(
                "android:get_usage_stats",
                android.os.Process.myUid(), packageName
            )
        }
        return mode == AppOpsManager.MODE_ALLOWED
    }


}

