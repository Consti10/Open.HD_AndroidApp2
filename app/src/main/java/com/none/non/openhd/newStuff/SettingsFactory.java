package com.none.non.openhd.newStuff;

import android.content.Context;

import com.none.non.openhd.R;

import java.util.ArrayList;

public class SettingsFactory {


    public static ArrayList<AbstractSetting> OPENHD_SETTINGS_1(final Context c){
        ArrayList<AbstractSetting> list=new ArrayList<>();
        list.add(new AbstractSetting("FC_RC_BAUDRATE",c, R.array.FC_RC_BAUDRATE));
        list.add(new AbstractSetting("FC_TELEMETRY_BAUDRATE",c));
        list.add(new AbstractSetting("FC_MSP_BAUDRATE",c));
        list.add(new AbstractSetting("FREQ",c,R.array.FREQ));
        list.add(new AbstractSetting("FC_RC_SERIALPORT",c));
        list.add(new AbstractSetting("FC_TELEMETRY_SERIALPORT",c));
        list.add(new AbstractSetting("FC_MSP_SERIALPORT",c));
        list.add(new AbstractSetting("UPDATE_NTH_TIME",c));
        list.add(new AbstractSetting("DATARATE",c));
        list.add(new AbstractSetting("VIDEO_BLOCKS",c));
        list.add(new AbstractSetting("VIDEO_FECS",c));
        list.add(new AbstractSetting("VIDEO_BLOCKLENGTH",c));
        list.add(new AbstractSetting("VIDEO_BITRATE",c));
        list.add(new AbstractSetting("BITRATE_PERCENT",c));
        list.add(new AbstractSetting("WIDTH",c));
        list.add(new AbstractSetting("HEIGHT",c));
        list.add(new AbstractSetting("EXTRAPARAMS",c));
        list.add(new AbstractSetting("KEYFRAMERATE",c));
        list.add(new AbstractSetting("FPS",c));
        list.add(new AbstractSetting("FREQSCAN",c));
        list.add(new AbstractSetting("TXMODE",c));
        list.add(new AbstractSetting("TELEMETRY_TRANSMISSION",c));
        list.add(new AbstractSetting("TELEMETRY_UPLINK",c));
        list.add(new AbstractSetting("RC",c));
        list.add(new AbstractSetting("CTS_PROTECTION",c));
        list.add(new AbstractSetting("WIFI_HOTSPOT",c,R.array.Y_OR_N));
        list.add(new AbstractSetting("WIFI_HOTSPOT_NIC",c));
        list.add(new AbstractSetting("ETHERNET_HOTSPOT",c,R.array.Y_OR_N));
        list.add(new AbstractSetting("ENABLE_SCREENSHOTS",c,R.array.Y_OR_N));
        list.add(new AbstractSetting("FORWARD_STREAM",c));
        list.add(new AbstractSetting("VIDEO_UDP_PORT",c));
        return list;
    }

    public static ArrayList<AbstractSetting> OPENHD_SETTINGS_2(final Context c){
        ArrayList<AbstractSetting> list=new ArrayList<>();
        list.add(new AbstractSetting("TxPower_A",c));
        list.add(new AbstractSetting("TxPower_R",c));
        //
        list.add(new AbstractSetting("DefaultAudioOut",c));
        list.add(new AbstractSetting("IsAudioTransferEnabled",c));
        list.add(new AbstractSetting("IsCamera1Enabled",c));
        list.add(new AbstractSetting("IsCamera2Enabled",c));
        list.add(new AbstractSetting("IsCamera3Enabled",c));
        list.add(new AbstractSetting("IsCamera4Enabled",c));
        list.add(new AbstractSetting("DefaultCameraId",c));
        list.add(new AbstractSetting("ChannelToListen",c));
        list.add(new AbstractSetting("Camera1ValueMin",c));
        list.add(new AbstractSetting("Camera1ValueMax",c));
        list.add(new AbstractSetting("Camera2ValueMin",c));
        list.add(new AbstractSetting("Camera2ValueMax",c));
        list.add(new AbstractSetting("Camera3ValueMin",c));
        list.add(new AbstractSetting("Camera3ValueMax",c));
        list.add(new AbstractSetting("Camera4ValueMin",c));
        list.add(new AbstractSetting("Camera4ValueMax",c));
        list.add(new AbstractSetting("EncryptionOrRange",c, R.array.Encryption_OR_Range));
        list.add(new AbstractSetting("IsBandSwicherEnabled",c));
        list.add(new AbstractSetting("Bandwidth",c));
        list.add(new AbstractSetting("UplinkSpeed",c));
        list.add(new AbstractSetting("ChannelToListen2",c));
        list.add(new AbstractSetting("PrimaryCardMAC",c));
        list.add(new AbstractSetting("SlaveCardMAC",c));
        list.add(new AbstractSetting("Band5Below",c));
        list.add(new AbstractSetting("Band10ValueMin",c));
        list.add(new AbstractSetting("Band10ValueMax",c));
        list.add(new AbstractSetting("Band20After",c));
        return list;
    }

    public static ArrayList<AbstractSetting> OPENHD_OSD_Settings(final Context c){
        ArrayList<AbstractSetting> list=new ArrayList<>();
        list.add(new AbstractSetting("IMPERIAL",c,R.array.true_OR_false));
        list.add(new AbstractSetting("COPTER",c,R.array.true_OR_false));
        list.add(new AbstractSetting("GLOBAL_SCALE",c));
        list.add(new AbstractSetting("CELLS",c));
        list.add(new AbstractSetting("CELL_MAX",c));
        list.add(new AbstractSetting("CELL_MIN",c));
        list.add(new AbstractSetting("CELL_WARNING1",c));
        list.add(new AbstractSetting("CELL_WARNING2",c));
        return list;
    }
}
