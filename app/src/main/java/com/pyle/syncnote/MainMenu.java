package com.pyle.syncnote;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainMenu extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ListView drawerList;
    private ArrayAdapter<String> adapter;
    private NetworkCallback callback;
    private EditText editor;

    Client socket;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set view to text editing
        setContentView(R.layout.activity_text_editor);
        String[] drawerItems = { "Editor", "Notes"};

        //Grab drawer for later
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, drawerItems);
        drawerList.setAdapter(adapter);

        //Set up toolbar
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_menu);
        setSupportActionBar(toolbar);

        //Set up editor for writing/reading
        editor = (EditText) findViewById(R.id.sharedText);

        //Setup connection to server, send test message
        callback = new NetworkCallback(this);
        socket = new Client("52.10.127.103", 3000, this);
        socket.setClientCallback(callback);
        socket.connect();

        //Set up connection checker to reconnect if connection is lost
        /*new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    socket.send("Ping");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 15000);*/


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
                JSONObject json = new JSONObject();
                try {
                    json.put("command", "pull");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String sent = json.toString() + '\n';
                try {
                    socket.send(sent);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.action_up:
                JSONObject jsonNew = new JSONObject();
                try {
                    jsonNew.put("push", editor.getText());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String sentNew = jsonNew.toString() + '\n';
                try {
                    socket.send(sentNew);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    /* Seth*/}
 // test commit
