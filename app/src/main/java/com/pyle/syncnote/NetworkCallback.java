package com.pyle.syncnote;

import android.util.Log;

import java.net.Socket;

/**
 * Created by Jason on 6/12/2017.
 */

public class NetworkCallback implements Client.ClientCallback {

    @Override
    public void onMessage(String message) {
        Log.d("NODEJS", message);
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
