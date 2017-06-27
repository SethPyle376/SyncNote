package com.pyle.syncnote;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Jason on 6/26/2017.
 */

public class DrawerItemClickListener implements ListView.OnItemClickListener {
    FragmentManager fragMan;
    DrawerLayout drawer;
    Client socket;


    public DrawerItemClickListener(FragmentManager fragMan, DrawerLayout drawer, Client socket) {
        this.fragMan = fragMan;
        this.drawer = drawer;
        this.socket = socket;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectItem(position);
    }


    private void selectItem(int position) {
        if (position == 0)
        {
            Fragment noteFrag = new NoteFragment();
            if (fragMan.findFragmentByTag("notepad") != null && fragMan.findFragmentByTag("notepad").isVisible()) {
                drawer.closeDrawer(Gravity.LEFT);
                return;
            }
            fragMan.beginTransaction()
                    .replace(R.id.content_frame, noteFrag, "notepad")
                    .commit();
            drawer.closeDrawer(Gravity.LEFT);
        }
        else if (position == 1)
        {
            if (fragMan.findFragmentByTag("noteList") != null && fragMan.findFragmentByTag("noteList").isVisible()) {
                drawer.closeDrawer(Gravity.LEFT);
                return;
            }

            NoteListFragment listFrag = new NoteListFragment();
            fragMan.beginTransaction()
                    .replace(R.id.content_frame, listFrag, "noteList")
                    .commit();
            drawer.closeDrawer(Gravity.LEFT);

            //Send request for list of notes to server.
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


        }
    }
}
