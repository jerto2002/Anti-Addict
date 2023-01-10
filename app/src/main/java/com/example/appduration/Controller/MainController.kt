package com.example.testmvc.Controller

import android.app.usage.UsageStatsManager
import android.content.pm.PackageManager
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Path
import android.graphics.RectF
import android.view.View
import com.example.appduration.Functions.GetInstalledApplicationsInfo
import com.example.appduration.View.MainActivity
import com.example.testmvc.Model.HomeScreenModel
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
import java.time.Duration
import java.time.LocalDateTime

class MainController {
    private lateinit var mainActivity: MainActivity;
    private lateinit var model : HomeScreenModel;

    constructor(mainActivity: MainActivity){
        model = HomeScreenModel(this);
        this.mainActivity = mainActivity;
    }

    fun FillPieChart(UsageStatsManager: UsageStatsManager, packageManager: PackageManager) {
        model.findAppsDurationTimes(UsageStatsManager,packageManager);
    }

    fun FillBarChart(UsageStatsManager: UsageStatsManager) {
        model.findDurationTimeOverHours(UsageStatsManager);
    }

    fun onFillPieChart(result: HashMap<String, Double>, packageManager: PackageManager){
        //this.mainActivity.piechart = drinkname;
        val entries = ArrayList<PieEntry>();
      var other = 100F;
      var amount = 3;
      if(amount > result.size){
          amount = result.size;
      }
      for(i in 0 until amount){
          var name = GetInstalledApplicationsInfo.getAppname(result.keys.toList().get(i), packageManager);
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

        this.mainActivity.piechart?.setData(data);
        this.mainActivity.piechart?.invalidate();
        this.mainActivity.piechart?.setHoleColor(Color.TRANSPARENT);
        this.mainActivity.piechart?.setHoleRadius(85F);
        this.mainActivity.piechart?.getData()?.setDrawValues(false);
        this.mainActivity.piechart?.getDescription()?.setEnabled(false);
        this.mainActivity.piechart?.setEntryLabelTextSize(16F)
        this.mainActivity.piechart?.getLegend()?.setEnabled(false)
        val textcenter = ((result.values.sum() /60000 /60).toInt()).toString()+ "h " +   (60 * ("0." + (result.values.sum() /60000 /60).toString().split(".").get(1)).toDouble()).toInt() +"m";
        this.mainActivity.piechart?.setCenterText(textcenter);
        this.mainActivity.piechart?.setCenterTextSize(25f);
        this.mainActivity.piechart?.setEntryLabelColor(Color.parseColor("#B6b6b7"));
        this.mainActivity.piechart?.setCenterTextColor(Color.parseColor("#B6b6b7"));
        this.mainActivity.piechart?.extraBottomOffset = 10f
        this.mainActivity.piechart?.extraTopOffset = 10f
        this.mainActivity.piechart?.extraLeftOffset = 30f
        this.mainActivity.piechart?.extraRightOffset =30f

    }


    fun putinfoonscreen(result: HashMap<String, Double>, packageManager: PackageManager){
        var textboxes = listOf(this.mainActivity.textViews[0], this.mainActivity.textViews[1], this.mainActivity.textViews[2], this.mainActivity.textViews[3]);
        var icons = listOf(this.mainActivity.iconimages[0], this.mainActivity.iconimages[1], this.mainActivity.iconimages[2], this.mainActivity.iconimages[3])
        if(result.size < 3){
            this.mainActivity.MosedusedApps?.visibility = View.GONE;
            this.mainActivity.title2?.visibility = View.GONE;
        }
        var amount = 4;
        if(result.size < amount){
            amount = result.size;
        }
        for (i in 0 until amount step 1) { //plaats tijd meest gebruikte apps in correcte textboxes
            try {
                var d = packageManager.getApplicationIcon(result.keys.toList().get(i))
                icons.get(i)?.setImageDrawable(d)
                textboxes.get(i)?.text = ((result.values.toList().get(i) /60000 /60).toInt()).toString()+ "h " +   (60 * ("0." + (result.values.toList().get(i) /60000 /60).toString().split(".").get(1)).toDouble()).toInt() +"m";
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
        textboxes = listOf(this.mainActivity.textViewsSocials[0], this.mainActivity.textViewsSocials[1], this.mainActivity.textViewsSocials[2], this.mainActivity.textViewsSocials[3]);
        icons = listOf(this.mainActivity.iconimagesSocials[0], this.mainActivity.iconimagesSocials[1], this.mainActivity.iconimagesSocials[2], this.mainActivity.iconimagesSocials[3]);
        if(resultMediaApps.size > 1){ // pas ui op basis van aaantal social media
            resultMediaApps = resultMediaApps.toList().sortedBy { (_, value) -> value}.reversed().toMap() as HashMap<String, Double>
        }
        else{
            this.mainActivity.title3?.visibility= View.GONE;
            this.mainActivity.MosedusedSocialApps?.visibility = View.GONE;
        }
        if(resultMediaApps.size < 4){ // pas ui op basis van aaantal social media
            for(i in 2 until icons.size){
                icons.get(i).visibility = View.GONE;
                textboxes.get(i).visibility = View.GONE;
            }
            this.mainActivity.Middlesocials?.visibility = View.GONE;
            this.mainActivity.SocailsVertical?.layoutParams?.height =
                this.mainActivity.MosedusedSocialApps?.layoutParams?.height?.div(2);
            this.mainActivity.MosedusedSocialApps?.layoutParams?.height =
                this.mainActivity.MosedusedSocialApps?.layoutParams?.height?.div(2);
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

    fun OnFillBarchart(results: ArrayList<Float>) { // vul barchart in
        var entries = ArrayList<BarEntry>();
        val xLabels: ArrayList<String> = ArrayList()
        for (i in 0 until results.size step 1) { // kijk tijd per uur
            entries.add(BarEntry(5- i.toFloat(), results.get(i)));
            xLabels.add((LocalDateTime.now().minus(Duration.ofHours((6 - i).toLong())) ).hour.toString())
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
            CustomBarChartRender(this.mainActivity.barchart, this.mainActivity.barchart?.getAnimator(), this.mainActivity.barchart?.getViewPortHandler())
        barChartRender.setRadius(30)
        this.mainActivity.barchart?.setRenderer(barChartRender)

        val data = BarData(dataSet);
        data.setBarWidth(0.5f);
        this.mainActivity.barchart?.setData(data);
      this.mainActivity.barchart?.getData()?.setDrawValues(false);
      this.mainActivity.barchart?.getDescription()?.setEnabled(false);
      this.mainActivity.barchart?.getLegend()?.setEnabled(false)
      this.mainActivity.barchart?.extraBottomOffset = 10f
      this.mainActivity.barchart?.getAxisLeft()?.setDrawGridLines(false);
      this.mainActivity.barchart?.getXAxis()?.setDrawGridLines(false);
      this.mainActivity.barchart?.setDrawGridBackground(false);

        val yl: YAxis = this.mainActivity.barchart!!.getAxisLeft()
        yl.setDrawAxisLine(false);
        yl.setEnabled(false);

        val yr: YAxis = this.mainActivity.barchart!!.getAxisRight()
        yr.setDrawAxisLine(false);
        yr.setTextColor(Color.parseColor("#B6b6b7"))
        val xl: XAxis = this.mainActivity.barchart!!.getXAxis()
        xl.setPosition(XAxis.XAxisPosition.BOTTOM);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false)

        yr.setValueFormatter(ClaimsYAxisValueFormatter())

        xl.setValueFormatter(IAxisValueFormatter { value, axis -> xLabels.get(value.toInt() % xLabels.size) })
        xl.setTextColor(Color.parseColor("#B6b6b7"))
        val l: Legend = this.mainActivity.barchart!!.getLegend()
      this.mainActivity.barchart?.axisLeft?.axisMinimum = 0f
      this.mainActivity.barchart?.axisRight!!.axisMinimum = 0f
        //zie doc https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/main/java/com/xxmassdeveloper/mpchartexample/HorizontalBarChartActivity.java
    }

    public fun showPartOfstringWithDots(maxlenght: Int, name: String): String { // toon puntjes achter string na bepaalde lengte
        if(name.length > maxlenght){
            return name.substring(0, maxlenght) + "..";
        }
        return name;
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