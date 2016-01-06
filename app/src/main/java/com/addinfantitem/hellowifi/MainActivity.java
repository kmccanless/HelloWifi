package com.addinfantitem.hellowifi;

import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.util.Log;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import java.lang.StringBuilder;
import java.io.File;
import java.io.FileOutputStream;


public class MainActivity extends AppCompatActivity {
    ListView lv;
    Button button;
    WifiManager wifi;
    String wifis[];
    WifiScanReceiver wifiReciever;
   // Timer timer;
   // MyTimerTask myTimerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        lv = (ListView) findViewById(R.id.listView);

        wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
        wifiReciever = new WifiScanReceiver();

    }
    protected void onPause() {
        unregisterReceiver(wifiReciever);
        super.onPause();
    }

    protected void onResume() {
        registerReceiver(wifiReciever, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        addListenerOnButton();
        //timer = new Timer();
       // myTimerTask = new MyTimerTask();
       // timer.schedule(myTimerTask, 5000, 10000);
        super.onResume();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    public void addListenerOnButton() {

        button = (Button) findViewById(R.id.button);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                wifi.startScan();
            }

        });

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class WifiScanReceiver extends BroadcastReceiver{
        public void onReceive(Context c, Intent intent) {
            List<ScanResult> wifiScanList = wifi.getScanResults();
            //FileOutputStream outputStream;

            wifis = new String[wifiScanList.size()];
            StringBuilder sb = new StringBuilder();
            //byte[]  = new String();

            for(int i = 0; i < wifiScanList.size(); i++){
                //wifis[i] = ((wifiScanList.get(i)).toString());
                int rssi = wifiScanList.get(i).level;
                int frequency = wifiScanList.get(i).frequency;
                int level = wifiScanList.get(i).level;
                String ssid =  wifiScanList.get(i).SSID;
                String bssid = wifiScanList.get(i).BSSID;
                long time = wifiScanList.get(i).timestamp;
                int signal = wifi.calculateSignalLevel(rssi, 100);

                sb.append(ssid);
                sb.append(", ");
                sb.append(bssid);
                sb.append(", ");
                sb.append(Integer.toString(frequency));
                sb.append(", ");
                sb.append(Integer.toString(level));
                sb.append(", ");
                sb.append(Long.toString(time));
                sb.append(", ");
                sb.append(Integer.toString(signal));
                sb.append("\n");
                //wifis[i] = sb.toString();
                //File file = new File(c.getFilesDir(), ssid + time);
                //tempFile += wifis[i];

            }
            String filename = "wifi_signals";
            try {
                String timeStamp = new java.text.SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                wifis = sb.toString().split("\n");
                File file = new File(Environment.getExternalStorageDirectory(), filename + timeStamp + ".csv");
                Log.v("CreateFile", file.getAbsolutePath());
                //outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                OutputStream os = new FileOutputStream(file);
                os.write(sb.toString().getBytes());
                os.close();
                Log.v("CreateFile", "Wrote file " + filename);
            } catch (Exception e) {
                Log.e("CreateFile", e.getStackTrace().toString());
            }
            lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,wifis));

        }
    }
    class MyTimerTask extends TimerTask {

        @Override
        public void run() {


            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    //show the toast
                    int duration = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(getApplicationContext(), wifis[0].toString(), duration);
                    toast.show();
                }});
        }

    }
}
