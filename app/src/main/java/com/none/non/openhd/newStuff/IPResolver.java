package com.none.non.openhd.newStuff;

import android.content.Context;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * There are 3 ways to get the IP address of the Ground Pi
 * a) When connected via WIFI hotspot, the IP is fixed to 192.168.1.1
 * b) When conneccted via USB tethering, all IP's are in start with 192.168.42.XXX
 *      this leaves us with 265 possible addresses, low enough to just ping all of them and await a response
 * c) Since Video,Telemetry and WFB-Telemetry are already broadcasted as soon as the AirPI detects a connection, we can just
 * listen for these messages on the corresponding ports and extract the IP as soon as data was received
 */

@SuppressWarnings("WeakerAccess")
public class IPResolver {

    //returns IP of Open.HD /EZ-WB ground pi on success
    //returns null otherwise
    //when interrupted,return (almost) immediately
    public static String resolveOpenHDIP(final Context c){
        //System.out.println("Resolving IP");
        if(IsConnected.checkWifiConnectedOpenHD(c)){
            return "192.168.2.1";
            /*try {
                final InetAddress address = InetAddress.getByName("192.168.2.1");
                final boolean reachable = address.isReachable(20);
                if (reachable) {
                    System.out.println("BLA reachable");
                } else {
                    //System.out.println("couldnt reach"+IP);
                }
            }catch (IOException e){
                e.printStackTrace();
            }*/
            //return "192.168.2.1";
        }
        if(IsConnected.checkWifiConnectedTest(c)){
            return "192.168.137.1";
        }
        if(IsConnected.getUSBStatus(c)== IsConnected.USB_CONNECTION.TETHERING){
            //First, listen for wifibroadcast telemetry data broadcast by the system
            try{
                return listenForDataAndReturnSender(5003,100);
            }catch (IOException e){
                //e.printStackTrace();
            }
            //then, ping all possible IPs as fallback
            final ArrayList<Integer> ipsToTest=new ArrayList<>();
            for(int i=0;i<256;i++){
                ipsToTest.add(i);
            }
            ipsToTest.removeAll(Collections.singletonList(129));
            final List<String> validIPs=pingAllIPsMultiThreaded(ipsToTest,8);
            if(validIPs.size()>0){
                return validIPs.get(0);
            }
        }
        return null;
    }


    private static String listenForDataAndReturnSender(final int port,final int maxWaitTime) throws IOException {
            final DatagramSocket socket = new DatagramSocket(null);
            socket.setReuseAddress(true);
            socket.setSoTimeout(maxWaitTime);
            socket.bind(new InetSocketAddress(port));
            byte[] msg = new byte[10];
            DatagramPacket dp = new DatagramPacket(msg, msg.length);
            socket.receive(dp);
            socket.close();
            return dp.getAddress().toString().replace("/","");
    }

    //split data into chunks and reduce in the end
    private static List<String> pingAllIPsMultiThreaded(final ArrayList<Integer> ipsToTest,final int N_THREADS){
        final long startTime=System.currentTimeMillis();
        final ArrayList<String> allReachableIPs=new ArrayList<>();
        final ArrayList<Thread> workers=new ArrayList<>();
        final ArrayList<List<Integer>> chunks=splitIntoChunks(ipsToTest,N_THREADS);
        for(int i=0;i<N_THREADS;i++){
            final List<Integer> chunk=chunks.get(i);
            final Runnable runnable=new Runnable() {
                @Override
                public void run() {
                    final List<String> reachableIPs=pingAllIPs(chunk);
                    synchronized (allReachableIPs){
                        allReachableIPs.addAll(reachableIPs);
                    }
                }
            };
            final Thread worker=new Thread(runnable);
            worker.start();
            workers.add(worker);
        }
        for(final Thread worker:workers){
            try {
                worker.join();
            } catch (InterruptedException e) {
                //e.printStackTrace();
                worker.interrupt();
                Thread.currentThread().interrupt();
            }
        }
        final long delta=System.currentTimeMillis()-startTime;
        System.out.println("Pinging all ips took ms:"+delta+allReachableIPs.toString());
        return allReachableIPs;
    }

    //blocks until either all ips have been tested or when the calling thread is interrupted
    private static List<String> pingAllIPs(final List<Integer> ipsToPing){
        final ArrayList<String> reachableIPs=new ArrayList<>();
        for(int ipFourthElement:ipsToPing){
            if(Thread.currentThread().isInterrupted()){
                return reachableIPs;
            }
            final String IP="192.168.42."+Integer.toString(ipFourthElement);
            try {
                final InetAddress address=InetAddress.getByName(IP);
                final boolean reachable=address.isReachable(30);
                if(reachable){
                    //IP found !
                    reachableIPs.add(IP);
                    //System.out.println("Reached"+IP);
                }else{
                    //System.out.println("Cannot reach"+IP+" "+address);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return reachableIPs;
    }


    private static ArrayList<List<Integer>> splitIntoChunks(final ArrayList<Integer> data,final int nChunks){
        ArrayList<List<Integer>> ret=new ArrayList<>();
        //if data.size() is not a multiple of nChunks, the last chunk will be smaller
        final int chunkSize=(int) Math.ceil((double)data.size() / nChunks);
        //System.out.println(data.size()+" "+nChunks+" "+chunkSize);
        for(int i=0;i<nChunks;i++){
            int end=(i+1)*chunkSize;
            if(end>data.size())end=data.size();
            final List<Integer> chunk=data.subList(i*chunkSize,end);
            ret.add(chunk);
        }
//        for(List<Integer> chunk:ret){
//            for(int i:chunk){
//                System.out.println(i);
//            }
//        }
        return ret;
    }

    /*private static boolean myIsReachable(final String ip,final int timeout){
        try{
            //The port does not matter-we only look for if the device at the input ip address exists, and if we get an
            //ECONNREFUSED we know that there is a device (tough it does not accept tcp connections at this port)
            //see Inet6AddressImp.java -> isReachable()
            //Socket clientSocket = new Socket(ip,6000);
            Socket clientSocket=new Socket();
            clientSocket.connect(new InetSocketAddress(ip,6000),timeout);
            clientSocket.close();
        }catch (IOException e){
            e.printStackTrace();
            final Throwable cause = e.getCause();
            if(cause!=null){
                System.out.println("Cause"+cause.toString());
            }
        }
        return false;
    }*/
//"192.168.42.136" seems to be a default one, so test it first
    //as well as 192.168.42.55
//            ipsToTest.add(136);
//            ipsToTest.add(55);
//            ipsToTest.add(177);
//            ipsToTest.add(195);

    //TODO multi-threading. Unfortunately the ping is only implemented as an blocking operation, so to speed things up we create 4 threads here
}
