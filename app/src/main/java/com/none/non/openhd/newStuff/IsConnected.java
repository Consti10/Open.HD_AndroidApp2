package com.none.non.openhd.newStuff;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

//Created for FPV-VR by Constantin

public final class IsConnected {

    //public static final int USB_NOTHING=0,USB_CONNECTED=1,USB_TETHERING=2;
    public enum USB_CONNECTION{
        NOTHING,DATA,TETHERING
    }


    private static boolean checkWifiConnectedToNetwork(final Context context,final String name){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo;
        if (wifiManager != null) {
            wifiInfo = wifiManager.getConnectionInfo();
        }else{
            return false;
        }
        if (wifiInfo.getSupplicantState() == SupplicantState.COMPLETED ) {
            String ssid = wifiInfo.getSSID();
            if(ssid.equals(name)){
                return true;
            }
        }
        return false;
        //System.out.println(wifiInfo.getSupplicantState().toString());
    }

    public static boolean checkWifiConnectedOpenHD(final Context context){
        return checkWifiConnectedToNetwork(context,"\"Open.HD\"");
    }

    public static boolean checkWifiConnectedTest(final Context context){
        return checkWifiConnectedToNetwork(context,"\"TestAero\"");
    }

    public static boolean checkWifiConnectedEZWB(final Context context){
        return checkWifiConnectedToNetwork(context,"\"EZ-WifiBroadcast\"");
    }


    public static USB_CONNECTION getUSBStatus(Context context){
        final Intent intent = context.registerReceiver(null, new IntentFilter("android.hardware.usb.action.USB_STATE"));
        assert intent!=null;
        final Bundle extras=intent.getExtras();
        assert extras!=null;
        final boolean connected=extras.getBoolean("connected",false);
        final boolean tetheringActive=extras.getBoolean("rndis",false);
        if(tetheringActive){
            return USB_CONNECTION.TETHERING;
        }
        if(connected){
            return USB_CONNECTION.DATA;
        }
        return USB_CONNECTION.NOTHING;
    }


    public static String getUSBTetheringLoopbackAddress(){
        try{
            for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
                final NetworkInterface intf=en.nextElement();
                //System.out.println("Intf:"+intf.getName());
                if(intf.getName().contains("rndis")){
                    final Enumeration<InetAddress> inetAdresses=intf.getInetAddresses();
                    while (inetAdresses.hasMoreElements()){
                        final InetAddress inetAddress=inetAdresses.nextElement();
                        System.out.println(inetAddress.toString());
                        //if(inetAddress.isLoopbackAddress()){
                            return inetAddress.toString().replace("/","");
                        //}
                    }
                }
            }
        }catch(Exception e){e.printStackTrace();}
        return null;
    }

    public static int getLastNumberOfIP(final String ip){
        String[] parts = ip.split(".");
        assert (parts.length==4);
        return Integer.parseInt(parts[3]);
    }

}
