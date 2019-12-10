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

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.model.TableColumnDpWidthModel;
import de.codecrafters.tableview.model.TableColumnModel;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class PayloadTable extends AppCompatActivity{
    TableView<String[]> payloadTable;

    public PayloadTable() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payload_table);

        Button homeButton = (Button) findViewById(R.id.homeButton);
        Button refreshButton = (Button) findViewById(R.id.refreshButton);
        payloadTable = (TableView<String[]>) findViewById(R.id.payloadTable);
        TextView deviceID = (TextView) findViewById(R.id.deviceID);

        Intent intent = getIntent();
        final ArrayList<Device> devices = (ArrayList<Device>) Parcels.unwrap(intent.getExtras().getParcelable("devices"));
        ArrayList<Payload> payloads = devices.get(0).getPayloads();
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
            ArrayList<Payload> payloads = values[0];
            int size = payloads.size();
            String[][] data = new String[size][6];
            TableColumnWeightModel columnModel = new TableColumnWeightModel(6);
            columnModel.setColumnWeight(1, 2);
            String[] tableHeaders = {"device_id", "DateTime", (char) 0x00B0 + "C", "Humidity(%)", "hPa", "Lux"};
            for (int row = 0; row < size; row++) {
                Payload payload = payloads.get(row);
                data[row][0] = payload.getDevice_id();
                data[row][1] = payload.getTime_stamp();
                data[row][2] = payload.getTemperature();
                data[row][3] = payload.getHumidity();
                data[row][4] = payload.getBarometric() + "0";
                data[row][5] = payload.getLuminostiy();
            }
            payloadTable.setColumnModel(columnModel);
            payloadTable.setDataAdapter(new SimpleTableDataAdapter(PayloadTable.this, data));
            SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(PayloadTable.this, tableHeaders);
            simpleTableHeaderAdapter.setTextSize(12);
            payloadTable.setHeaderAdapter(simpleTableHeaderAdapter);
        }
    }
}
