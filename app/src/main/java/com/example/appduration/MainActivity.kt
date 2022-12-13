package com.example.appduration

import TestcommonFunctions
import TestcommonFunctions.Companion.getAllAppsAndTimeStamps
import TestcommonFunctions.Companion.getTotalTimeApps
import android.app.ActivityManager
import android.app.AppOpsManager
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.appduration.databinding.ActivityMainBinding
import com.github.mikephil.charting.animation.ChartAnimator
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.interfaces.dataprovider.BarDataProvider
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.renderer.BarChartRenderer
import com.github.mikephil.charting.utils.ColorTemplate
import com.github.mikephil.charting.utils.Transformer
import com.github.mikephil.charting.utils.Utils
import com.github.mikephil.charting.utils.ViewPortHandler
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId


class MainActivity : AppCompatActivity() { //order code straks
    private lateinit var binding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)
        if(!foregroundSerciceRunning()){ // start de CheckUseBlockedAppsService als deze nog niet is gestart (zet later in andere activity).
            var serviceIntent = Intent(this, CheckUseBlockedAppsService::class.java);// service for check time

            startForegroundService(serviceIntent);
            //https://stackoverflow.com/questions/33114063/how-do-i-properly-fire-action-request-ignore-battery-optimizations-intent
            //https://stackoverflow.com/questions/39256501/check-if-battery-optimization-is-enabled-or-not-for-an-app
            val pm = getSystemService(POWER_SERVICE) as PowerManager
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val myIntent = Intent()
            myIntent.action = Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS
            startActivity(myIntent)
            }
        }
        checkOverlayPermission(); // vraagt de gebruiker toegang zodat we over andere apps kunnen tekenen
        binding.btnRestricked.setOnClickListener {
            OpenRestrickedActivity();
        }
        //binding.txtFace
        if ( checkUsageStatsPermission() ) { // als we de toestemming hebben voor usage stats vind gebruik tijd voor alle apps
            findAppsDurationTimes()
        }
        else {
            // Navigate the user to the permission settings
            Intent( Settings.ACTION_USAGE_ACCESS_SETTINGS ).apply {
                startActivity( this )
            }
        }

    }

    override fun onStart() {
        super.onStart();
        CheckUseBlockedAppsService.testheropstart = true;
        var serviceIntent = Intent(this, WindowService::class.java);
        stopService(serviceIntent);
    }


    // method to ask user to grant the Overlay permission
    fun checkOverlayPermission() { // kijk voor toegang  zodat we over andere apps kunnen tekenen
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                // send user to the device settings
                val myIntent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                startActivity(myIntent)
            }
        }
    }

    override fun onDestroy() {
        var broadcastintent = Intent();
        broadcastintent.setAction("restartservice")
        super.onDestroy()
    }

    override fun onPause() {
        var broadcastintent = Intent();
        broadcastintent.setAction("restartservice")
        super.onPause()
    }

    private fun foregroundSerciceRunning(): Boolean { // kijk of CheckUseBlockedAppsService is gestart.
        //https://www.youtube.com/watch?v=bA7v1Ubjlzw
        var activitymanager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager;
        for (servise in activitymanager.getRunningServices(Integer.MAX_VALUE)) {
            if (CheckUseBlockedAppsService::class.java.name.equals(servise.service.className)) {
                return true;
            }
        }
        return false;
    }

    private fun findAppsDurationTimes(){ // zoek gebruik tijden voor alle apps.
        var foregroundAppPackageName : String? = null
        val currentTime = System.currentTimeMillis()
        val start = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        //https://stackoverflow.com/questions/59113756/android-get-usagestats-per-hour
        //bepaal tijd per app
        var UsageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
        var data = getAllAppsAndTimeStamps(start = start, currentTime = currentTime, UsageStatsManager); // kijk common functions
        var result = getTotalTimeApps(data); // kijk common functions
        result = result.toList().sortedBy { (_, value) -> value}.reversed().toMap() as HashMap<String, Double>
        putinfoonscreen(result);
        fillInChart(result);
        barchart();
    }

    private fun putinfoonscreen(result: HashMap<String, Double>){
        var textboxes = listOf(binding.txtapp1, binding.txtapp2, binding.txtapp3, binding.txtapp4);
        var icons = listOf(binding.iconimage1, binding.iconimage2, binding.iconimage3, binding.iconimage4)
        for (i in 0 until 4 step 1) { //plaats tijd meest gebruikte apps in correcte textboxes
            try {
                var d = packageManager.getApplicationIcon(result.keys.toList().get(i))
                icons.get(i).setImageDrawable(d)
                textboxes.get(i).text = ((result.values.toList().get(i) /60000 /60).toInt()).toString()+ "h " +   (60 * ("0." + (result.values.toList().get(i) /60000 /60).toString().split(".").get(1)).toDouble()).toInt() +"m";
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }

        val socialMediaapps =listOf("youtube", "facebook", "twitch", "twitter", "reddit", "facebook", "instagram", "wattsapp", "titok", "9gag", "discord", "pinterest", "bereal", "vimeo");
        var resultMediaApps = HashMap<String, Double>();
        var y = 0;
        while(resultMediaApps.size < 4 && y < result.size){ //plaats tijd meest gebruikte social media apps in correcte textboxes
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
        if(resultMediaApps.size > 1){ // pas ui op basis van aaantal social media
            resultMediaApps = resultMediaApps.toList().sortedBy { (_, value) -> value}.reversed().toMap() as HashMap<String, Double>
        }
        else{
            binding.MosedusedSocialApps.visibility = View.INVISIBLE;
        }
        if(resultMediaApps.size < 4){ // pas ui op basis van aaantal social media
            for(i in 2 until icons.size){
                icons.get(i).visibility = View.INVISIBLE;
                textboxes.get(i).visibility = View.INVISIBLE;
            }
            binding.middlesocials.visibility = View.INVISIBLE;
            binding.socailsVertical.layoutParams.height = binding.MosedusedSocialApps.layoutParams.height / 2;
            binding.MosedusedSocialApps.layoutParams.height = binding.MosedusedSocialApps.layoutParams.height / 2;
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

    public fun OpenRestrickedActivity() { // open andere pagina
        val intent = Intent(this, RestricktedAppsActivity::class.java);
        startActivity(intent);
    }

    private fun fillInChart(result: HashMap<String, Double>){ // vul de chart in //https://www.youtube.com/watch?v=S3zqxVoIUig
        val entries = ArrayList<PieEntry>();
        var other = 100F;
        for(i in 0..3){
            var name = TestcommonFunctions.getAppname(result.keys.toList().get(i), packageManager );
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

    private fun barchart(){ // vul barchart in
        var entries = ArrayList<BarEntry>();
        val xLabels: ArrayList<String> = ArrayList()
        for (i in 0 until 6 step 1) { // kijk tijd per uur
            val currentTime = System.currentTimeMillis() - (3600000 * i+ (LocalDateTime.now().second * 1000) + (LocalDateTime.now().minute * 60000));
            val start = System.currentTimeMillis() - (3600000 *(i+1) + (LocalDateTime.now().second * 1000) + (LocalDateTime.now().minute * 60000));//trek 1 h van de tijd af en zet de seconden en minuten op 0
            //https://stackoverflow.com/questions/59113756/android-get-usagestats-per-hour
            //bepaal tijd per app
            var UsageStatsManager = getSystemService(USAGE_STATS_SERVICE) as UsageStatsManager
            var datatime = getAllAppsAndTimeStamps(start = start, currentTime = currentTime, UsageStatsManager);
            var result = getTotalTimeApps(datatime);
            val to = (result.values.sum() / 60000).toFloat()
            entries.add(BarEntry(5- i.toFloat(), (result.values.sum() / 60000).toFloat()));
            xLabels.add((LocalDateTime.now().hour -6 + i).toString())
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
        val barChartRender =
            CustomBarChartRender(binding.barchart, binding.barchart.getAnimator(), binding.barchart.getViewPortHandler())
        barChartRender.setRadius(30)
        binding.barchart.setRenderer(barChartRender)

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

        yr.setValueFormatter(ClaimsYAxisValueFormatter())

        xl.setValueFormatter(IAxisValueFormatter { value, axis -> xLabels.get(value.toInt() % xLabels.size) })
        xl.setTextColor(Color.parseColor("#B6b6b7"))
        val l: Legend = binding.barchart.getLegend()
        binding.barchart.axisLeft.axisMinimum = 0f
        binding.barchart.axisRight.axisMinimum = 0f
        //zie doc https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/main/java/com/xxmassdeveloper/mpchartexample/HorizontalBarChartActivity.java
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

class ClaimsYAxisValueFormatter : IAxisValueFormatter { // ronde hoeken grafiek // https://medium.com/@makkenasrinivasarao1/line-chart-implementation-with-mpandroidchart-af3dd11804a7
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return value.toUInt().toString() + " m";
    }
}


class CustomBarChartRender( // https://itecnote.com/tecnote/android-mpandroidchart-round-edged-bar-chart/
    chart: BarDataProvider?,
    animator: ChartAnimator?,
    viewPortHandler: ViewPortHandler?
) :
    BarChartRenderer(chart, animator, viewPortHandler) {
    private val mBarShadowRectBuffer = RectF()
    private var mRadius = 0
    fun setRadius(mRadius: Int) {
        this.mRadius = mRadius
    }

    override fun drawDataSet(c: Canvas, dataSet: IBarDataSet, index: Int) {
        val trans: Transformer = mChart.getTransformer(dataSet.axisDependency)
        mBarBorderPaint.color = dataSet.barBorderColor
        mBarBorderPaint.strokeWidth = Utils.convertDpToPixel(dataSet.barBorderWidth)
        mShadowPaint.color = dataSet.barShadowColor
        val drawBorder = dataSet.barBorderWidth > 0f
        val phaseX = mAnimator.phaseX
        val phaseY = mAnimator.phaseY
        if (mChart.isDrawBarShadowEnabled) {
            mShadowPaint.color = dataSet.barShadowColor
            val barData = mChart.barData
            val barWidth = barData.barWidth
            val barWidthHalf = barWidth / 2.0f
            var x: Float
            var i = 0
            val count = Math.min(
                Math.ceil(
                    (dataSet.entryCount.toFloat() * phaseX).toDouble().toInt().toDouble()
                ), dataSet.entryCount.toDouble()
            )
            while (i < count) {
                val e = dataSet.getEntryForIndex(i)
                x = e.x
                mBarShadowRectBuffer.left = x - barWidthHalf
                mBarShadowRectBuffer.right = x + barWidthHalf
                trans.rectValueToPixel(mBarShadowRectBuffer)
                if (!mViewPortHandler.isInBoundsLeft(mBarShadowRectBuffer.right)) {
                    i++
                    continue
                }
                if (!mViewPortHandler.isInBoundsRight(mBarShadowRectBuffer.left)) break
                mBarShadowRectBuffer.top = mViewPortHandler.contentTop()
                mBarShadowRectBuffer.bottom = mViewPortHandler.contentBottom()
                c.drawRoundRect(mBarRect, mRadius.toFloat(), mRadius.toFloat(), mShadowPaint)
                i++
            }
        }

        // initialize the buffer
        val buffer = mBarBuffers[index]
        buffer.setPhases(phaseX, phaseY)
        buffer.setDataSet(index)
        buffer.setInverted(mChart.isInverted(dataSet.axisDependency))
        buffer.setBarWidth(mChart.barData.barWidth)
        buffer.feed(dataSet)
        trans.pointValuesToPixel(buffer.buffer)
        val isSingleColor = dataSet.colors.size == 1
        if (isSingleColor) {
            mRenderPaint.color = dataSet.color
        }
        var j = 0
        while (j < buffer.size()) {
            if (!mViewPortHandler.isInBoundsLeft(buffer.buffer[j + 2])) {
                j += 4
                continue
            }
            if (!mViewPortHandler.isInBoundsRight(buffer.buffer[j])) break
            if (!isSingleColor) {
                // Set the color for the currently drawn value. If the index
                // is out of bounds, reuse colors.
                mRenderPaint.color = dataSet.getColor(j / 4)
            }
            val path2: Path = roundRect(
                RectF(
                    buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                    buffer.buffer[j + 3]
                ), mRadius.toFloat(), mRadius.toFloat(), true, true, false, false
            )
            c.drawPath(path2, mRenderPaint)
            if (drawBorder) {
                val path: Path = roundRect(
                    RectF(
                        buffer.buffer[j], buffer.buffer[j + 1], buffer.buffer[j + 2],
                        buffer.buffer[j + 3]
                    ), mRadius.toFloat(), mRadius.toFloat(), true, true, false, false
                )
                c.drawPath(path, mBarBorderPaint)
            }
            j += 4
        }
    }

    private fun roundRect(
        rect: RectF,
        rx: Float,
        ry: Float,
        tl: Boolean,
        tr: Boolean,
        br: Boolean,
        bl: Boolean
    ): Path {
        var rx = rx
        var ry = ry
        val top = rect.top
        val left = rect.left
        val right = rect.right
        val bottom = rect.bottom
        val path = Path()
        if (rx < 0) rx = 0f
        if (ry < 0) ry = 0f
        val width = right - left
        val height = bottom - top
        if (rx > width / 2) rx = width / 2
        if (ry > height / 2) ry = height / 2
        val widthMinusCorners = width - 2 * rx
        val heightMinusCorners = height - 2 * ry
        path.moveTo(right, top + ry)
        if (tr) path.rQuadTo(0F, -ry, -rx, -ry) //top-right corner
        else {
            path.rLineTo(0F, -ry)
            path.rLineTo(-rx, 0F)
        }
        path.rLineTo(-widthMinusCorners, 0F)
        if (tl) path.rQuadTo(-rx, 0F, -rx, ry) //top-left corner
        else {
            path.rLineTo(-rx, 0F)
            path.rLineTo(0F, ry)
        }
        path.rLineTo(0F, heightMinusCorners)
        if (bl) path.rQuadTo(0F, ry, rx, ry) //bottom-left corner
        else {
            path.rLineTo(0F, ry)
            path.rLineTo(rx, 0F)
        }
        path.rLineTo(widthMinusCorners, 0F)
        if (br) path.rQuadTo(rx, 0F, rx, -ry) //bottom-right corner
        else {
            path.rLineTo(rx, 0F)
            path.rLineTo(0F, -ry)
        }
        path.rLineTo(0F, -heightMinusCorners)
        path.close() //Given close, last lineto can be removed.
        return path
    }
}