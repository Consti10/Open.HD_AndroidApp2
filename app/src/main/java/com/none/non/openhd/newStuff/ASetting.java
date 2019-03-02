package com.none.non.openhd.newStuff;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ArrayRes;
import android.support.annotation.ColorInt;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Abstract setting
 */

@SuppressWarnings("WeakerAccess")
public class ASetting implements AdapterView.OnItemSelectedListener,TextWatcher {

    public final String KEY;
    //Text view holding key
    private final TextView textView;
    //There are 2 input methods:
    //EditText or Spinner
    //one of them is always null while the other one is active
    //(that's why there are 2 constructors)
    private final Spinner spinner;
    private final ArrayAdapter<CharSequence> adapter; //null when not spinner
    private final EditText editText;
    public final TableRow tableRow;
    private final UserChangedText userChangedText=new UserChangedText() {
        @Override
        public void onTextChanged(String newText) {
            setColor(Color.RED);
            updatedByUser=true;
        }
    };
    private boolean updatedByUser=false;

    public ASetting(final String key, final Context c){
        this.KEY =key;
        textView=new TextView(c);
        textView.setText(KEY);
        editText=new EditText(c);
        editText.setText("Setting not loaded");
        editText.setInputType(InputType.TYPE_CLASS_TEXT);
        spinner=null;
        adapter=null;
        tableRow=new TableRow(c);
        tableRow.addView(textView);
        tableRow.addView(editText);
        editText.addTextChangedListener(this);
    }

    public ASetting(final String key,final Context c,@ArrayRes int textArrayResId){
        this.KEY =key;
        textView=new TextView(c);
        textView.setText(KEY);
        spinner=new Spinner(c);
        adapter=ArrayAdapter.createFromResource(
                c, textArrayResId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        editText=null;
        tableRow=new TableRow(c);
        tableRow.addView(textView);
        tableRow.addView(spinner);
        spinner.setOnItemSelectedListener(this);
    }


    public boolean hasBeenUpdatedByUser(){
        return updatedByUser;
    }


    public void inputViewSetEnabled(final boolean enable){
        if(spinner!=null){
            spinner.setEnabled(enable);
        }else{
            editText.setEnabled(enable);
        }
    }

    public void setColor(@ColorInt int color){
        if(spinner!=null){
            spinner.setBackgroundColor(color);
        }else{
            editText.setTextColor(color);
        }
    }

    public String getCurrentValue(){
        if(spinner!=null){
            return spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
        }
        return textView.getText().toString();
    }

    public void inputViewUpdateText(final String value){
        //when the input view is an edit text, we just update its content
        //else, we have to find the right index from the drop down menu
        if(editText!=null){
            //The callback should only listen for user modifications
            editText.removeTextChangedListener(this);
            editText.setText(value);
            editText.addTextChangedListener(this);
        }else{
            spinner.setOnItemSelectedListener(null);
            int spinnerPosition=adapter.getPosition(value);
            if(spinnerPosition!=-1){
                spinner.setSelection(spinnerPosition);
            }
            spinner.setOnItemSelectedListener(this);
        }
    }



    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        final String newText=s.toString();
        System.out.println("Text changed");
        userChangedText.onTextChanged(newText);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        final String newText=parent.getItemAtPosition(position).toString();
        userChangedText.onTextChanged(newText);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }



}
