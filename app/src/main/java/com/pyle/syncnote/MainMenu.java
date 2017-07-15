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
import android.text.Spannable;
import android.text.style.StyleSpan;
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

/**
 * This is our main class that gets everything ready. It creates our toolbar, nav drawer,
 * and floating action button and handles them.
 */
public class MainMenu extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ListView drawerList;
    private ArrayAdapter<String> adapter;
    private NetworkCallback callback;
    private EditText editor;
    private android.support.v4.app.FragmentManager fragManager;
    private Context myActivity;
    private FloatingActionButton fab;



    Client socket;


    /**
     * This sets everything up and gets it ready to go. Drawer, toolbar, and buttons are all
     * initialized. The network connection to the server is also initialized here. Finally a notepad
     * fragment is initialized and presented to the user.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragManager = getSupportFragmentManager();

        //Set view to text editing
        setContentView(R.layout.activity_text_editor);
        myActivity = this;

        //Set up toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View view) {
                                           final NoteFragment noteFragment = (NoteFragment) fragManager.findFragmentByTag("notepad");

                                           if (noteFragment != null) {
                                               final String[] noteTitle = new String[1];
                                               if (noteFragment.title.equals("default")) {
                                                   Log.d("nodejs", "entering alerter");
                                                   AlertDialog.Builder builder = new AlertDialog.Builder(myActivity);
                                                   builder.setTitle("Please enter a title for your note");
                                                   final EditText input = new EditText(myActivity);
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
                                                               fab.hide();
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
                                           }
                                       }
                                   });
        }



        callback = new NetworkCallback(this, getSupportFragmentManager(), toolbar, fab);
        socket = new Client("52.10.127.103", 4000, this);
        socket.setClientCallback(callback);
        socket.connect();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = new NavigationView(this);
        navigationView = (NavigationView) findViewById(R.id.nav_view);


        navigationView.setNavigationItemSelectedListener(this);


        toolbar.setTitle("New Note");
        Bundle noteBundle = new Bundle();
        noteBundle.putString("title", "default");
        NoteFragment newNote = new NoteFragment();
        newNote.setArguments(noteBundle);
        getSupportFragmentManager().beginTransaction().add(R.id.content_frame, newNote, "notepad").commit();

    }

    /**
     * This is a button callback function for the menu button, to open and close the drawer.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Inflates the toolbar menu
     * @param menu
     * @return bool Whether or not it was successful
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // This will get deleted and changed with the text editor UI later on
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * Callback functions for handling button presses to either upload or download
     * a note to the server.
     * @param item Button in question
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    /**
     * Another button callback handler, used for buttons in the navigation drawer.
     * @param item
     * @return
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.nav_new) {
            Bundle bundle = new Bundle();
            toolbar.setTitle("New Note");
            bundle.putString("title", "default");
            NoteFragment newNoteList = new NoteFragment();
            newNoteList.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, newNoteList, "notepad").commit();
        }
        else if (id == R.id.nav_navigate) {
            fragManager.beginTransaction()
                    .replace(R.id.content_frame
                            , new NoteListFragment(), "noteList")
                    .commit();
            fab.show();
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
        }  else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
