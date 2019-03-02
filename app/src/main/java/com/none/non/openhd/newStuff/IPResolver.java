package com.none.non.openhd.newStuff;

import android.content.Context;

import com.none.non.openhd.newStuff.IsConnected;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;

/**
 * There are 3 ways to get the IP address of the Ground Pi
 * a) When connected via WIFI hotspot, the IP is fixed to 192.168.1.1
 * b) When conneccted via USB tethering, all IP's are in start with 192.168.42.XXX
 *      this leaves us with 265 possible addresses, low enough to just ping all of them and await a response
 * c) Since Video,Telemetry and WFB-Telemetry are already broadcasted as soon as the AirPI detects a connection, we can just
 * listen for these messages on the corresponding ports and extract the IP as soon as data was received
 */
public class IPResolver {


    public static String resolveIP(final Context c){
        System.out.println("Resolving IP");

        if(IsConnected.checkWifiConnectedToEZWB(c)){
            return "192.168.1.1";
        }
        if(IsConnected.checkTetheringConnectedToEZWB(c)==IsConnected.USB_TETHERING){
            //Unfortunately the ping is only implemented as an blocking operation, so to speed things up we create 4 threads here
            final ArrayList<String> reachableIPs=new ArrayList<>();
            Thread thread=new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        for(int i=0;i<256;i++){
                            //USB tethering addresses always begin with that
                            final String IP="192.168.42."+Integer.toString(i);
                            final InetAddress address=InetAddress.getByName(IP);
                            final boolean reachable=address.isReachable(10);
                            if(reachable){
                                reachableIPs.add(IP);
                            }
                        }
                    } catch (IOException e){ e.printStackTrace(); }
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return null;
    }



}
