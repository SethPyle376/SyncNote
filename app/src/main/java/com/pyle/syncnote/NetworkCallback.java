package com.pyle.syncnote;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.zip.Inflater;

import static android.support.design.widget.BaseTransientBottomBar.LENGTH_LONG;
import static android.support.design.widget.Snackbar.LENGTH_SHORT;

/**
 * Created by Jason on 6/12/2017.
 */

public class NetworkCallback implements Client.ClientCallback {

    Context mainContext;
    Activity mainActivity;
    FragmentManager fragMan;
    ArrayList<String> list;
    String designatedNote;
    Toolbar mainToolbar;
    FloatingActionButton fab;
    Client socket;


    public NetworkCallback(Context context, FragmentManager fragMan, Toolbar mainToolbar, FloatingActionButton fab, MainMenu mainMenu) {
        mainContext = context;
        mainActivity = (Activity) context;
        this.fragMan = fragMan;
        list = new ArrayList<String>();
        this.mainToolbar = mainToolbar;
        this.fab = fab;
        this.socket = mainMenu.socket;
    }

    public String getDesignatedNote() {
        return designatedNote;
    }

    @Override
    public void onMessage(String message) throws JSONException {
        Log.d("NODEJS", message);
        JSONObject jsonObject = new JSONObject(message);
        String command = jsonObject.getString("command");
        Log.d("nodejs", command);

        if (command.equals("list")) {
            Log.d("nodejs", "Got through list");
            if (fragMan.findFragmentByTag("noteList") != null) {
                if (!list.contains(jsonObject.getString("data"))) {
                    list.add(jsonObject.getString("data"));
                }
                Log.d("nodejs", "Added to list");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(mainActivity, android.R.layout.simple_list_item_1, list);
                final ListView notesList = (ListView) mainActivity.findViewById(R.id.notelist);
                notesList.setAdapter(adapter);
                notesList.setOnItemClickListener(new ListView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        fab.hide();
                        String text = notesList.getItemAtPosition(position).toString().trim();
                        Bundle noteBundle = new Bundle();
                        noteBundle.putString("title", text);
                        Fragment noteFrag = new NoteFragment();
                        noteFrag.setArguments(noteBundle);
                        mainToolbar.setTitle(text);
                        fragMan.beginTransaction()
                                .replace(R.id.content_frame, noteFrag, "notepad")
                                .commit();

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                                final NoteFragment noteFragment = (NoteFragment)fragMan.findFragmentByTag("notepad");
                                final EditText editor = (EditText) mainActivity.findViewById(R.id.sharedText);
                                editor.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before,
                                                              int count) {
                                        Log.d("nodejs", "text changed");

                                        if (count == 1) {
                                            JSONObject jsonNew = new JSONObject();

                                            try {
                                                jsonNew.put("command", "push");
                                                jsonNew.put("target", noteFragment.title);
                                                jsonNew.put("data", editor.getText());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            String sentNew = jsonNew.toString() + '\n';
                                            try {
                                                if (editor.hasFocus())
                                                    socket.send(sentNew);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }



                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {

                                    }

                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count,
                                                                  int after) {
                                        // TODO Auto-generated method stub

                                    }
                                });

                                JSONObject jsonNew = new JSONObject();
                                try {
                                    jsonNew.put("command", "pull");
                                    jsonNew.put("target", noteFragment.title);
                                    String sentNew = jsonNew.toString() + '\n';
                                    socket.send(sentNew);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        }, 1000);






                    }});
            }
        }
        else if (command.equals("update")) {
            EditText editor = (EditText) mainActivity.findViewById(R.id.sharedText);
            if (editor != null) {
                editor.setText(jsonObject.getString("data"));
            }
        }
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
