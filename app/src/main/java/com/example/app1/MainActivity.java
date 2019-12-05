package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import com.github.mikephil.charting.data.Entry;

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
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        TextView nodeID = (TextView) findViewById(R.id.nodeID);
        TextView nodeReading = (TextView) findViewById(R.id.nodeReading);
        LineChart tempChart = (LineChart) findViewById(R.id.tempChart);
        LineChart humidityChart = (LineChart) findViewById(R.id.humidityChart);
        LineChart pressureChart = (LineChart) findViewById(R.id.pressureChart);
        LineChart lightChart = (LineChart) findViewById(R.id.lightChart);

        Intent intent = getIntent();
        ArrayList<Device> devices = (ArrayList<Device>) Parcels.unwrap(intent.getExtras().getParcelable("devices"));
        nodeID.setText(devices.get(0).getDevice_id());
        String lastReading = devices.get(0).getPayloads().get(devices.get(0).getPayloads().size() - 1).getTemperature();
        nodeReading.setText(lastReading + (char) 0x00B0 + "C");

        final Intent intent1 = new Intent(MainActivity.this, PayloadTable.class);
        intent1.putExtra("payloads", Parcels.wrap(devices.get(0).getPayloads()));
        intent1.putExtra("devices", Parcels.wrap(devices));
        intent1.putExtra("nodeID", devices.get(0).getDevice_id());
        nodeReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intent1);
            }
        });

    }
}
