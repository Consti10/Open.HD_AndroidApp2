package com.none.non.openhd.newStuff;

import android.content.Context;

import com.none.non.openhd.R;

import java.util.ArrayList;

public class SettingsFactory {


    public static ArrayList<ASetting> OPENHD_SETTINGS_1(final Context c){
        ArrayList<ASetting> list=new ArrayList<>();
        list.add(new ASetting("FC_RC_BAUDRATE",c, R.array.FC_RC_BAUDRATE));
        list.add(new ASetting("FC_TELEMETRY_BAUDRATE",c));
        list.add(new ASetting("FC_MSP_BAUDRATE",c));
        list.add(new ASetting("FREQ",c,R.array.FREQ));
        list.add(new ASetting("FC_RC_SERIALPORT",c));
        list.add(new ASetting("FC_TELEMETRY_SERIALPORT",c));
        list.add(new ASetting("FC_MSP_SERIALPORT",c));
        list.add(new ASetting("UPDATE_NTH_TIME",c));
        list.add(new ASetting("DATARATE",c));
        list.add(new ASetting("VIDEO_BLOCKS",c));
        list.add(new ASetting("VIDEO_FECS",c));
        list.add(new ASetting("VIDEO_BLOCKLENGTH",c));
        list.add(new ASetting("VIDEO_BITRATE",c));
        list.add(new ASetting("BITRATE_PERCENT",c));
        list.add(new ASetting("WIDTH",c));
        list.add(new ASetting("HEIGHT",c));
        list.add(new ASetting("EXTRAPARAMS",c));
        list.add(new ASetting("KEYFRAMERATE",c));
        list.add(new ASetting("FPS",c));
        list.add(new ASetting("FREQSCAN",c));
        list.add(new ASetting("TXMODE",c));
        list.add(new ASetting("TELEMETRY_TRANSMISSION",c));
        list.add(new ASetting("TELEMETRY_UPLINK",c));
        list.add(new ASetting("RC",c));
        list.add(new ASetting("CTS_PROTECTION",c));
        list.add(new ASetting("WIFI_HOTSPOT",c,R.array.Y_OR_N));
        list.add(new ASetting("WIFI_HOTSPOT_NIC",c));
        list.add(new ASetting("ETHERNET_HOTSPOT",c,R.array.Y_OR_N));
        list.add(new ASetting("ENABLE_SCREENSHOTS",c,R.array.Y_OR_N));
        list.add(new ASetting("FORWARD_STREAM",c));
        list.add(new ASetting("VIDEO_UDP_PORT",c));
        return list;
    }

    public static ArrayList<ASetting> OPENHD_SETTINGS_2(final Context c){
        ArrayList<ASetting> list=new ArrayList<>();
        list.add(new ASetting("txpowerA",c));
        list.add(new ASetting("txpowerR",c));
        //
        list.add(new ASetting("DefaultAudioOut",c));
        list.add(new ASetting("RemoteSettingsEnabled",c));
        list.add(new ASetting("IsAudioTransferEnabled",c));
        list.add(new ASetting("IsCamera1Enabled",c));
        list.add(new ASetting("IsCamera2Enabled",c));
        list.add(new ASetting("IsCamera3Enabled",c));
        list.add(new ASetting("IsCamera4Enabled",c));
        list.add(new ASetting("DefaultCameraId",c));
        list.add(new ASetting("ChannelToListen",c));
        list.add(new ASetting("Camera1ValueMin",c));
        list.add(new ASetting("Camera1ValueMax",c));
        list.add(new ASetting("Camera2ValueMin",c));
        list.add(new ASetting("Camera2ValueMax",c));
        list.add(new ASetting("Camera3ValueMin",c));
        list.add(new ASetting("Camera3ValueMax",c));
        list.add(new ASetting("Camera4ValueMin",c));
        list.add(new ASetting("Camera4ValueMax",c));
        list.add(new ASetting("EncryptionOrRange",c, R.array.EncryptionOrRangeArray));
        list.add(new ASetting("IsBandSwicherEnabled",c));
        list.add(new ASetting("Bandwidth",c));
        list.add(new ASetting("UplinkSpeed",c));
        list.add(new ASetting("ChannelToListen2",c));
        list.add(new ASetting("PrimaryCardMAC",c));
        list.add(new ASetting("SlaveCardMAC",c));
        list.add(new ASetting("Band5Below",c));
        list.add(new ASetting("Band10ValueMin",c));
        list.add(new ASetting("Band10ValueMax",c));
        list.add(new ASetting("Band20After",c));
        return list;
    }

    public static ArrayList<ASetting> OPENHD_OSD(final Context c){
        ArrayList<ASetting> list=new ArrayList<>();
        list.add(new ASetting("IMPERIAL",c,R.array.true_OR_false));
        list.add(new ASetting("COPTER",c,R.array.true_OR_false));
        list.add(new ASetting("GLOBAL_SCALE",c));
        list.add(new ASetting("CELLS",c));
        list.add(new ASetting("CELL_MAX",c));
        list.add(new ASetting("CELL_MIN",c));
        list.add(new ASetting("CELL_WARNING1",c));
        list.add(new ASetting("CELL_WARNING2",c));
        return list;
    }
}
