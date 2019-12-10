package com.example.app1;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateAxisFormatter extends ValueFormatter {
    private Chart chart;
    public DateAxisFormatter(Chart chart) {
        super();
        this.chart = chart;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        String result = "";
        Date date = new Date((long)value);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM H:mm");
        format.setTimeZone(TimeZone.getDefault());
        result = format.format(date);
        return result;
    }
}
