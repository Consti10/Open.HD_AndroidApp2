package com.none.non.openhd.newStuff;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class UDPReceiver extends Thread {

    @Override
    public void run(){
        DatagramSocket socket;
        try {
            socket = new DatagramSocket(5601);
            socket.setSoTimeout(5000);
        } catch (SocketException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("Start receiving");
        byte[] msg = new byte[1000];
        DatagramPacket dp = new DatagramPacket(msg, msg.length);
        while(true){
            try{
                socket.receive(dp);
                System.out.println(dp.getData().toString());

            }catch (IOException e){
                System.out.println("cannot receive data");
            }
        }
    }

}
