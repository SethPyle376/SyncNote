package com.pyle.syncnote;

import android.app.Activity;



import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;


/**
 * Created by Jason on 6/12/2017.
 */

public class Client {
    private Socket socket;
    private OutputStream socketOutput;
    private BufferedReader socketInput;

    private String ip;
    private int port;
    private ClientCallback listener=null;
    private Activity activity;
    private Boolean connected;

    public Client(String ip, int port, Activity activity){
        this.ip=ip;
        this.port=port;
        this.activity = activity;
    }

    public void connect(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                socket = new Socket();
                InetSocketAddress socketAddress = new InetSocketAddress(ip, port);
                try {
                    socket.connect(socketAddress);
                    socketOutput = socket.getOutputStream();
                    socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    new ReceiveThread().start();
                    connected = true;

                    if(listener!=null)
                        listener.onConnect(socket);
                } catch (IOException e) {
                    if(listener!=null)
                        listener.onConnectError(socket, e.getMessage());
                }
            }
        }).start();
    }

    public void disconnect(){
        try {
            socket.close();
        } catch (IOException e) {
            if(listener!=null)
                listener.onDisconnect(socket, e.getMessage());
        }
    }

    public void send(String message) throws IOException {
        try {
            socketOutput.write(message.getBytes());
        } catch (IOException e) {
            if(listener!=null) {
                listener.onDisconnect(socket, e.getMessage());
                connected = false;
            }
        }
    }

    public Boolean isConnected() throws IOException {
        return connected;
    }

    private class ReceiveThread extends Thread implements Runnable{
        public void run(){
            String message;
            try {
                while((message = socketInput.readLine()) != null) {   // each line must end with a \n to be received
                    if(listener!=null) {
                        final String finalMessage = message;
                        activity.runOnUiThread(new Runnable() {
                            public void run()
                            {
                                try {
                                    listener.onMessage(finalMessage);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                                               });
                    }
                }
            } catch (IOException e) {
                if(listener!=null)
                    listener.onDisconnect(socket, e.getMessage());
            }
        }
    }

    public void setClientCallback(ClientCallback listener){
        this.listener=listener;
    }

    public void removeClientCallback(){
        this.listener=null;
    }

    public interface ClientCallback {
        void onMessage(String message) throws JSONException;
        void onConnect(Socket socket);
        void onDisconnect(Socket socket, String message);
        void onConnectError(Socket socket, String message);
    }
}
