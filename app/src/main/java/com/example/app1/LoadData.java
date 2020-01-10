/**
 * @Class: LoadData
 * @Description: Gets JSON objects from PHP script on the web server
 * @Author: Fouad Elbakly
 */

package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LoadData extends AppCompatActivity {
    ArrayList<Device> devices;
    // URL of the php script which gets data from the sql database on the server
    final String url = "http://lora.fambaan.com/php/getPayloads.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_data);

        // Get load data text vie and Set the load data text to center screen
        final TextView loadDataText = (TextView) findViewById(R.id.loadDataText);
        loadDataText.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);

        devices = new ArrayList<>();
        final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        // Get a request Queue instance
        RequestQueue queue = MySingleton.getInstance(this.getApplicationContext()).getRequestQueue();

        // Create a json array request which contains a listener where on response gets a json object array
        final JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            // On connection successful
            @Override
            public void onResponse(JSONArray response) {
                try {
                    // remove loading panel
                    findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                    // Get number of JSON objects
                    int length = response.length();
                    // Calculate step size so that only 2000 data points are parsed
                    int stepSize = (length >= 2000) ? length / 2000 : 1;
                    // Loop through JSON objects with step size
                    for (int index = 0; index < length; index += stepSize) {
                        // Get JSON object as index
                        JSONObject row = (JSONObject) response.get(index);
                        // Create a temp device with the device id of the json object
                        Device tempDevice = new Device(row.getString("device_id"));
                        // if the devices arraylist doesn't contain the tempdevice add it to the arraylist
                        if (!devices.contains(tempDevice))
                            devices.add(tempDevice);
                        // Format timestamp to Netherlands time
                        String timeStamp = row.getString("time_stamp");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
                        Date timeStampDate = sdf.parse(timeStamp);
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(timeStampDate);
                        calendar.add(Calendar.HOUR_OF_DAY, 2);
                        timeStamp = sdf.format(calendar.getTime()).toString();
                        // Get device from arraylist and add payload to it
                        devices.get(devices.indexOf(tempDevice)).addToPayloads(new Payload(
                                row.getString("device_id"),
                                timeStamp,
                                row.getString("temperature"),
                                row.getString("humidity"),
                                row.getString("barometric"),
                                row.getString("luminosity")
                        ));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // Get intent if any existing
                Intent redirect = getIntent();
                // Create a new intent to switch to main activity
                Intent intent = new Intent(LoadData.this, MainActivity.class);

                // Add devices arraylist to intent as a parcelable using the parcels library
                // To send to main acitivity
                intent.putExtra("devices", Parcels.wrap(devices));
                // Add flags to remove from activity stack
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            // On error
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                // make loading panel disappear
                findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                // Set error text
                loadDataText.setText("Error loading data\nClick here to retry");
                // Set on on click listener to resetart activity to retry
                loadDataText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(LoadData.this, LoadData.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
            }
        });

        // Adds the jsonArrayRequest to the request quque of the MySingelton queue
        MySingleton.getInstance(LoadData.this).addToRequestQueue(jsonArrayRequest);
    }
}
