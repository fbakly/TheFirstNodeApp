/**
 * @Class: MyChart
 * @Description: Class that uses the MPAndroidChart library LineChart. Sets custom settings, and
 *                  refreshes graph
 * @Author: Fouad Elbakly
 */

package com.example.app1;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.Arrays;
import java.util.List;

public class MyChart {
    private LineChart chart;
    private String format;
    private LineData ld;
    private LineDataSet lds;

    public MyChart(LineChart chart) {
        this.chart = chart;
        ld = new LineData();
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public void setLegend(String legendLabel) {
        LegendEntry chartLegendEntry = new LegendEntry();
        chartLegendEntry.label = legendLabel;
        chartLegendEntry.formColor = ColorTemplate.rgb("66FFFF");
        chart.getLegend().setCustom(Arrays.asList(chartLegendEntry));
    }

    public void setLegend(String legendLabel, int color) {
        LegendEntry chartLegendEntry = new LegendEntry();
        chartLegendEntry.label = legendLabel;
        chartLegendEntry.formColor = color;
        chart.getLegend().setCustom(Arrays.asList(chartLegendEntry));
    }

    public void setSettings(String format, int labelCount) {
        this.format = format;
        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);
        XAxis tempX = chart.getXAxis();
        // Sets the axis formatter to DateAxisFormatter class
        tempX.setValueFormatter(new DateAxisFormatter(chart, format));
        tempX.setPosition(XAxis.XAxisPosition.BOTTOM);
        tempX.setLabelCount(labelCount);
        tempX.setTextSize(9);
        chart.getDescription().setEnabled(false);
    }

    public void setLineDataSetColor(int color) {
        lds.setColor(color);
    }

    public LineData getLd() {
        return ld;
    }

    public void setLd() {
        this.ld = new LineData(lds);
    }

    public LineDataSet getLds() {
        return lds;
    }

    public void setLds(List<Entry> entries, String label) {
        this.lds = new LineDataSet(entries, label);
    }

    public void drawCircles(boolean condition) {
        lds.setDrawCircles(condition);
    }

    public void setAnimation(int millis) {
        chart.animateX(millis);
    }

    public void refresh() {
        chart.setData(ld);
        chart.invalidate();
    }

    public LineChart getChart() {
        return chart;
    }
}
