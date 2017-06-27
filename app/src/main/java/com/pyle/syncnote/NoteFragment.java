package com.pyle.syncnote;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Jason on 6/25/2017.
 */

public class NoteFragment extends Fragment {
    public String title;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("nodejs", "view created");
        Bundle bundle = getArguments();
        title = bundle.getString("title");
        return inflater.inflate(R.layout.notefragment, container, false);
    }
}
