package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Button refresh = (Button) findViewById(R.id.refreshButtonHome);
        TextView nodeID = (TextView) findViewById(R.id.nodeID);
        TextView nodeReading = (TextView) findViewById(R.id.nodeReading);
        final MyChart tempChart = new MyChart((LineChart) findViewById(R.id.tempChart));
        final MyChart humidityChart = new MyChart((LineChart) findViewById(R.id.humidityChart));
        final MyChart pressureChart = new MyChart((LineChart) findViewById(R.id.pressureChart));
        final MyChart lightChart = new MyChart((LineChart) findViewById(R.id.lightChart));
        final Spinner homePeriodSpinner = (Spinner) findViewById(R.id.homePeriodSpinner);
        ArrayList<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Hour");
        spinnerItems.add("Day");
        spinnerItems.add("Week");
        spinnerItems.add("Since Start");
        final int[] labelCount = {0};
        final String[] format = {""};

        homePeriodSpinner.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerItems));

        Intent intent = getIntent();
        ArrayList<Device> devices = (ArrayList<Device>) Parcels.unwrap(intent.getExtras().getParcelable("devices"));
        nodeID.setText(devices.get(0).getDevice_id());
        String lastReading = devices.get(0).getPayloads().get(devices.get(0).getPayloads().size() - 1).getTemperature();
        nodeReading.setText(lastReading + (char) 0x00B0 + "C");

        final List<Entry> tempEntries = new ArrayList<>();
        final List<Entry> humidityEntries = new ArrayList<>();
        final List<Entry> pressureEntries = new ArrayList<>();
        final List<Entry> lightEntries = new ArrayList<>();
        final ArrayList<Payload> payloads = devices.get(0).getPayloads();

        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

        final String[] selectedPeriod = {homePeriodSpinner.getSelectedItem().toString()};
        String latestEntryTime = payloads.get(payloads.size() - 1).getTime_stamp();
        Date lastEntry = new Date();
        final Calendar calendar = Calendar.getInstance();
        try {
            lastEntry = sdf.parse(latestEntryTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        final Date[] targetDate = {new Date()};

        final Date finalLastEntry = lastEntry;
        homePeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedPeriod[0] = homePeriodSpinner.getSelectedItem().toString();

                switch (selectedPeriod[0]) {
                    case "Hour":
                        calendar.setTime(finalLastEntry);
                        calendar.add(Calendar.HOUR, -1);
                        targetDate[0] = calendar.getTime();
                        labelCount[0] = 12;
                        format[0] = "m:H";
                        break;
                    case "Day":
                        calendar.setTime(finalLastEntry);
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        targetDate[0] = calendar.getTime();
                        labelCount[0] = 12;
                        format[0] = "H";
                        break;
                    case "Week":
                        calendar.setTime(finalLastEntry);
                        calendar.add(Calendar.DAY_OF_MONTH, -7);
                        targetDate[0] = calendar.getTime();
                        labelCount[0] = 7;
                        format[0] = "E";
                        break;
                    case "Since Start":
                        String startDate = payloads.get(0).getTime_stamp();
                        labelCount[0] = 5;
                        format[0] = "dd/M";
                        try {
                            targetDate[0] = sdf.parse(startDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }
                tempEntries.clear();
                humidityEntries.clear();
                pressureEntries.clear();
                lightEntries.clear();
                for (int index = payloads.size() - 1; true && index >= 0; index--) {
                    Payload tempPayload = payloads.get(index);
                    try {
                        Date tempPayloadDate = sdf.parse(tempPayload.getTime_stamp());
                        calendar.setTime(tempPayloadDate);
                        if (tempPayloadDate.after(targetDate[0]) || tempPayloadDate.equals(targetDate[0])) {
                            tempEntries.add(new Entry(calendar.getTimeInMillis(), Float.valueOf(tempPayload.getTemperature())));
                            humidityEntries.add(new Entry(calendar.getTimeInMillis(), Float.valueOf(tempPayload.getHumidity())));
                            pressureEntries.add(new Entry(calendar.getTimeInMillis(), Float.valueOf(tempPayload.getBarometric())));
                            lightEntries.add(new Entry(calendar.getTimeInMillis(), Float.valueOf(tempPayload.getLuminostiy())));
                        } else
                            break;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                tempChart.setSettings(format[0], labelCount[0]);
                tempChart.setLds(tempEntries, "Temperature");
                tempChart.setLegend("Temperature", ColorTemplate.rgb("B00103"));
                tempChart.drawCircles(true);
                tempChart.setLd();
                tempChart.setLineDataSetColor(ColorTemplate.rgb("B00103"));
                tempChart.refresh();

                humidityChart.setSettings(format[0], labelCount[0]);
                humidityChart.setLds(humidityEntries, "Humidity");
                humidityChart.drawCircles(false);
                humidityChart.setLd();
                humidityChart.setLineDataSetColor(ColorTemplate.rgb("66FFFF"));
                humidityChart.refresh();

                pressureChart.setSettings(format[0], labelCount[0]);
                pressureChart.setLds(pressureEntries, "Pressure");
                pressureChart.setLegend("Pressure", ColorTemplate.rgb("9D7228"));
                pressureChart.drawCircles(false);
                pressureChart.setLd();
                pressureChart.setLineDataSetColor(ColorTemplate.rgb("9D7228"));
                pressureChart.refresh();

                lightChart.setSettings(format[0], labelCount[0]);
                lightChart.setLds(lightEntries, "Light");
                lightChart.setLegend("Light", ColorTemplate.rgb("FFD700"));
                lightChart.drawCircles(false);
                lightChart.setLd();
                lightChart.setLineDataSetColor(ColorTemplate.rgb("FFD700"));
                lightChart.refresh();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        final Intent intent1 = new Intent(MainActivity.this, PayloadTable.class);
        intent1.putExtra("devices", Parcels.wrap(devices));
        intent1.putExtra("nodeID", devices.get(0).getDevice_id());
        nodeReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent1);
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoadData.class);
                intent.putExtra("class", "MainActivity");
                startActivity(intent);
            }
        });
    }
}
