package com.pyle.syncnote;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import java.io.Console;

/**
 * Created by Owner on 6/26/2017.
 */

public class NotesListOnItemClickListener implements AdapterView.OnItemClickListener {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d("nodejs", "click registered");
    }
}
