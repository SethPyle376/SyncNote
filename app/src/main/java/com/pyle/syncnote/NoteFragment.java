package com.pyle.syncnote;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jason on 6/25/2017.
 */

public class NoteFragment extends android.support.v4.app.Fragment {

    public String title;
    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("nodejs", "view created");
        Bundle bundle = getArguments();
        title = bundle.getString("title");
        myView = inflater.inflate(R.layout.notefragment, container, false);
        return myView;
    }
}
