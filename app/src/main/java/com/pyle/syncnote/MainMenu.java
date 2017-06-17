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
import android.widget.ListView;

import java.io.IOException;

public class MainMenu extends AppCompatActivity {
    private Toolbar toolbar;
    private DrawerLayout drawer;
    private ListView drawerList;
    private ArrayAdapter<String> adapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set view to text editting
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



        //Setup connection to server, send test message
        NetworkCallback callback = new NetworkCallback();
        final Client socket = new Client("52.10.127.103", 3000);
        socket.setClientCallback(callback);
        socket.connect();

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
        }
        return super.onOptionsItemSelected(item);
    }
    /* Seth*/}
 // test commit
