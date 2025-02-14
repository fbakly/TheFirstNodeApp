/**
 * @Class: Mainactivity
 * @Description: Creates the MainActivity which shows the graphs and latest readings
 *                  also sends the payload to the PayloadTable class
 * @Author: Fouad Elbakly
 */

package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.parceler.Parcels;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // Get Views from the layout files via their IDs
        Button refresh = (Button) findViewById(R.id.refreshButtonHome);
        final TextView nodeID = (TextView) findViewById(R.id.nodeID);
        final TextView nodeReading = (TextView) findViewById(R.id.nodeReading);
        final TextView nodeReadingHumidity = (TextView) findViewById(R.id.nodeReadingHumidity);
        final TextView nodeReadingLight = (TextView) findViewById(R.id.nodeReadingLight);
        final TextView nodeReadingPressure = (TextView) findViewById(R.id.nodeReadingPressure);
        final MyChart tempChart = new MyChart((LineChart) findViewById(R.id.tempChart));
        final MyChart humidityChart = new MyChart((LineChart) findViewById(R.id.humidityChart));
        final MyChart pressureChart = new MyChart((LineChart) findViewById(R.id.pressureChart));
        final MyChart lightChart = new MyChart((LineChart) findViewById(R.id.lightChart));
        final Spinner homePeriodSpinner = (Spinner) findViewById(R.id.homePeriodSpinner);

        // Make a new arraylist of string to add to the drop down menu as items
        ArrayList<String> spinnerItems = new ArrayList<>();
        spinnerItems.add("Hour");
        spinnerItems.add("Day");
        spinnerItems.add("Week");
        spinnerItems.add("Since Start");

        // Set the home spinner's (drop down menu) adapter to the arraylist
        homePeriodSpinner.setAdapter(new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_dropdown_item, spinnerItems));

        // Set an empty label count and format due to it changing depending on the chosen period
        // from the spinner
        final int[] labelCount = {0};
        final String[] format = {""};

        // Get the intent sent from the LoadData class and get the data that is sent
        Intent intent = getIntent();
        final ArrayList<Device> devices = (ArrayList<Device>) Parcels.unwrap(intent.getExtras().getParcelable("devices"));

        // Calls the setLatestReading function to display the last read values on textually
        setLatestReading(devices, nodeID, nodeReading, nodeReadingHumidity, nodeReadingLight,
                nodeReadingPressure);

        // Create empty lists of type Entry for each of readings received
        final List<Entry> tempEntries = new ArrayList<>();
        final List<Entry> humidityEntries = new ArrayList<>();
        final List<Entry> pressureEntries = new ArrayList<>();
        final List<Entry> lightEntries = new ArrayList<>();

        // Get the payloads of the first device since only 1 device is currently available
        final ArrayList<Payload> payloads = devices.get(0).getPayloads();

        // Create a date formatter to parse the received time_stamp string into a Date
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");

        // Create an array of string to get the selected period
        // An array is required due to it being called in the spinner onItemSelectedListener
        final String[] selectedPeriod = {homePeriodSpinner.getSelectedItem().toString()};

        // Get the time_stamp of the last entry
        final String latestEntryTime = payloads.get(payloads.size() - 1).getTime_stamp();

        Date lastEntry = new Date();
        final Calendar calendar = Calendar.getInstance();
        try {
            // Parse the last entry time to the lastEntry Date type
            lastEntry = sdf.parse(latestEntryTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        final Date[] targetDate = {new Date()};

        // Create a final variable due to it being used in a subclass
        final Date finalLastEntry = lastEntry;

        // Set a listener on the home spinner to determine the time period selected
        homePeriodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Get the selected time period
                selectedPeriod[0] = homePeriodSpinner.getSelectedItem().toString();

                // Depending on the selected period the target date is calculated, number of labels
                // is set and the display format is set
                switch (selectedPeriod[0]) {
                    case "Hour":
                        calendar.setTime(finalLastEntry);
                        calendar.add(Calendar.HOUR_OF_DAY, -1);
                        targetDate[0] = calendar.getTime();
                        labelCount[0] = 4;
                        format[0] = "HH:mm";
                        break;
                    case "Day":
                        calendar.setTime(finalLastEntry);
                        calendar.add(Calendar.DAY_OF_MONTH, -1);
                        targetDate[0] = calendar.getTime();
                        labelCount[0] = 4;
                        format[0] = "HH:mm d";
                        break;
                    case "Week":
                        calendar.setTime(finalLastEntry);
                        calendar.add(Calendar.DAY_OF_MONTH, -7);
                        targetDate[0] = calendar.getTime();
                        labelCount[0] = 7;
                        format[0] = "E";
                        break;
                    case "Since Start":
                        calendar.setTime(finalLastEntry);
                        String startDate = payloads.get(0).getTime_stamp();
                        labelCount[0] = 5;
                        format[0] = "dd/MM/yy";
                        try {
                            targetDate[0] = sdf.parse(startDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }

                // Clear the entries so it doesn't overlap with entries of previous period selection
                tempEntries.clear();
                humidityEntries.clear();
                pressureEntries.clear();
                lightEntries.clear();

                // Loop through the payloads to add the entries starting from the target date
                // get date in milliseconds which calls the DateAxisFormatter class due to the inability
                // to plot with string
                for (Payload tempPayload : payloads) {
                    try {
                        Date tempPayloadDate = sdf.parse(tempPayload.getTime_stamp());
                        calendar.setTime(tempPayloadDate);
                        if (tempPayloadDate.after(targetDate[0]) || tempPayloadDate.equals(targetDate[0])) {
                            tempEntries.add(new Entry(calendar.getTimeInMillis(), Float.valueOf(tempPayload.getTemperature())));
                            humidityEntries.add(new Entry(calendar.getTimeInMillis(), Float.valueOf(tempPayload.getHumidity())));
                            pressureEntries.add(new Entry(calendar.getTimeInMillis(), Float.valueOf(tempPayload.getBarometric())));
                            lightEntries.add(new Entry(calendar.getTimeInMillis(), Float.valueOf(tempPayload.getLuminostiy())));
                        }
                        // stop looping if the current looped time stamp is equal to the latest entry time
                        if (tempPayload.getTime_stamp().equals(latestEntryTime))
                            break;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                // Calls the set chart method
                setChart(tempChart, format[0], labelCount[0], tempEntries, "Temperature", ColorTemplate.rgb("B00103"));
                setChart(humidityChart, format[0], labelCount[0], humidityEntries, "Humidity", ColorTemplate.rgb("66FFFF"));
                setChart(pressureChart, format[0], labelCount[0], pressureEntries, "Pressure", ColorTemplate.rgb("9D7228"));
                setChart(lightChart, format[0], labelCount[0], lightEntries, "Light", ColorTemplate.rgb("FFD700"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set an on click listener on the node reading to start an intent to take the user to the
        // PayloadTable activity
        nodeReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PayloadTable.class);
                intent.putExtra("devices", Parcels.wrap(devices));
                intent.putExtra("nodeID", devices.get(0).getDevice_id());
                startActivity(intent);
            }
        });

        // Set an on click listener  on the refresh button to take the user to the LoadData activity
        // and refresh the data
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoadData.class);
                intent.putExtra("class", "MainActivity");
                startActivity(intent);
            }
        });
    }

    // Sets the chart settings and refreshes the data
    private void setChart(MyChart chart, String format, int labelCount, List<Entry> entries,
                          String labelText, int color) {
        chart.setSettings(format, labelCount);
        chart.setLds(entries, labelText);
        chart.setLegend(labelText, color);
        chart.drawCircles(false);
        chart.setLd();
        chart.setLineDataSetColor(color);
        chart.setAnimation(2000);
        chart.refresh();
    }

    // Displays the latest readings on the main activity
    private void setLatestReading(ArrayList<Device> devices, TextView nodeID, TextView nodeReading,
                                  TextView nodeReadingHumidity, TextView nodeReadingLight,
                                  TextView nodeReadingPressure) {
        nodeID.setText(devices.get(0).getDevice_id());
        String lastReading = devices.get(0).getPayloads().get(devices.get(0).getPayloads().size() - 1).getTemperature();
        String lastReadingHumidity = devices.get(0).getPayloads().get(devices.get(0).getPayloads().size() - 1).getHumidity();
        String lastReadingLight = devices.get(0).getPayloads().get(devices.get(0).getPayloads().size() - 1).getLuminostiy();
        String lastReadingPressure = devices.get(0).getPayloads().get(devices.get(0).getPayloads().size() - 1).getBarometric();
        nodeReading.setText(lastReading + (char) 0x00B0 + "C");
        nodeReadingHumidity.setText(lastReadingHumidity + "%");
        nodeReadingLight.setText(lastReadingLight + " Lux");
        nodeReadingPressure.setText(lastReadingPressure + "0" + " hPa");
    }
}
