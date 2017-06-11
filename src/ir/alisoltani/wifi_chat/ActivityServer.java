package ir.alisoltani.wifi_chat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class ActivityServer extends Activity {

    private Button           btnServerToggle;
    private Button           btnSend;
    private TextView         txtStateServer;
    private TextView         txtMessage;
    private EditText         edtMessageBox;

    private DataOutputStream outputStream;
    private BufferedReader   inputStream;
    private Socket           socket;
    private Handler          handler = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        btnServerToggle = (Button) findViewById(R.id.btnServerToggle);
        btnSend = (Button) findViewById(R.id.btnSend);
        txtStateServer = (TextView) findViewById(R.id.txtStateServer);
        txtMessage = (TextView) findViewById(R.id.txtMessage);
        edtMessageBox = (EditText) findViewById(R.id.edtMessageBox);

        btnServerToggle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                try {
                    toggle();
                }
                catch (Exception exception) {

                }
            }
        });

        btnSend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                sendMessage();
            }
        });
    }


    protected void toggle() {
        if ( !HotspotManager.isApOn(G.context)) {
            HotspotManager.configApState(ActivityServer.this, "Server");
            createSocket();
        } else {
            HotspotManager.configApState(ActivityServer.this, "Server");
        }
    }


    private void createSocket() {
        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(5000);
                    log("Waiting for client...", "");
                    socket = serverSocket.accept();
                    log("A new client Connected!", "");
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    outputStream = new DataOutputStream(socket.getOutputStream());
                    inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
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


    private void log(final String state, final String message) {
        handler.post(new Runnable() {

            @Override
            public void run() {
                txtStateServer.setText(state);
                txtMessage.setText(message);
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (socket != null) {
                socket.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

}
