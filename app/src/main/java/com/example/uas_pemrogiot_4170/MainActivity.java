package com.example.uas_pemrogiot_4170;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MqttCallback {

    MqttClient client = null;
    String btnState;

    ArrayList<LineDataSet> flowDataSet;

    LineChart bc;
    LineChart ldrLC, soilLC, flowLC, humLC, tempLC;
    Button on_btn, off_btn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectToMQTTBroker();

        Log.d("MQTT", "subscribed");

//        bc = findViewById(R.id.myChart);
        flowLC = findViewById(R.id.flowLC);
        soilLC = findViewById(R.id.soilLC);
        ldrLC = findViewById(R.id.ldrLC);
        tempLC = findViewById(R.id.tempLC);
        humLC = findViewById(R.id.humLC);

        on_btn = findViewById(R.id.on_btn);
        off_btn = findViewById(R.id.off_btn);

//        LineData data = new LineData(getLabel(10), getDummyDataSet());
//        bc.setData(data);
//        bc.animateXY(4000, 4000);
//        bc.invalidate();

        LineData flowData = new LineData(getLabel(10));
        flowLC.setData(flowData);
        flowLC.animateXY(4000, 4000);
        flowLC.invalidate();
        /* ini udh ada flowData, asumsikan klo mau update data, yang diupdate yg flowData terus
        flowLC.notifyDataChanged();
        flowLC.notifyDataChanged();
         */

        LineData soilData = new LineData(getLabel(10));
        soilLC.setData(soilData);
        soilLC.animateXY(4000, 4000);
        soilLC.invalidate();

        LineData ldrData = new LineData(getLabel(10));
        ldrLC.setData(ldrData);
        ldrLC.animateXY(4000, 4000);
        ldrLC.invalidate();

        LineData humData = new LineData(getLabel(10));
        humLC.setData(humData);
        humLC.animateXY(4000, 4000);
        humLC.invalidate();

        LineData tempData = new LineData(getLabel(10));
        tempLC.setData(tempData);
        tempLC.animateXY(4000, 4000);
        tempLC.invalidate();

        /*
        LineData data = lineChart.getData();
        ILineDataSet set = data.getDataSetByIndex(0);
        data.addEntry(new Entry(set.getEntryCount(),iv),0);
        data.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.setVisibleXRangeMaximum(10);
        lineChart.moveViewToX(data.getEntryCount());
         */

        on_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnState = "1";
                off_btn.setBackgroundColor(R.color.purple_700);
                on_btn.setBackgroundColor(Color.rgb(36, 120, 34));
                publishMessage(btnState);
            }
        });

        off_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnState = "0";
                on_btn.setBackgroundColor(R.color.purple_700);
                off_btn.setBackgroundColor(Color.rgb(135, 35, 37));
                publishMessage(btnState);

            }
        });
    }

    private void connectToMQTTBroker() {
        try {
            client = new MqttClient("tcp://192.168.41.70:1883", "asdasd", new MemoryPersistence());
            client.setCallback(this);
            client.connect();

            // Re-subscribe to topics after reconnection if needed
            client.subscribe("4170/flow");
            client.subscribe("4170/soil");
            client.subscribe("4170/ldr");
            client.subscribe("4170/dht11/temp");
            client.subscribe("4170/dht11/hum");

            Log.d("MQTT", "Connected to MQTT broker");
        } catch (MqttException e) {
            e.printStackTrace();
            Log.d("MQTT", "Connection to MQTT broker failed");
        }
    }

    private ArrayList getLabel(int n) {
        ArrayList xLabel = new ArrayList();
        for(int i = 0; i < n; i++) {
            xLabel.add("n: " + Integer.toString(i + 1));
        }
        return xLabel;
    }

    public void publishMessage(String payload){
        try {
            if (!client.isConnected()) {
                client.connect();
            }
            MqttMessage message = new MqttMessage();
            message.setPayload(payload.getBytes());
            message.setQos(0);
            message.setRetained(false);
            client.publish("btnState", message);
        } catch (Exception e){
            Log.d("Mqtt", e.toString());
            e.printStackTrace();
        }
    }

//    private ArrayList getDummyDataSet() {
//        ArrayList dataset = null; //buat inisialisasi
//
//        ArrayList valueset1 = new ArrayList();
//        ArrayList valueset2 = new ArrayList();
//
//        int val;
//        for (int i = 0; i < 10; i++) {
//            val = (int) (Math.random() * 200 - 30);
//            valueset1.add(new Entry(val, i));
//            val = (int) (Math.random() * 200 - 30);
//            valueset2.add(new Entry(val, i));
//        }
//
//        LineDataSet bds1 = new LineDataSet(valueset1, "Data Set 1");
//        LineDataSet bds2 = new LineDataSet(valueset2, "Data Set 2");
//        bds1.setColor(Color.rgb(0, 200, 0));
//        bds2.setColor(Color.rgb(0, 0, 150));
//
//        dataset = new ArrayList();
//        dataset.add(bds1);
//        dataset.add(bds2);
//        return dataset;
//    }

    /*
    private ArrayList getSensorDataSet(float val, LineData sensorData) {
        ArrayList dataset = null;

        if (sensorData == null) {
            ArrayList valueset = new ArrayList();

            valueset.add(new Entry(val, 0));

            LineDataSet dataSet = new LineDataSet(valueset, "Data Set");
            dataSet.setColor(Color.rgb(0, 200, 0));

            dataset = new ArrayList();
            dataset.add(dataSet);
            Log.d("MQTT", "sensorData == null");
        } else {
            LineDataSet dataSet = (LineDataSet) sensorData.getDataSetByIndex(0);
            Entry newEntry = new Entry(val, dataSet.getEntryCount());
            dataSet.addEntry(newEntry);
            sensorData.notifyDataChanged();
            Log.d("MQTT", "sensorData != null");
        }
        return dataset;
    }
*/
    @Override
    public void connectionLost(Throwable cause) {
        Log.d("MQTT", "Connection lost: " + cause.getMessage());
        // Handle reconnection logic here
        connectToMQTTBroker();
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        if (topic.equals("4170/flow")) {
            // Update flow line chart with the received data
            // ...
            Log.d("MQTT", "topic: " + topic);
            Log.d("MQTT", "message: " + message);

            float value = Float.parseFloat(String.valueOf(message));
            updateFlowLineChart(value, flowLC);

        } else if (topic.equals("4170/soil")) {
            // Update soil line chart with the received data
            // ...
            Log.d("MQTT", "topic: " + topic);
            Log.d("MQTT", "message: " + message);

            float value = Float.parseFloat(String.valueOf(message));
            updateSoilLineChart(value, soilLC);

        } else if (topic.equals("4170/ldr")) {
            // Update ldr line chart with the received data
            // ...
            Log.d("MQTT", "topic: " + topic);
            Log.d("MQTT", "message: " + message);

            float value = Float.parseFloat(String.valueOf(message));
            updateLdrLineChart(value, ldrLC);

        } else if (topic.equals("4170/dht11/temp")) {
            // Update temperature line chart with the received data
            // ...
            Log.d("MQTT", "topic: " + topic);
            Log.d("MQTT", "message: " + message);

            float value = Float.parseFloat(String.valueOf(message));
            updateTempLineChart(value, tempLC);

        } else if (topic.equals("4170/dht11/hum")) {
            // Update humidity line chart with the received data
            // ...
            Log.d("MQTT", "topic: " + topic);
            Log.d("MQTT", "message: " + message);

            float value = Float.parseFloat(String.valueOf(message));
            updateHumLineChart(value, humLC);
        }
    }

    private void updateFlowLineChart(float value, LineChart lc) {
        LineData lineData = lc.getData();

        if (lineData != null) {
            ILineDataSet dataSet = lineData.getDataSetByIndex(0);

            if (dataSet == null) {
                dataSet = createFlowDataSet();
                lineData.addDataSet(dataSet);
            }

            int entryCount = dataSet.getEntryCount();

            if (entryCount >= 10) {
                //dataSet.removeEntry(0);
                for (int i = 0; i < 10; i++) {
                    dataSet.removeEntry(i);
                }

                Entry newEntry = new Entry(value, 0);
                dataSet.addEntry(newEntry);
            } else {
                Entry newEntry = new Entry(value, entryCount);
                dataSet.addEntry(newEntry);
            }

            lineData.notifyDataChanged();
            lc.notifyDataSetChanged();
            lc.setVisibleXRangeMaximum(10);
            lc.moveViewToX(-1);
        }
    }

    private void updateSoilLineChart(float value, LineChart lc) {
        LineData lineData = lc.getData();

        if (lineData != null) {
            ILineDataSet dataSet = lineData.getDataSetByIndex(0);

            if (dataSet == null) {
                dataSet = createSoilDataSet();
                lineData.addDataSet(dataSet);
            }

            int entryCount = dataSet.getEntryCount();

            if (entryCount >= 10) {
                //dataSet.removeEntry(0);
                for (int i = 0; i < 10; i++) {
                    dataSet.removeEntry(i);
                }

                Entry newEntry = new Entry(value, 0);
                dataSet.addEntry(newEntry);
            } else {
                Entry newEntry = new Entry(value, entryCount);
                dataSet.addEntry(newEntry);
            }

            lineData.notifyDataChanged();
            lc.notifyDataSetChanged();
            lc.setVisibleXRangeMaximum(10);
            lc.moveViewToX(-1);
        }
    }

    private void updateLdrLineChart(float value, LineChart lc) {
        LineData lineData = lc.getData();

        if (lineData != null) {
            ILineDataSet dataSet = lineData.getDataSetByIndex(0);

            if (dataSet == null) {
                dataSet = createLdrDataSet();
                lineData.addDataSet(dataSet);
            }

            int entryCount = dataSet.getEntryCount();

            if (entryCount >= 10) {
                //dataSet.removeEntry(0);
                for (int i = 0; i < 10; i++) {
                    dataSet.removeEntry(i);
                }

                Entry newEntry = new Entry(value, 0);
                dataSet.addEntry(newEntry);
            } else {
                Entry newEntry = new Entry(value, entryCount);
                dataSet.addEntry(newEntry);
            }

            lineData.notifyDataChanged();
            lc.notifyDataSetChanged();
            lc.setVisibleXRangeMaximum(10);
            lc.moveViewToX(-1);
        }
    }

    private void updateHumLineChart(float value, LineChart lc) {
        LineData lineData = lc.getData();

        if (lineData != null) {
            ILineDataSet dataSet = lineData.getDataSetByIndex(0);

            if (dataSet == null) {
                dataSet = createHumDataSet();
                lineData.addDataSet(dataSet);
            }

            int entryCount = dataSet.getEntryCount();

            if (entryCount >= 10) {
                //dataSet.removeEntry(0);
                for (int i = 0; i < 10; i++) {
                    dataSet.removeEntry(i);
                }

                Entry newEntry = new Entry(value, 0);
                dataSet.addEntry(newEntry);
            } else {
                Entry newEntry = new Entry(value, entryCount);
                dataSet.addEntry(newEntry);
            }

            lineData.notifyDataChanged();
            lc.notifyDataSetChanged();
            lc.setVisibleXRangeMaximum(10);
            lc.moveViewToX(-1);
        }
    }

    private void updateTempLineChart(float value, LineChart lc) {
        LineData lineData = lc.getData();

        if (lineData != null) {
            ILineDataSet dataSet = lineData.getDataSetByIndex(0);

            if (dataSet == null) {
                dataSet = createTempDataSet();
                lineData.addDataSet(dataSet);
            }

            int entryCount = dataSet.getEntryCount();

            if (entryCount >= 10) {
                //dataSet.removeEntry(0);
                for (int i = 0; i < 10; i++) {
                    dataSet.removeEntry(i);
                }

                Entry newEntry = new Entry(value, 0);
                dataSet.addEntry(newEntry);
            } else {
                Entry newEntry = new Entry(value, entryCount);
                dataSet.addEntry(newEntry);
            }

            lineData.notifyDataChanged();
            lc.notifyDataSetChanged();
            lc.setVisibleXRangeMaximum(10);
            lc.moveViewToX(-1);
        }
    }

    private LineDataSet createFlowDataSet() {
        LineDataSet dataSet = new LineDataSet(null, "Flow Data");
        dataSet.setColor(Color.rgb(255, 149, 0));
        dataSet.setCircleColor(Color.rgb(255, 149, 0));
        dataSet.setDrawValues(false);
        return dataSet;
    }

    private LineDataSet createSoilDataSet() {
        LineDataSet dataSet = new LineDataSet(null, "Soil Data");
        dataSet.setColor(Color.RED);
        dataSet.setCircleColor(Color.RED);
        dataSet.setDrawValues(false);
        return dataSet;
    }

    private LineDataSet createLdrDataSet() {
        LineDataSet dataSet = new LineDataSet(null, "LDR Data");
        dataSet.setColor(Color.BLUE);
        dataSet.setCircleColor(Color.BLUE);
        dataSet.setDrawValues(false);
        return dataSet;
    }

    private LineDataSet createHumDataSet() {
        LineDataSet dataSet = new LineDataSet(null, "Humidity Data");
        dataSet.setColor(Color.rgb(22, 79, 13));
        dataSet.setCircleColor(Color.rgb(22, 79, 13));
        dataSet.setDrawValues(false);
        return dataSet;
    }

    private LineDataSet createTempDataSet() {
        LineDataSet dataSet = new LineDataSet(null, "Temperature Data");
        dataSet.setColor(Color.MAGENTA);
        dataSet.setCircleColor(Color.MAGENTA);
        dataSet.setDrawValues(false);
        return dataSet;
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
