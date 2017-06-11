package ir.alisoltani.wifi_chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class ActivityClient extends Activity {

    private Button                btnClientToggle;
    private Button                btnSend;
    private TextView              txtStateClinet;
    private TextView              txtMessage;
    private EditText              edtMessageBox;
    private String                ssid;

    private WifiManager           wifi;
    private ArrayList<ScanResult> results;
    private DataOutputStream      outputStream;
    private BufferedReader        inputStream;
    private Socket                socketToServer;
    private Handler               handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        btnClientToggle = (Button) findViewById(R.id.btnClientToggle);
        btnSend = (Button) findViewById(R.id.btnSend);
        txtStateClinet = (TextView) findViewById(R.id.txtStateClinet);
        txtMessage = (TextView) findViewById(R.id.txtMessage);
        edtMessageBox = (EditText) findViewById(R.id.edtMessageBox);
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        results = new ArrayList<ScanResult>();

        setWifiOn();

        btnClientToggle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                connectHotspot();
                createSocket();
            }
        });

        btnSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sendMessage();
            }
        });

        reciveMessage();
    }


    private void setWifiOn() {
        if ( !wifi.isWifiEnabled()) {
            if (wifi.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
                wifi.setWifiEnabled(true);
            }
        }
    }


    protected void scanHotspot() {
        // Register a broadcast receiver that listens for scan results.
        registerReceiver(new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                results = (ArrayList<ScanResult>) wifi.getScanResults();
                ScanResult bestSignal = null;
                for (ScanResult result: results) {
                    if (bestSignal == null ||
                            WifiManager.compareSignalLevel(bestSignal.level, result.level) < 0)
                        bestSignal = result;
                }
                //String connSummary = results.size() + " networks found. " + bestSignal.SSID + " is the strongest.";
                //Toast.makeText(G.context, "ss", Toast.LENGTH_SHORT).show();
                //show();
            }
        }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        // Initiate a scan.
        wifi.startScan();
    }


    protected void connectHotspot() {
        ssid = "Server";
        String pass = "";
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", pass);

        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        //remember id
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }


    private void createSocket() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                log("Connecting...", "");
                try {
                    for (int i = 10; i >= 0; i--) {
                        Thread.sleep(1000);
                        log("Second...  " + i, "");
                    }
                }
                catch (InterruptedException e1) {
                    e1.printStackTrace();
                }

                String range = "192.168.43.";
                for (int i = 0; i <= 255; i++) {
                    String ip = range + i;
                    log(ip, "");
                    try {
                        //log("Try IP: " + ip);
                        socketToServer = new Socket();
                        socketToServer.connect(new InetSocketAddress(ip, 5000), 1000);

                        outputStream = new DataOutputStream(socketToServer.getOutputStream());
                        inputStream = new BufferedReader(new InputStreamReader(socketToServer.getInputStream()));
                        log("Connected!", "");
                        while (true) {
                            try {
                                String message = inputStream.readLine();
                                if (message != null) {
                                    log("Online", message);
                                }
                            }
                            catch (IOException e) {}
                        }
                    }
                    catch (Exception e) {}
                }
                return;
            }
        });
        thread.start();
    }


    private void sendMessage() {
        String s = edtMessageBox.getText().toString() + "\n";
        try {
            outputStream.write(s.getBytes());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void reciveMessage() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

            }
        });
        thread.start();
    }


    private void log(final String state, final String message) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                txtStateClinet.setText(state);
                txtMessage.setText(message);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (socketToServer != null) {
                socketToServer.close();
            }
            if (wifi.isWifiEnabled()) {
                wifi.setWifiEnabled(false);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
