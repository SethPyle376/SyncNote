package com.pyle.syncnote;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.IOException;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        NetworkCallback callback = new NetworkCallback();

        final Client socket = new Client("52.10.127.103", 8080);
        socket.setClientCallback(callback);
        socket.connect();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                socket.disconnect();
            }
        }, 10000);


    }
    /* Seth*/}
 // test commit
