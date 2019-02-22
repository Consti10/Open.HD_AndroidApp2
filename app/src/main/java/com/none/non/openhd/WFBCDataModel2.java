package com.none.non.openhd;

import java.util.ArrayList;

public class WFBCDataModel2 {

    public static ArrayList<ASetting> createList(){
        ArrayList<ASetting> list=new ArrayList<>();
        list.add(new ASetting("FC_RC_BAUDRATE"));
        list.add(new ASetting("FC_TELEMETRY_BAUDRATE"));
        list.add(new ASetting("FC_MSP_BAUDRATE"));
        list.add(new ASetting("FREQ"));
        list.add(new ASetting("FC_RC_SERIALPORT"));
        list.add(new ASetting("DefaultAudioOut"));
        list.add(new ASetting("RemoteSettingsEnabled"));
        list.add(new ASetting("IsAudioTransferEnabled"));
        list.add(new ASetting("txpowerA"));
        list.add(new ASetting("txpowerR"));
        list.add(new ASetting("FC_TELEMETRY_SERIALPORT"));
        list.add(new ASetting("FC_MSP_SERIALPORT"));
        list.add(new ASetting("UPDATE_NTH_TIME"));
        list.add(new ASetting("Copter"));
        list.add(new ASetting("Imperial"));
        list.add(new ASetting("DATARATE"));
        list.add(new ASetting("VIDEO_BLOCKS"));
        list.add(new ASetting("VIDEO_FECS"));
        list.add(new ASetting("VIDEO_BLOCKLENGTH"));
        list.add(new ASetting("VIDEO_BITRATE"));
        list.add(new ASetting("BITRATE_PERCENT"));
        list.add(new ASetting("WIDTH"));
        list.add(new ASetting("HEIGHT"));
        list.add(new ASetting("EXTRAPARAMS"));
        list.add(new ASetting("KEYFRAMERATE"));
        list.add(new ASetting("FPS"));
        list.add(new ASetting("FREQSCAN"));
        list.add(new ASetting("TXMODE"));
        list.add(new ASetting("TELEMETRY_TRANSMISSION"));
        list.add(new ASetting("TELEMETRY_UPLINK"));
        list.add(new ASetting("RC"));
        list.add(new ASetting("CTS_PROTECTION"));
        list.add(new ASetting("WIFI_HOTSPOT"));
        list.add(new ASetting("WIFI_HOTSPOT_NIC"));
        list.add(new ASetting("ETHERNET_HOTSPOT"));
        list.add(new ASetting("ENABLE_SCREENSHOTS"));
        list.add(new ASetting("FORWARD_STREAM"));
        list.add(new ASetting("VIDEO_UDP_PORT"));
        list.add(new ASetting("IsCamera1Enabled"));
        list.add(new ASetting("IsCamera2Enabled"));
        list.add(new ASetting("IsCamera3Enabled"));
        list.add(new ASetting("IsCamera4Enabled"));
        list.add(new ASetting("DefaultCameraId"));
        list.add(new ASetting("ChannelToListen"));
        list.add(new ASetting("Camera1ValueMin"));
        list.add(new ASetting("Camera1ValueMax"));
        list.add(new ASetting("Camera2ValueMin"));
        list.add(new ASetting("Camera2ValueMax"));
        list.add(new ASetting("Camera3ValueMin"));
        list.add(new ASetting("Camera3ValueMax"));
        list.add(new ASetting("Camera4ValueMin"));
        list.add(new ASetting("Camera4ValueMax"));
        list.add(new ASetting("CELLS"));
        list.add(new ASetting("EncryptionOrRange"));
        list.add(new ASetting("IsBandSwicherEnabled"));
        list.add(new ASetting("Bandwidth"));
        list.add(new ASetting("UplinkSpeed"));
        list.add(new ASetting("ChannelToListen2"));
        list.add(new ASetting("PrimaryCardMAC"));
        list.add(new ASetting("SlaveCardMAC"));
        list.add(new ASetting("Band5Below"));
        list.add(new ASetting("Band10ValueMin"));
        list.add(new ASetting("Band10ValueMax"));
        list.add(new ASetting("Band20After"));
        return list;
    }

}

