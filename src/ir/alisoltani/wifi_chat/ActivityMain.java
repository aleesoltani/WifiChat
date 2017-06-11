package ir.alisoltani.wifi_chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class ActivityMain extends Activity {

    private Button btnServer;
    private Button btnClient;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnServer = (Button) findViewById(R.id.btnServer);
        btnClient = (Button) findViewById(R.id.btnClient);

        btnServer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ActivityMain.this, ActivityServer.class);
                ActivityMain.this.startActivity(intent);
            }
        });

        btnClient.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ActivityMain.this, ActivityClient.class);
                ActivityMain.this.startActivity(intent);
            }
        });
    }
}