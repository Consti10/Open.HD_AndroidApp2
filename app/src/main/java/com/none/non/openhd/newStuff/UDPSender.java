package com.none.non.openhd.newStuff;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class UDPSender extends Thread {
    private static final String TAG ="UDPSender" ;
    private static final int PORT = 5601;
    private final String IP;

    public UDPSender(){
        IP="192.168.42.41";
        System.out.println("IP is:"+IP);
    }

    @Override
    public void run(){
        try {
            DatagramSocket socket;
            socket = new DatagramSocket(PORT);
            final String MSG = "VERSION";
            while (!isInterrupted()) {
                {
                    DatagramPacket packetSend = new DatagramPacket(MSG.getBytes(), MSG.getBytes().length, new InetSocketAddress(IP, PORT));
                    socket.send(packetSend);
                    Log.d(TAG, "Sent data");
                }
                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
            }
            socket.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getNetworkInterfacesIPAddresses(){
        String ret="";
        StringBuilder s= new StringBuilder();
        try{
            for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
                NetworkInterface intf=en.nextElement();
                for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                    InetAddress inetAddress=enumIpAddr.nextElement();
                    if(!intf.isLoopback() && !intf.getName().contains("dummy0")){
                        s.append("Interface ").append(intf.getName()).append(": ").append(inetAddress.getHostAddress()).append("\n");
                        if(intf.getName().equals("rndis0")){
                            ret=inetAddress.getHostAddress();
                        }
                    }
                }
            }
        }catch(Exception e){e.printStackTrace();}
        return ret;
    }

}

    /*private RTSPResponse receiveServerResponse2() throws IOException{
        Log.d(TAG,"receiveServerResponse2() start");
        ArrayList<String> message=new ArrayList<>();
        ArrayList<String> messageBody=new ArrayList<>();
        final InputStream inputStream=mClientSocket.getInputStream();
        byte[] data=new byte[2*];
        long time=System.currentTimeMillis();
        int len;//=inputStream.read(data);
        StringBuilder s= new StringBuilder();//=new String(data,0,len);
        boolean crlfFound;
        System.out.println("CRLF l:"+CRLF.length());
        while (System.currentTimeMillis()-time<5000){
            len=inputStream.read(data);
            s.append(new String(data, 0, len));
            if(s.length()>=CRLF.length() && s.substring(s.length()-CRLF.length()).equals(CRLF)){
                System.out.println(s);
                s = new StringBuilder();
            }
        }

        while (inputStream.available()>0){
            len=inputStream.read(data);
            s.append(new String(data, 0, len));
        }
        Log.d(TAG, s.toString());
        RTSPResponse response=new RTSPResponse(message,messageBody);
        Log.d(TAG,"receiveServerResponse2() stop");
        return response;
    }*/