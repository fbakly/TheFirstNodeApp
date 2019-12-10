package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
        LineChart tempChart = (LineChart) findViewById(R.id.tempChart);
        LineChart humidityChart = (LineChart) findViewById(R.id.humidityChart);
        LineChart pressureChart = (LineChart) findViewById(R.id.pressureChart);
        LineChart lightChart = (LineChart) findViewById(R.id.lightChart);
        Spinner homePeriodSpinner = (Spinner) findViewById(R.id.homePeriodSpinner);
        ArrayList<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Hour");
        spinnerItems.add("Day");
        spinnerItems.add("Week");

        homePeriodSpinner.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, spinnerItems));

        Intent intent = getIntent();
        ArrayList<Device> devices = (ArrayList<Device>) Parcels.unwrap(intent.getExtras().getParcelable("devices"));
        nodeID.setText(devices.get(0).getDevice_id());
        String lastReading = devices.get(0).getPayloads().get(devices.get(0).getPayloads().size() - 1).getTemperature();
        nodeReading.setText(lastReading + (char) 0x00B0 + "C");

        LegendEntry tempLegendEntry = new LegendEntry();
        tempLegendEntry.label = "Temperature";
        tempLegendEntry.formColor = ColorTemplate.rgb("66FFFF");
        tempChart.getLegend().setCustom(Arrays.asList(tempLegendEntry));

        tempChart.setTouchEnabled(false);
        tempChart.setDragEnabled(false);
        tempChart.setDoubleTapToZoomEnabled(false);
        XAxis tempX = tempChart.getXAxis();
        tempX.setValueFormatter(new DateAxisFormatter(tempChart));
        tempX.setPosition(XAxis.XAxisPosition.BOTTOM);
        tempX.setLabelCount(5);
        tempX.setTextSize(9);
        tempChart.getDescription().setEnabled(false);

        List<Entry> tempEntries = new ArrayList<>();
        List<Entry> humidityEntries = new ArrayList<>();
        List<Entry> pressureEntries = new ArrayList<>();
        List<Entry> lightEntries = new ArrayList<>();
        ArrayList<Payload> payloads = devices.get(0).getPayloads();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd hh:mm:ss");

        for (Payload payload : payloads) {
            try {
                Date date = sdf.parse(payload.getTime_stamp());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                tempEntries.add(new Entry(calendar.getTimeInMillis(), Integer.valueOf(payload.getTemperature())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        LineDataSet lds = new LineDataSet(tempEntries, "Date");
        lds.setDrawCircles(false);
        LineData ld = new LineData(lds);
        lds.setColor(ColorTemplate.rgb("66FFFF"));
        tempChart.setData(ld);
        tempChart.invalidate();

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
