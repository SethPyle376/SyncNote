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


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ListView drawerList;
    private ArrayAdapter<String> adapter;
    private NetworkCallback callback;
    private EditText editor;
    private android.support.v4.app.FragmentManager fragManager;


    Client socket;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragManager = getSupportFragmentManager();
        //Set view to text editing
        setContentView(R.layout.activity_text_editor);

        //May not be necessary anymore
        //String[] drawerItems = { "New Note", "Notes"};

        //Set up toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }



        callback = new NetworkCallback(this, getSupportFragmentManager(), toolbar);
        socket = new Client("52.10.127.103", 4000, this);
        socket.setClientCallback(callback);
        socket.connect();

        //Not sure if this goes here or at the end
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = new NavigationView(this);
        navigationView = (NavigationView) findViewById(R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(this);


        //May not be necessary either
        // Grab drawer for later
        //drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //drawerList = (ListView) findViewById(R.id.left_drawer);
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerItems);
        //drawerList.setAdapter(adapter);


        Bundle noteBundle = new Bundle();
        noteBundle.putString("title", "default");
        NoteFragment newNoteList = new NoteFragment();
        newNoteList.setArguments(noteBundle);
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, newNoteList, "notepad").commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This will get deleted and changed with the text editor UI later on
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //When user presses menu at top left, this will open or close drawer
            case R.id.action_settings:
                //if (drawer.isDrawerOpen(Gravity.LEFT)) {
                //   drawer.closeDrawer(Gravity.LEFT);
                //}
                //else {
                //    drawer.openDrawer(Gravity.LEFT);
                //}
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_new) {
            Bundle bundle = new Bundle();
            bundle.putString("title", "default");
            NoteFragment newNoteList = new NoteFragment();
            newNoteList.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame, newNoteList, "notepad").commit();
        } else if (id == R.id.nav_navigate) {
            fragManager.beginTransaction()
                    .replace(R.id.content_frame
                            , new NoteListFragment(), "noteList")
                    .commit();
            JSONObject json = new JSONObject();
            try {
                json.put("command", "list");
                json.put("data", "");
                json.put("target", "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String sent = json.toString() + '\n';
            try {
                socket.send(sent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
