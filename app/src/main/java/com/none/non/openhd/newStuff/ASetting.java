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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Abstract setting
 */

@SuppressWarnings("WeakerAccess")
public class ASetting implements AdapterView.OnItemSelectedListener,TextWatcher {
    private static final String NOT_LOADED="Setting not loaded";
    private static final String NOT_IN_SYNC="Not in sync";

    public final String KEY;
    //Text view holding key
    private final TextView textView;
    //There are 2 input methods:
    //EditText or Spinner
    //one of them is always null while the other one is active
    //(that's why there are 2 constructors)
    private final Spinner spinner;
    private final ArrayAdapter<String> adapter; //null when not spinner
    private final EditText editText;
    public final TableRow tableRow;
    private final UserChangedText userChangedText=new UserChangedText() {
        @Override
        public void onTextChanged(String newText) {
            System.out.println("user changed text"+newText+KEY);
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
        final ArrayList<String> dropdownValues= new ArrayList<>(Arrays.asList(c.getResources().getStringArray(textArrayResId)));
        dropdownValues.add(NOT_LOADED);
        dropdownValues.add(NOT_IN_SYNC);
        adapter=new ArrayAdapter<String>(c,android.R.layout.simple_spinner_dropdown_item,dropdownValues);
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

    public String getCurrentValue(){
        if(spinner!=null){
            return spinner.getItemAtPosition(spinner.getSelectedItemPosition()).toString();
        }
        return editText.getText().toString();
    }
    public void reset(){
        updatedByUser=false;
        setColor(Color.RED);
        inputViewUpdateText(NOT_LOADED);
        inputViewSetEnabled(false);
    }

    public void processMessageGET_OK(final boolean ground,final String value){
        //System.out.println("Process get_ok "+KEY+" "+ground+" "+value);
        //System.out.println("Curr"+getCurrentValue());
        if(!ground){
            if(value.equals(getCurrentValue())){
                //air and ground are in sync
                inputViewSetEnabled(true);
                setColor(Color.GREEN);
            }else{
                inputViewUpdateText(NOT_IN_SYNC);
                inputViewSetEnabled(false);
            }
        }else{
            inputViewUpdateText(value);
            setColor(Color.YELLOW);
        }
    }

    public void processMessageCHANGE_OK(final boolean ground,final String value){
        if(!ground){
            if(value.equals(getCurrentValue())){
                //air and ground are in sync
                inputViewSetEnabled(true);
                setColor(Color.GREEN);
            }else{
                //ask the user if he would like to use the air or ground value

            }
        }else{
            inputViewUpdateText(value);
            setColor(Color.YELLOW);
        }
    }

    private void inputViewSetEnabled(final boolean enable){
        if(spinner!=null){
            spinner.setEnabled(enable);
        }else{
            editText.setEnabled(enable);
        }
    }

    private void setColor(@ColorInt int color){
        if(spinner!=null){
            spinner.setBackgroundColor(color);
        }else{
            editText.setTextColor(color);
        }
    }

    //update input view without notifying the watchers
    private void inputViewUpdateText(final String value){
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
                spinner.setSelection(spinnerPosition,false);//animate==false important else the listener gets notified ! (WTF android !!)
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
