package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;
import java.util.ArrayList;

public class LoadData extends AppCompatActivity {
    ArrayList<Device> devices;
    final String url = "http://lora.fambaan.com/php/getPayloads.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data);

        devices = new ArrayList<>();

        // Get a request Queue
        RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    int length = response.length();
                    int stepSize = (length >= 2000) ? length / 2000 : 1;
                    for (int index = 0; index < length; index += stepSize) {
                        JSONObject row = (JSONObject) response.get(index);
                        Device tempDevice = new Device(row.getString("device_id"));
                        if (!devices.contains(tempDevice))
                            devices.add(tempDevice);
                        devices.get(devices.indexOf(tempDevice)).addToPayloads(new Payload(
                                row.getString("device_id"),
                                row.getString("time_stamp"),
                                row.getString("temperature"),
                                row.getString("humidity"),
                                row.getString("barometric"),
                                row.getString("luminosity")
                        ));
                        final String device_id = row.getString("device_id");
                        final String time_stamp = row.getString("time_stamp");
                        final String temperature = row.getString("temperature");
                        final String humidity = row.getString("humidity");
                        final String barometric = row.getString("barometric");
                        final String luminosity = row.getString("luminosity");

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent redirect = getIntent();
                Intent intent;
                if (redirect.getExtras() == null || (redirect.getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == 0) {
                    intent = new Intent(LoadData.this, MainActivity.class);
                } else {

                    String destination = redirect.getExtras().getString("class");
                    switch (destination) {
                        case "MainActivity":
                            intent = new Intent(LoadData.this, MainActivity.class);
                            break;
                        case "PayloadTable":
                            intent = new Intent(LoadData.this, PayloadTable.class);
                            break;
                        default:
                            intent = new Intent(LoadData.this, MainActivity.class);
                            break;
                    }
                    intent.putExtra("nodeID", redirect.getExtras().getString("nodeID"));
                }
                intent.putExtra("devices", Parcels.wrap(devices));
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        MySingleton.getInstance(LoadData.this).addToRequestQueue(jsonArrayRequest);
    }
}
