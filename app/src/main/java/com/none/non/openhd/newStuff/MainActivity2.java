package com.none.non.openhd.newStuff;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.none.non.openhd.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity2  extends AppCompatActivity implements TCPClient.ProcessMessage {

    //ArrayList<ASetting> mSelectedSyncSettings;

    Button bRefresh;
    Button bApply;
    Button bPing;
    Switch sSyncGroundOnly;
    TableLayout tableLayout;
    Context context;
    ArrayList<ArrayList<ASetting>> ALL_SYNCHRONIZED_SETTINGS;
    ArrayList<ASetting> mSelectedSyncSettings;

    private final TCPClient client=new TCPClient(this);
    private AtomicBoolean connectionEstablished=new AtomicBoolean(false);

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_main2);
        //Note: call OPENHD_SETTINGS_1 after setContentView !
        ALL_SYNCHRONIZED_SETTINGS=new ArrayList<>();
        ALL_SYNCHRONIZED_SETTINGS.add(SettingsFactory.OPENHD_SETTINGS_1(this));
        ALL_SYNCHRONIZED_SETTINGS.add(SettingsFactory.OPENHD_SETTINGS_2(this));
        ALL_SYNCHRONIZED_SETTINGS.add(SettingsFactory.OPENHD_OSD_Settings(this));
        for(final ArrayList<ASetting> list:ALL_SYNCHRONIZED_SETTINGS){
            for(final ASetting setting:list){
                setting.getKeyView().setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if(!connectionEstablished.get()){
                            Toast.makeText(context,"Connect first",Toast.LENGTH_SHORT).show();
                        }else{
                            client.sendMessage(Message.BuildMessageGET(sSyncGroundOnly.isChecked(),setting.KEY));
                        }
                        return false;
                    }
                });
            }
        }
        mSelectedSyncSettings =ALL_SYNCHRONIZED_SETTINGS.get(0);
        tableLayout=findViewById(R.id.tableLayout);
        //Populate the layout with all synchronized settings values
        for(final ASetting setting: mSelectedSyncSettings){
            tableLayout.addView(setting.tableRow);
            //Disable the edit text - only as soon as it is initialized with its default currentValue (confirmed by both ground and air) we enable it
            setting.reset();
        }
        TabLayout tabLayout=findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tableLayout.removeAllViews();
                mSelectedSyncSettings =ALL_SYNCHRONIZED_SETTINGS.get(tab.getPosition());
                //Populate the layout with all synchronized settings values
                for(final ASetting setting: mSelectedSyncSettings){
                    tableLayout.addView(setting.tableRow);
                    //Disable the edit text - only as soon as it is initialized with its default currentValue (confirmed by both ground and air) we enable it
                    setting.reset();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        bRefresh=findViewById(R.id.buttonRefresh);
        bApply=findViewById(R.id.buttonApply);
        sSyncGroundOnly=findViewById(R.id.switchOnlySyncGround);
        sSyncGroundOnly.setChecked(true);
        bPing=findViewById(R.id.buttonPing);
        bPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!connectionEstablished.get()){
                    Toast.makeText(context,"Connect first",Toast.LENGTH_SHORT).show();
                }
                client.sendMessage(Message.BuildMessageHELLO(sSyncGroundOnly.isChecked()));
            }
        });
        bRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!connectionEstablished.get()){
                    Toast.makeText(context, "Please connect your device first", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Disable all views
                //send GET message for all synchronized settings
                for(final ASetting setting: mSelectedSyncSettings){
                    setting.reset();
                    client.sendMessage(Message.BuildMessageGET(sSyncGroundOnly.isChecked(),setting.KEY));
                }
            }
        });

        bApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!connectionEstablished.get()){
                    Toast.makeText(context, "Please connect your device first", Toast.LENGTH_SHORT).show();
                    return;
                }
                final ArrayList<ASetting> modifiedSettings=new ArrayList<>();
                for(final ASetting setting: mSelectedSyncSettings){
                    if(setting.hasBeenUpdatedByUser()){
                        modifiedSettings.add(setting);
                    }
                }
                if(modifiedSettings.isEmpty()){
                    Toast.makeText(context,"Change settings first",Toast.LENGTH_SHORT).show();
                }else{
                    StringBuilder messageToUser= new StringBuilder("Do you want to change these values:\n");
                    for(final ASetting setting: modifiedSettings){
                        messageToUser.append(setting.KEY).append(" ").append(setting.getCurrentValue()).append("\n");
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setCancelable(true);
                    builder.setMessage(messageToUser.toString());
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            for(final ASetting setting:modifiedSettings){
                                client.sendMessage(Message.BuildMessageCHANGE(sSyncGroundOnly.isChecked(),setting.KEY,setting.getCurrentValue()));
                                //setting.reset();
                            }
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }


    @Override
    protected void onResume(){
        super.onResume();
        client.start(context);
    }


    @Override
    protected void onPause(){
        super.onPause();
        client.stop();
        try {
            client.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }




    private void processMessageGET_OK(final boolean ground,final String key,final String value){
        //find the matching setting and call its processing function
        for(final ASetting setting: mSelectedSyncSettings){
            if(key.equals(setting.KEY)){
                setting.processMessageGET_OK(ground,value,sSyncGroundOnly.isChecked());
                break;
            }
        }
    }

    private void processMessageCHANGE_OK(final boolean ground,final String key,final String value){
        //find the matching setting and call its processing function
        for(final ASetting setting: mSelectedSyncSettings){
            if(key.equals(setting.KEY)){
                setting.processMessageCHANGE_OK(ground,value,sSyncGroundOnly.isChecked());
                break;
            }
        }
    }

    @Override
    public void processMessage(final String messageData) {
        System.out.println("Received from server:"+ messageData);
        final Message message=new Message(messageData);
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (message.cmd) {
                    case "HELLO":{
                        client.sendMessage(Message.BuildMessageHELLO_OK());
                        break;
                    }
                    case "HELLO_OK":{
                        Toast.makeText(context,message.src+" "+message.cmd,Toast.LENGTH_SHORT).show();
                        break;
                    }
                    case "GET_OK": {
                        processMessageGET_OK(message.ground(),message.dataKey,message.dataValue);
                        break;
                    }
                    case "CHANGE_OK": {
                        processMessageCHANGE_OK(message.ground(),message.dataKey,message.dataValue);
                        break;
                    }
                    default:
                        System.out.println("Unknown command"+message.toString());
                        break;
                }
            }
        });
    }


    @Override
    public void connectionEstablished() {
        connectionEstablished.set(true);
        //for(final ASetting setting : mSelectedSyncSettings){
            //client.sendMessage("GET "+setting.KEY);
        //}
        System.out.println("Connection established");
    }

    @Override
    public void connectionClosed(){
        System.out.println("Connection closed");
        connectionEstablished.set(false);
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for(final ASetting setting: mSelectedSyncSettings){
                    setting.reset();
                }
            }
        });
    }


}
