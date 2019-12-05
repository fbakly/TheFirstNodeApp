package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.parceler.Parcel;
import org.parceler.ParcelConstructor;
import org.parceler.ParcelConverter;
import org.parceler.ParcelPropertyConverter;
import org.parceler.Parcels;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

public class PayloadTable extends AppCompatActivity{
    TableLayout payloadTable;

    public PayloadTable() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payload_table);

        Button homeButton = (Button) findViewById(R.id.homeButton);
        Button refreshButton = (Button) findViewById(R.id.refreshButton);
        payloadTable = (TableLayout) findViewById(R.id.payloadTable);
        TextView deviceID = (TextView) findViewById(R.id.deviceID);

        Intent intent = getIntent();
        ArrayList<Payload> payloads = (ArrayList<Payload>) Parcels.unwrap(intent.getExtras().getParcelable("payloads"));
        final ArrayList<Device> devices = (ArrayList<Device>) Parcels.unwrap(intent.getExtras().getParcelable("devices"));
        final String nodeID = intent.getExtras().getString("nodeID");

        deviceID.setText(nodeID);

        new TableCreator().execute(payloads, payloads, null);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayloadTable.this, MainActivity.class);
                intent.putExtra("devices", Parcels.wrap(devices));
                startActivity(intent);
            }
        });

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PayloadTable.this, LoadData.class);
                intent.putExtra("nodeID", nodeID);
                intent.putExtra("class", "PayloadTable");
                startActivity(intent);
            }
        });
    }

    class TableCreator extends AsyncTask<ArrayList<Payload>, ArrayList<Payload>, Void> {
        @Override
        protected Void doInBackground(ArrayList<Payload>... values) {
            publishProgress(values);
            return null;
        }

        @Override
        protected void onProgressUpdate(ArrayList<Payload>... values) {
            super.onProgressUpdate(values);
//            ArrayList<Payload> payloads = values[0];
//            for (Payload payload : payloads) {
//                TableRow tr = new TableRow(PayloadTable.this);
//                TextView device_id = new TextView(PayloadTable.this);
//                TextView date = new TextView(PayloadTable.this);
//                TextView time = new TextView(PayloadTable.this);
//                TextView temp = new TextView(PayloadTable.this);
//                TextView humidity = new TextView(PayloadTable.this);
//                TextView pressure = new TextView(PayloadTable.this);
//                TextView light = new TextView(PayloadTable.this);
//
//                device_id.setText(payload.getDevice_id());
//                date.setText(payload.getDate_stamp());
//                time.setText(payload.getTime_stamp());
//                temp.setText(payload.getTemperature());
//                humidity.setText(payload.getHumidity());
//                pressure.setText(payload.getBarometric());
//                light.setText(payload.getLuminostiy());
//
//                device_id.setPadding(10, 0, 10, 0);
//                date.setPadding(10, 0, 10, 0);
//                time.setPadding(10, 0, 10, 0);
//                temp.setPadding(10, 0, 10, 0);
//                humidity.setPadding(10, 0, 10, 0);
//                pressure.setPadding(10, 0, 10, 0);
//                light.setPadding(10, 0, 10, 0);
//
//                tr.addView(device_id);
//                tr.addView(date);
//                tr.addView(time);
//                tr.addView(temp);
//                tr.addView(humidity);
//                tr.addView(pressure);
//                tr.addView(light);
//                payloadTable.addView(tr);
//            }
        }
    }
}
