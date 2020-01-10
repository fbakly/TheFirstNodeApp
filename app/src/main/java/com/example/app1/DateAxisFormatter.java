/**
 * @Class: DateAxisFormatter
 * @Description: Gets time in millis and formats it to given date format
 * @Author: Fouad Elbakly
 */

package com.example.app1;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateAxisFormatter extends ValueFormatter {
    private Chart chart;
    private String format;
    public DateAxisFormatter(Chart chart, String format) {
        super();
        this.chart = chart;
        this.format = format;
    }


    // This function receives the time in milliseconds and transforms it into a string to
    // Have the date on the x-axis
    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        String result = "";
        Date date = new Date((long)value);
        SimpleDateFormat format = new SimpleDateFormat(this.format);
        format.setTimeZone(TimeZone.getDefault());
        result = format.format(date);
        return result;
    }
}
