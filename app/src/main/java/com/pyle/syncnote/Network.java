package com.pyle.syncnote;

import java.net.MalformedURLException;

import io.socket.IOAcknowledge;
import io.socket.IOCallback;
import io.socket.SocketIO;
import io.socket.SocketIOException;

/**
 * Created by Seth on 6/12/2017.
 */

public class Network implements IOCallback {
    private SocketIO socket;

    public void Network() throws Exception {
        socket = new SocketIO();
        try {
            socket.connect("http://52.10.127.103:8080/", this);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


}
