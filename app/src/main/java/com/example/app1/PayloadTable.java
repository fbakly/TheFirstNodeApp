package com.example.app1;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.parceler.Parcels;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.model.TableColumnWeightModel;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class PayloadTable extends AppCompatActivity{
    SortableTableView<String[]> payloadTable;

    public PayloadTable() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payload_table);

        Button homeButton = (Button) findViewById(R.id.homeButton);
        payloadTable = (SortableTableView<String[]>) findViewById(R.id.payloadTable);
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
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("devices", Parcels.wrap(devices));
                startActivity(intent);
            }
        });
    }


    private static class  deviceComparator implements Comparator<String[]> {
        @Override
        public int compare(String[] o1, String[] o2) {
            return o1[0].compareTo(o2[0]);
        }
    }

    private static class timeStampComparator implements Comparator<String[]> {
        @Override
        public int compare(String[] o1, String[] o2) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-dd HH:mm:ss");
            try {
                Date date1 = sdf.parse(o1[1]);
                Date date2 = sdf.parse(o2[1]);
                return date1.compareTo(date2);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        }
    }

    private static class temperatureComparator implements Comparator<String[]> {
        @Override
        public int compare(String[] o1, String[] o2) {
            return Float.valueOf(o1[2]).compareTo(Float.valueOf(o2[2]));
        }
    }

    private static class humidityComparator implements Comparator<String[]> {
        @Override
        public int compare(String[] o1, String[] o2) {
            return Float.valueOf(o1[3]).compareTo(Float.valueOf(o2[3]));
        }
    }

    private static class pressureComparator implements Comparator<String[]> {
        @Override
        public int compare(String[] o1, String[] o2) {
            return Float.valueOf(o1[4]).compareTo(Float.valueOf(o2[4]));
        }
    }

    private static class lightComparator implements Comparator<String[]> {
        @Override
        public int compare(String[] o1, String[] o2) {
            return Float.valueOf(o1[5]).compareTo(Float.valueOf(o2[5]));
        }
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
            for (int row = size - 1, dataRow = 0; row >= 0; row--, dataRow++) {
                Payload payload = payloads.get(row);
                data[dataRow][0] = payload.getDevice_id();
                data[dataRow][1] = payload.getTime_stamp();
                data[dataRow][2] = payload.getTemperature();
                data[dataRow][3] = payload.getHumidity();
                data[dataRow][4] = payload.getBarometric() + "0";
                data[dataRow][5] = payload.getLuminostiy();
            }
            payloadTable.setColumnModel(columnModel);
            payloadTable.setDataAdapter(new SimpleTableDataAdapter(PayloadTable.this, data));
            SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(PayloadTable.this, tableHeaders);
            simpleTableHeaderAdapter.setTextSize(12);
            payloadTable.setHeaderAdapter(simpleTableHeaderAdapter);
            payloadTable.setColumnComparator(0, new deviceComparator());
            payloadTable.setColumnComparator(1, new timeStampComparator());
            payloadTable.setColumnComparator(2, new temperatureComparator());
            payloadTable.setColumnComparator(3, new humidityComparator());
            payloadTable.setColumnComparator(4, new pressureComparator());
            payloadTable.setColumnComparator(5, new lightComparator());
        }
    }
}
