package com.example.appduration

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.databinding.ActivityMainBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)

        //binding.txtFace
        if ( checkUsageStatsPermission() ) {
            findAppsDurationTimes()
        }
        else {
            // Navigate the user to the permission settings
            Intent( Settings.ACTION_USAGE_ACCESS_SETTINGS ).apply {
                startActivity( this )
            }
        }
    }

    private fun getAppname(packagenaam: String): String{ // https://stackoverflow.com/questions/11229219/android-how-to-get-application-name-not-package-name
        var applicationInfo: ApplicationInfo? = null
        try {
            applicationInfo = this.packageManager.getApplicationInfo(
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
    private fun showPartOfstringWithDots(maxlenght: Int, name: String): String {
        if(name.length > maxlenght){
            return name.substring(0, maxlenght) + "...";
        }
        return name;
    }
    private fun fillInChart(result: HashMap<String, Double>){ //https://www.youtube.com/watch?v=S3zqxVoIUig
        val entries = ArrayList<PieEntry>();
        var other = 100F;
        for(i in 0..3){
            var name = getAppname(result.keys.toList().get(i) );
            name = showPartOfstringWithDots(6,name);
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
        dataSet.setValueLinePart1OffsetPercentage(0F);
        dataSet.setValueLinePart1Length(0.2f);
        dataSet.setValueLinePart2Length(0f);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.valueLineColor = Color.TRANSPARENT


        binding.chart.setData(data);
        binding.chart.invalidate();
        binding.chart.setHoleColor(Color.TRANSPARENT);
        binding.chart.setHoleRadius(80F);
        binding.chart.getData().setDrawValues(false);
        binding.chart.getDescription().setEnabled(false);
        binding.chart.setEntryLabelTextSize(18F)
        binding.chart.getLegend().setEnabled(false)
        val textcenter = ((result.values.sum() /60000 /60).toInt()).toString()+ "h " +   (60 * ("0." + (result.values.sum() /60000 /60).toString().split(".").get(1)).toDouble()).toInt() +"m";
        binding.chart.setCenterText(textcenter);
        binding.chart.setCenterTextSize(25f);
        binding.chart.setEntryLabelColor(Color.parseColor("#B6b6b7"));
        binding.chart.setCenterTextColor(Color.parseColor("#B6b6b7"));
        binding.chart.extraBottomOffset = 10f
        barchart();
    }
    private fun barchart(){
        val entries = ArrayList<BarEntry>();
        for (i in 0 until 6 step 1) {
            val currentTime = System.currentTimeMillis() - (3600000 * i+ (LocalDateTime.now().second * 1000) + (LocalDateTime.now().minute * 60000));
            val start = System.currentTimeMillis() - (3600000 *(i+1) + (LocalDateTime.now().second * 1000) + (LocalDateTime.now().minute * 60000));//trek 1 h van de tijd af en zet de seconden en minuten op 0
            //https://stackoverflow.com/questions/59113756/android-get-usagestats-per-hour
            //bepaal tijd per app
            var datatime = getAllAppsAndTimeStamps(start = start, currentTime = currentTime);
            var result = getTotalTimeApps(datatime);
            val to = (result.values.sum() / 60000).toFloat()
            entries.add(BarEntry(i.toFloat(), (result.values.sum() / 60000).toFloat() + 5));
        }
        val colors = ArrayList<Int>();
        for(color in ColorTemplate.MATERIAL_COLORS){
            colors.add(color)
        }

        for(color in ColorTemplate.VORDIPLOM_COLORS){
            colors.add(color);
        }

        val dataSet = BarDataSet(entries, "Expense Category");
        dataSet.setColors(Color.parseColor("#1400FF"))
        dataSet.setBarBorderColor(Color.BLACK)
        dataSet.setBarBorderWidth(6f)


        val data = BarData(dataSet);
        data.setBarWidth(0.5f);
        binding.barchart.setData(data);
        binding.barchart.getData().setDrawValues(false);
        binding.barchart.getDescription().setEnabled(false);
        binding.barchart.getLegend().setEnabled(false)
        binding.barchart.extraBottomOffset = 10f
        binding.barchart.getAxisLeft().setDrawGridLines(false);
        binding.barchart.getXAxis().setDrawGridLines(false);
        binding.barchart.setDrawGridBackground(false);

        val yl: YAxis = binding.barchart.getAxisLeft()
        yl.setDrawAxisLine(false);
        yl.setEnabled(false);

        val yr: YAxis = binding.barchart.getAxisRight()
        yr.setDrawAxisLine(false);
        yr.setTextColor(Color.parseColor("#B6b6b7"))
        val xl: XAxis = binding.barchart.getXAxis()
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false)
        val xLabels: ArrayList<String> = ArrayList()
        xLabels.add("1")
        xLabels.add("2")
        xLabels.add("3")
        xLabels.add("4")
        xLabels.add("5")
        xLabels.add("6")

        xl.setValueFormatter(IAxisValueFormatter { value, axis -> xLabels.get(value.toInt() % xLabels.size) })
        xl.setTextColor(Color.parseColor("#B6b6b7"))
        val l: Legend = binding.barchart.getLegend()
        binding.barchart.axisLeft.axisMinimum = 0f
        binding.barchart.axisRight.axisMinimum = 0f
        //zie doc https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/main/java/com/xxmassdeveloper/mpchartexample/HorizontalBarChartActivity.java
    }




        private fun findAppsDurationTimes(){
        var foregroundAppPackageName : String? = null
        val currentTime = System.currentTimeMillis()
        val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        //https://stackoverflow.com/questions/59113756/android-get-usagestats-per-hour
        //bepaal tijd per app
        var data = getAllAppsAndTimeStamps(start = start, currentTime = currentTime);
        var result = getTotalTimeApps(data);
        result = result.toList().sortedBy { (_, value) -> value}.reversed().toMap() as HashMap<String, Double>
        putinfoonscreen(result);
        fillInChart(result)
    }

    private fun putinfoonscreen(result: HashMap<String, Double>){
        var textboxes = listOf(binding.txtapp1, binding.txtapp2, binding.txtapp3, binding.txtapp4);
        var icons = listOf(binding.iconimage1, binding.iconimage2, binding.iconimage3, binding.iconimage4)
        for (i in 0 until 4 step 1) {
            try {
                var d = packageManager.getApplicationIcon(result.keys.toList().get(i))
                icons.get(i).setImageDrawable(d)
                textboxes.get(i).text = ((result.values.toList().get(i) /60000 /60).toInt()).toString()+ "h " +   (60 * ("0." + (result.values.toList().get(i) /60000 /60).toString().split(".").get(1)).toDouble()).toInt() +"m";
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }

        val socialMediaapps =listOf("youtube", "facebook", "twitch", "twitter", "reddit", "facebook", "instagram", "wattsapp", "titok", "9gag", "discord");
        var resultMediaApps = HashMap<String, Double>();
        var y = 0;
        while(resultMediaApps.size < 4 && y < result.size){
            for (i in 0 until socialMediaapps.size step 1) {
                val value =result.keys.toList().get(y);
                val x = socialMediaapps.get(i);
                if(result.keys.toList().get(y).contains(socialMediaapps.get(i))){
                    resultMediaApps.put(result.keys.toList().get(y), result.values.toList().get(y));
                }
            }
            y++
        }
        textboxes = listOf(binding.txtappSocial1, binding.txtappSocial2, binding.txtappSocial3, binding.txtappSocial4);
        icons = listOf(binding.iconimageSocial1, binding.iconimageSocial2, binding.iconimageSocial3, binding.iconimageSocial4);
        if(resultMediaApps.size > 0){ // fix error wanneer je maar één of minder social media hebt
            resultMediaApps = resultMediaApps.toList().sortedBy { (_, value) -> value}.reversed().toMap() as HashMap<String, Double>
        }
        for (i in 0 until resultMediaApps.size step 1) {
            try {
                var d = packageManager.getApplicationIcon(resultMediaApps.keys.toList().get(i))
                icons.get(i).setImageDrawable(d)
                textboxes.get(i).text = ((resultMediaApps.values.toList().get(i) /60000 /60).toInt()).toString()+ "h " +   (60 * ("0." + (resultMediaApps.values.toList().get(i) /60000 /60).toString().split(".").get(1)).toDouble()).toInt() +"m";
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }
        var te = "";
    }
    private fun getTotalTimeApps(data:  HashMap<String, ArrayList<UsageEvents.Event>>) : HashMap<String, Double> {
        var result = HashMap<String, Double>();
        for((k, v) in data){
            var time = 0.0;
            for (i in 0 until v.size -1 step 1) {
                if(v.get(i).eventType == 1 && v.get(i + 1).eventType == 2){
                    time += (v.get(i + 1).timeStamp - v.get(i).timeStamp);
                }
            }
            result.put(k,   time);
        }
        return result;
    }
    private fun getAllAppsAndTimeStamps(start: Long, currentTime: Long) : HashMap<String, ArrayList<UsageEvents.Event>> {
        //test niet als app in klein schermje zit (youtube of maps als voorbeeld) andere apps hebben dit ook
        val usageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        val stats = usageStatsManager.queryEvents(start, currentTime)
        var data : HashMap<String, ArrayList<UsageEvents.Event>>
                = HashMap<String,  ArrayList<UsageEvents.Event>> ();
        //get start en stop tijden
        while (stats.hasNextEvent()) {
            val event = UsageEvents.Event()
            stats.getNextEvent(event)
            if(event.eventType == UsageEvents.Event.ACTIVITY_RESUMED||
                event.eventType == UsageEvents.Event.ACTIVITY_PAUSED){
                if(data.get(event.packageName) == null){
                    data.put(event.packageName, ArrayList<UsageEvents.Event>(Arrays.asList(event)));
                }else{
                    data.get(event.packageName)?.add(event);
                }
            }
        }
        return data;
    }
    private fun checkUsageStatsPermission() : Boolean {
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