package com.none.non.openhd;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Abstract setting
 */

@SuppressWarnings("WeakerAccess")
public class ASetting {

    public final String key;
    public String value;

    public int changed,ackAir,ackGround;

    public ASetting(final String key){
        this.key=key;
    }


    public TextView textView;
    //Can be edited on the Android side either using a spinner (fixed values) or
    //using a edit text
    public Spinner spinner;
    public EditText editText;

}
