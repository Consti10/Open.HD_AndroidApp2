package com.none.non.openhd.newStuff;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TableLayout;
import android.widget.Toast;

import com.none.non.openhd.R;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity2  extends AppCompatActivity implements TCPClient.ProcessMessage {

    ArrayList<ASetting> mSynchronizedSettings;

    Button bRefresh;
    Button bApply;
    Button bPing;
    Switch sSyncGroundOnly;
    Context context;

    private final TCPClient client=new TCPClient(this);
    private AtomicBoolean connectionEstablished=new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_main2);
        //Note: call createList after setContentView !
        mSynchronizedSettings= createList();
        bRefresh=findViewById(R.id.buttonRefresh);
        bApply=findViewById(R.id.buttonApply);
        sSyncGroundOnly=findViewById(R.id.switchOnlySyncGround);
        bPing=findViewById(R.id.buttonPing);
        bPing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!connectionEstablished.get()){
                    Toast.makeText(context,"Connect first",Toast.LENGTH_SHORT).show();
                }
                client.sendMessage(Message.BuildMessageHELLO());
            }
        });

        final TableLayout tableLayout=findViewById(R.id.tableLayout);
        //Populate the layout with all synchronized settings values
        for(final ASetting setting: mSynchronizedSettings){
            tableLayout.addView(setting.tableRow);
            //Disable the edit text - only as soon as it is initialized with its default currentValue (confirmed by both ground and air) we enable it
            setting.reset();
        }

        bRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!connectionEstablished.get()){
                    Toast.makeText(context, "Please connect your device first", Toast.LENGTH_SHORT).show();
                    return;
                }
                //Disable all views
                //send GET message for all synchronized settings
                for(final ASetting setting:mSynchronizedSettings){
                    setting.reset();
                    client.sendMessage(Message.BuildMessageGET(sSyncGroundOnly.isChecked(),setting.KEY));
                    //Slow down for debugging
                    //try {
                    //    Thread.sleep(2000);
                    //} catch (InterruptedException e) {
                    //   e.printStackTrace();
                    //}
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
                for(final ASetting setting:mSynchronizedSettings){
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
                                setting.reset();
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



    private void makeToast(final String message) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processMessageGET_OK(final boolean ground,final String key,final String value){
        //find the matching setting and call its processing function
        //run this function on the UI thread
        for(final ASetting setting:mSynchronizedSettings){
            if(key.equals(setting.KEY)){
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setting.processMessageGET_OK(ground,value,sSyncGroundOnly.isChecked());
                    }
                });
            }
        }
    }

    private void processMessageCHANGE_OK(final boolean ground,final String key,final String value){
        //find the matching setting and call its processing function
        //run this function on the UI thread
        for(final ASetting setting:mSynchronizedSettings){
            if(key.equals(setting.KEY)){
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setting.processMessageCHANGE_OK(ground,value,sSyncGroundOnly.isChecked());
                    }
                });
            }
        }
    }

    @Override
    public void processMessage(final String messageData) {
        System.out.println("Received from server:"+ messageData);
        final Message message=new Message(messageData);
        switch (message.cmd) {
            case "HELLO":{
                client.sendMessage(Message.BuildMessageHELLO_OK());
                break;
            }
            case "HELLO_OK":{
                makeToast(message.src+" "+message.cmd);
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


    @Override
    public void connectionEstablished() {
        connectionEstablished.set(true);
        //for(final ASetting setting : mSynchronizedSettings){
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
                for(final ASetting setting:mSynchronizedSettings){
                    setting.reset();
                }
            }
        });
    }


    private ArrayList<ASetting> createList(){
        ArrayList<ASetting> list=new ArrayList<>();
        list.add(new ASetting("FC_RC_BAUDRATE",this, R.array.FC_RC_BAUDRATE));
        list.add(new ASetting("FC_TELEMETRY_BAUDRATE",this));
        list.add(new ASetting("FC_MSP_BAUDRATE",this));
        list.add(new ASetting("FREQ",this,R.array.FREQ));
        list.add(new ASetting("FC_RC_SERIALPORT",this));
        list.add(new ASetting("FC_TELEMETRY_SERIALPORT",this));
        list.add(new ASetting("FC_MSP_SERIALPORT",this));
        list.add(new ASetting("UPDATE_NTH_TIME",this));
        list.add(new ASetting("DATARATE",this));
        list.add(new ASetting("VIDEO_BLOCKS",this));
        list.add(new ASetting("VIDEO_FECS",this));
        list.add(new ASetting("VIDEO_BLOCKLENGTH",this));
        list.add(new ASetting("VIDEO_BITRATE",this));
        list.add(new ASetting("BITRATE_PERCENT",this));
        list.add(new ASetting("WIDTH",this));
        list.add(new ASetting("HEIGHT",this));
        list.add(new ASetting("EXTRAPARAMS",this));
        list.add(new ASetting("KEYFRAMERATE",this));
        list.add(new ASetting("FPS",this));
        list.add(new ASetting("FREQSCAN",this));
        list.add(new ASetting("TXMODE",this));
        list.add(new ASetting("TELEMETRY_TRANSMISSION",this));
        list.add(new ASetting("TELEMETRY_UPLINK",this));
        list.add(new ASetting("RC",this));
        list.add(new ASetting("CTS_PROTECTION",this));
        list.add(new ASetting("WIFI_HOTSPOT",this));
        list.add(new ASetting("WIFI_HOTSPOT_NIC",this));
        list.add(new ASetting("ETHERNET_HOTSPOT",this));
        list.add(new ASetting("ENABLE_SCREENSHOTS",this));
        list.add(new ASetting("FORWARD_STREAM",this));
        list.add(new ASetting("VIDEO_UDP_PORT",this));
        //OSD
        //list.add(new ASetting("Copter",this,R.array.CopterArray));
        //list.add(new ASetting("Imperial",this,R.array.ImperialArray));
        //list.add(new ASetting("CELLS",this));
        //Special
        list.add(new ASetting("txpowerA",this));
        list.add(new ASetting("txpowerR",this));
        //
        list.add(new ASetting("DefaultAudioOut",this));
        list.add(new ASetting("RemoteSettingsEnabled",this));
        list.add(new ASetting("IsAudioTransferEnabled",this));
        list.add(new ASetting("IsCamera1Enabled",this));
        list.add(new ASetting("IsCamera2Enabled",this));
        list.add(new ASetting("IsCamera3Enabled",this));
        list.add(new ASetting("IsCamera4Enabled",this));
        list.add(new ASetting("DefaultCameraId",this));
        list.add(new ASetting("ChannelToListen",this));
        list.add(new ASetting("Camera1ValueMin",this));
        list.add(new ASetting("Camera1ValueMax",this));
        list.add(new ASetting("Camera2ValueMin",this));
        list.add(new ASetting("Camera2ValueMax",this));
        list.add(new ASetting("Camera3ValueMin",this));
        list.add(new ASetting("Camera3ValueMax",this));
        list.add(new ASetting("Camera4ValueMin",this));
        list.add(new ASetting("Camera4ValueMax",this));
        list.add(new ASetting("EncryptionOrRange",this, R.array.EncryptionOrRangeArray));
        list.add(new ASetting("IsBandSwicherEnabled",this));
        list.add(new ASetting("Bandwidth",this));
        list.add(new ASetting("UplinkSpeed",this));
        list.add(new ASetting("ChannelToListen2",this));
        list.add(new ASetting("PrimaryCardMAC",this));
        list.add(new ASetting("SlaveCardMAC",this));
        list.add(new ASetting("Band5Below",this));
        list.add(new ASetting("Band10ValueMin",this));
        list.add(new ASetting("Band10ValueMax",this));
        list.add(new ASetting("Band20After",this));
        return list;
    }
}
