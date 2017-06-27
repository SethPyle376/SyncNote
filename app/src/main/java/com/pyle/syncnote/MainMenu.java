package com.pyle.syncnote;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static java.security.AccessController.getContext;

public class MainMenu extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ListView drawerList;
    private ArrayAdapter<String> adapter;
    private NetworkCallback callback;
    private EditText editor;
    private FragmentManager fragManager;
    private DrawerItemClickListener drawerListener;

    Client socket;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragManager = getSupportFragmentManager();
        //Set view to text editing
        setContentView(R.layout.activity_text_editor);
        String[] drawerItems = { "New Note", "Notes"};

        //Set up toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

        callback = new NetworkCallback(this, getSupportFragmentManager(), toolbar);
        socket = new Client("52.10.127.103", 4000, this);
        socket.setClientCallback(callback);
        socket.connect();

        //Grab drawer for later
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerItems);
        drawerList.setAdapter(adapter);
        drawerListener = new DrawerItemClickListener(getSupportFragmentManager(), drawer, socket);
        drawerList.setOnItemClickListener(drawerListener);



        Bundle noteBundle = new Bundle();
        noteBundle.putString("title", "default");
        NoteFragment newNoteList = new NoteFragment();
        newNoteList.setArguments(noteBundle);
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, newNoteList, "notepad").commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //When user presses menu at top left, this will open or close drawer
            case android.R.id.home:
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT);
                }
                else {
                    drawer.openDrawer(Gravity.LEFT);
                }

                return true;

            case R.id.action_menu:
                NoteFragment noteFrag = (NoteFragment)fragManager.findFragmentByTag("notepad");
                if (noteFrag != null) {
                    String title = noteFrag.title;
                    JSONObject json = new JSONObject();
                    try {
                        json.put("command", "pull");
                        json.put("target", title);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String sent = json.toString() + '\n';
                    try {
                        Log.d("nodejs", sent);
                        socket.send(sent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.action_up:
                final NoteFragment noteFragment = (NoteFragment)fragManager.findFragmentByTag("notepad");

                if (noteFragment != null) {
                    final String[] noteTitle = new String[1];

                    if (noteFragment.title.equals("default")) {
                        Log.d("nodejs", "entering alerter");
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setTitle("Please enter a title for your note");
                        final EditText input = new EditText(this);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        builder.setView(input);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                noteTitle[0] = input.getText().toString();
                                noteFragment.title = noteTitle[0];
                                toolbar.setTitle(noteFragment.title);
                                EditText tempEditor = (EditText) findViewById(R.id.sharedText);
                                JSONObject jsonNew = new JSONObject();
                                try {
                                    jsonNew.put("command", "push");
                                    jsonNew.put("target", noteFragment.title);
                                    jsonNew.put("data", tempEditor.getText());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                String sentNew = jsonNew.toString() + '\n';
                                try {
                                    socket.send(sentNew);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();

                    }

                    else {
                        EditText tempEditor = (EditText) findViewById(R.id.sharedText);
                        JSONObject jsonNew = new JSONObject();
                        try {
                            jsonNew.put("command", "push");
                            jsonNew.put("target", noteFragment.title);
                            jsonNew.put("data", tempEditor.getText());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String sentNew = jsonNew.toString() + '\n';
                        try {
                            socket.send(sentNew);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    /* Seth*/}
 // test commit
