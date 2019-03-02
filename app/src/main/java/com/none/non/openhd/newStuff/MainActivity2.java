package com.none.non.openhd.newStuff;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.Toast;

import com.none.non.openhd.R;

import java.util.ArrayList;

public class MainActivity2  extends AppCompatActivity implements TCPClient.ProcessMessage {

    ArrayList<ASetting> mSynchronizedSettings;

    Button bRefresh;
    Button bApply;
    Context context;

    private final TCPClient client=new TCPClient(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;

        //IPResolver.resolveIP(context);

        setContentView(R.layout.activity_main2);
        //Note: call createList after setContentView !
        mSynchronizedSettings= createList();
        bRefresh=findViewById(R.id.buttonRefresh);
        bApply=findViewById(R.id.buttonApply);

        final TableLayout tableLayout=findViewById(R.id.tableLayout);
        //Populate the layout with all synchronized settings values
        for(final ASetting setting: mSynchronizedSettings){
            tableLayout.addView(setting.tableRow);
            //Disable the edit text - only as soon as it is initialized with its default currentValue we enable it
            setting.inputViewSetEnabled(false);
        }

        bRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Disable all views
                //send GET message for all synchronized settings
                for(final ASetting setting:mSynchronizedSettings){
                    setting.inputViewSetEnabled(false);
                    client.sendMessage("GET "+setting.KEY);
                }
            }
        });

        bApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean anythingChangedByUser=false;
                for(final ASetting setting:mSynchronizedSettings){
                    if(setting.hasBeenUpdatedByUser()){
                        setting.inputViewSetEnabled(false);
                        anythingChangedByUser=true;
                        client.sendMessage("CHANGE "+setting.KEY+"="+setting.getCurrentValue());
                    }
                }
                if(!anythingChangedByUser){
                    Toast.makeText(context,"Change settings first",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        client.start();
    }


    @Override
    protected void onPause(){
        super.onPause();
        client.stop();
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
        //find the right text view
        //update its content with value
        //change its value to Greeon
        for(final ASetting setting:mSynchronizedSettings){
            if(key.equals(setting.KEY)){
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setting.inputViewSetEnabled(true);
                        setting.inputViewUpdateText(value);
                        setting.setColor(Color.GREEN);
                    }
                });
            }
        }
    }

    private void processMessageCHANGE_OK(final boolean ground,final String key,final String value){
        //find the right text view
        //update its content with value
        //make it editable by the user
        //change its color to green
        for(final ASetting setting:mSynchronizedSettings){
            if(key.equals(setting.KEY)){
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setting.inputViewSetEnabled(true);
                        setting.inputViewUpdateText(value);
                        setting.setColor(Color.GREEN);
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
                client.sendMessage("HELLO_OK");
                break;
            }
            case "GET_OK_G": {
                processMessageGET_OK(true,message.dataKey,message.dataValue);
                break;
            }
            case "GET_OK_A": {
                processMessageGET_OK(false,message.dataKey,message.dataValue);
                break;
            }
            case "CHANGE_OK_G": {
                processMessageCHANGE_OK(true,message.dataKey,message.dataValue);
                break;
            }
            case "CHANGE_OK_A": {
                processMessageCHANGE_OK(false,message.dataKey,message.dataValue);
                break;
            }
            default:
                System.out.println("Unknown command"+message.toString());
                break;
        }
    }


    @Override
    public void connectionEstablished() {
        for(final ASetting setting : mSynchronizedSettings){
            client.sendMessage("GET "+setting.KEY);
        }
    }

    @Override
    public void connectionClosed(){
    }


    private ArrayList<ASetting> createList(){
        ArrayList<ASetting> list=new ArrayList<>();
        list.add(new ASetting("FC_RC_BAUDRATE",this, R.array.RateArray));
        list.add(new ASetting("FC_TELEMETRY_BAUDRATE",this));
        list.add(new ASetting("FC_MSP_BAUDRATE",this));
        list.add(new ASetting("FREQ",this,R.array.FREQ));
        list.add(new ASetting("FC_RC_SERIALPORT",this));
        list.add(new ASetting("DefaultAudioOut",this));
        list.add(new ASetting("RemoteSettingsEnabled",this));
        list.add(new ASetting("IsAudioTransferEnabled",this));
        list.add(new ASetting("txpowerA",this));
        list.add(new ASetting("txpowerR",this));
        list.add(new ASetting("FC_TELEMETRY_SERIALPORT",this));
        list.add(new ASetting("FC_MSP_SERIALPORT",this));
        list.add(new ASetting("UPDATE_NTH_TIME",this));
        list.add(new ASetting("Copter",this,R.array.CopterArray));
        list.add(new ASetting("Imperial",this,R.array.ImperialArray));
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
        list.add(new ASetting("CELLS",this));
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
