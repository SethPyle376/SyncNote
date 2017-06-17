package com.pyle.syncnote;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.Log;

import java.net.Socket;

import static android.support.design.widget.BaseTransientBottomBar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;

/**
 * Created by Jason on 6/12/2017.
 */

public class NetworkCallback implements Client.ClientCallback {

    Context mainContext;

    public NetworkCallback(Context context) {
        mainContext = context;
    }

    @Override
    public void onMessage(String message) {
        Log.d("NODEJS", message);
        Snackbar snackBar = Snackbar.make(((Activity)mainContext).findViewById(R.id.myCoordinatorLayout), message, LENGTH_SHORT);
        snackBar.show();
    }

    @Override
    public void onConnect(Socket socket) {
        Log.d("NODEJS", "Connected");
    }

    @Override
    public void onDisconnect(Socket socket, String message) {
        Log.d("NODEJS", "Disconnected");
    }

    @Override
    public void onConnectError(Socket socket, String message) {
        Log.d("NODEJS", message);
    }
}
