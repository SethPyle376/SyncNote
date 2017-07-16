package com.pyle.syncnote;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/**
 * Created by Jason on 6/25/2017.
 */

public class NoteFragment extends android.support.v4.app.Fragment {

    public String title;
    View myView;
    private FloatingActionButton color;
    private FloatingActionButton bold;
    private FloatingActionButton italic;
    private FloatingActionButton bullet;
    private FloatingActionButton box;
    private FloatingActionButton color_black;
    private FloatingActionButton color_red;
    private FloatingActionButton color_blue;
    private FloatingActionButton color_green;
    private FloatingActionButton bullet_on;
    private EditText contentText;
    private Context myActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("nodejs", "view created");
        Bundle bundle = getArguments();
        title = bundle.getString("title");
        myView = inflater.inflate(R.layout.notefragment, container, false);
        myActivity = getActivity();

        // This allows us the user to change the selected text to bold
        bold = (FloatingActionButton)  myView.findViewById(R.id.fab_bold);
        if (bold != null) {
            bold.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contentText = (EditText) myView.findViewById(R.id.sharedText);

                    int selectionStart = contentText.getSelectionStart();

                    int selectionEnd = contentText.getSelectionEnd();

                    if (selectionStart > selectionEnd) {
                        int temp = selectionEnd;
                        selectionEnd = selectionStart;
                        selectionStart = temp;
                    }

                    if (selectionEnd > selectionStart) {
                        Spannable str = contentText.getText();
                        StyleSpan[] ss = str.getSpans(selectionStart, selectionEnd, StyleSpan.class);

                        boolean exists = false;
                        for (int i = 0; i < ss.length; i++) {
                            if (ss[i].getStyle() == Typeface.BOLD) {
                                str.removeSpan(ss[i]);
                                exists = true;
                            }
                        }

                        if (!exists) {
                            str.setSpan(new StyleSpan(Typeface.BOLD), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    checkSelection(selectionEnd, selectionStart);
                }

            });
        }

        // This allows the user to change the selected text to italics
        italic = (FloatingActionButton)  myView.findViewById(R.id.fab_italic);
        if (italic != null) {
            italic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contentText = (EditText) myView.findViewById(R.id.sharedText);

                    int selectionStart = contentText.getSelectionStart();

                    int selectionEnd = contentText.getSelectionEnd();

                    if (selectionStart > selectionEnd) {
                        int temp = selectionEnd;
                        selectionEnd = selectionStart;
                        selectionStart = temp;
                    }

                    if (selectionEnd > selectionStart) {
                        Spannable str = contentText.getText();
                        StyleSpan[] ss = str.getSpans(selectionStart, selectionEnd, StyleSpan.class);

                        boolean exists = false;
                        for (int i = 0; i < ss.length; i++) {
                            if (ss[i].getStyle() == Typeface.ITALIC) {
                                str.removeSpan(ss[i]);
                                exists = true;
                            }
                        }

                        if (!exists) {
                            str.setSpan(new StyleSpan(Typeface.ITALIC), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        }
                    }

                    checkSelection(selectionEnd, selectionStart);
                }

            });
        }

        // This shows the hidden buttons that allow the user to change text colors
        color = (FloatingActionButton)  myView.findViewById(R.id.fab_color);
        color_black = (FloatingActionButton)  myView.findViewById(R.id.fab_color_black);
        color_red = (FloatingActionButton)  myView.findViewById(R.id.fab_color_red);
        color_blue = (FloatingActionButton)  myView.findViewById(R.id.fab_color_blue);
        color_green = (FloatingActionButton)  myView.findViewById(R.id.fab_color_green);
        if (color != null) {
            color.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    color_black.show();
                    color_blue.show();
                    color_red.show();
                    color_green.show();
                }
            });
        }

        // This allows the user to change the selected text to a different color
        if (color_black != null) {
            color_black.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contentText = (EditText) myView.findViewById(R.id.sharedText);

                    int selectionStart = contentText.getSelectionStart();

                    int selectionEnd = contentText.getSelectionEnd();

                    if (selectionStart > selectionEnd) {
                        int temp = selectionEnd;
                        selectionEnd = selectionStart;
                        selectionStart = temp;
                    }

                    if (selectionEnd > selectionStart) {
                        Spannable str = contentText.getText();
                        str.setSpan(new ForegroundColorSpan(Color.BLACK), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    color_black.setVisibility(view.GONE);
                    color_blue.setVisibility(view.GONE);
                    color_red.setVisibility(view.GONE);
                    color_green.setVisibility(view.GONE);

                    checkSelection(selectionEnd, selectionStart);
                }

            });
        }

        // This allows the user to change the selected text to a different color
        if (color_red != null) {
            color_red.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contentText = (EditText) myView.findViewById(R.id.sharedText);

                    int selectionStart = contentText.getSelectionStart();

                    int selectionEnd = contentText.getSelectionEnd();

                    if (selectionStart > selectionEnd) {
                        int temp = selectionEnd;
                        selectionEnd = selectionStart;
                        selectionStart = temp;
                    }

                    if (selectionEnd > selectionStart) {
                        Spannable str = contentText.getText();
                        str.setSpan(new ForegroundColorSpan(Color.RED), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    color_black.setVisibility(view.GONE);
                    color_blue.setVisibility(view.GONE);
                    color_red.setVisibility(view.GONE);
                    color_green.setVisibility(view.GONE);

                    checkSelection(selectionEnd, selectionStart);
                }

            });
        }

        // This allows the user to change the selected text to a different color
        if (color_blue != null) {
            color_blue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contentText = (EditText) myView.findViewById(R.id.sharedText);

                    int selectionStart = contentText.getSelectionStart();

                    int selectionEnd = contentText.getSelectionEnd();

                    if (selectionStart > selectionEnd) {
                        int temp = selectionEnd;
                        selectionEnd = selectionStart;
                        selectionStart = temp;
                    }

                    if (selectionEnd > selectionStart) {
                        Spannable str = contentText.getText();
                        str.setSpan(new ForegroundColorSpan(Color.BLUE), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    color_black.setVisibility(view.GONE);
                    color_blue.setVisibility(view.GONE);
                    color_red.setVisibility(view.GONE);
                    color_green.setVisibility(view.GONE);

                    checkSelection(selectionEnd, selectionStart);
                }

            });
        }

        // This allows the user to change the selected text to a different color
        if (color_green != null) {
            color_green.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contentText = (EditText) myView.findViewById(R.id.sharedText);

                    int selectionStart = contentText.getSelectionStart();

                    int selectionEnd = contentText.getSelectionEnd();

                    if (selectionStart > selectionEnd) {
                        int temp = selectionEnd;
                        selectionEnd = selectionStart;
                        selectionStart = temp;
                    }

                    if (selectionEnd > selectionStart) {
                        Spannable str = contentText.getText();
                        str.setSpan(new ForegroundColorSpan(Color.argb(255,70,185,0)), selectionStart, selectionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    color_black.setVisibility(view.GONE);
                    color_blue.setVisibility(view.GONE);
                    color_red.setVisibility(view.GONE);
                    color_green.setVisibility(view.GONE);

                    checkSelection(selectionEnd, selectionStart);
                }

            });
        }

        return myView;
    }

    /*
     * Check to make sure that something is selected, if not, displays instructions to the user
     */
    private void checkSelection (int selectionEnd, int selectionStart) {

        if(myActivity != null){
            if (selectionEnd == selectionStart) {
                AlertDialog.Builder builder = new AlertDialog.Builder(myActivity);
                builder.setTitle("Text must be selected to apply changes");
                final EditText input = new EditText(myActivity);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setNegativeButton("Got It!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        }
    }
}
