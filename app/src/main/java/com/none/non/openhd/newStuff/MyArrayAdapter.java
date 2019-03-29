package com.none.non.openhd.newStuff;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

//TODO write own array adapter for spinner with changeable text color

public class MyArrayAdapter extends ArrayAdapter<String> {

    public MyArrayAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }


}
