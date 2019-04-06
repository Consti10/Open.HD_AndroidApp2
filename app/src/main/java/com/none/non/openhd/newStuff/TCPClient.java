package com.none.non.openhd.newStuff;

import android.content.Context;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.NoSuchElementException;
import java.util.concurrent.LinkedBlockingQueue;


@SuppressWarnings("WeakerAccess")
public class TCPClient implements Runnable {

    private static final int PORT = 5601;

    private final LinkedBlockingQueue<String> mSendQueue;
    private final ProcessMessage mProcessMessage;

    private Thread mTCPThread;
    private final Context context;

    public TCPClient(final ProcessMessage processMessage,final Context context){
        mSendQueue= new LinkedBlockingQueue<>();
        this.mProcessMessage =processMessage;
        this.context=context;
    }

    @Override
    public void run() {
        while(!Thread.currentThread().isInterrupted()){
            final String IP=IPResolver.resolveOpenHDIP(context);
            if(IP==null){
                //Wait a little bit before trying again
                try{ Thread.sleep(1000); } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                    continue;
                }
                continue;
            }
            System.out.println("Start client on "+IP);
            Socket clientSocket;
            DataOutputStream outToServer;
            BufferedReader inputFromServer;
            try {
                clientSocket = new Socket();
                clientSocket.connect(new InetSocketAddress(IP,PORT), 500);
                outToServer = new DataOutputStream(clientSocket.getOutputStream());
                inputFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                mSendQueue.clear();
                clientSocket.setSoTimeout(100);
            }catch (IOException e){
                e.printStackTrace();
                //avoid spamming the server with requests, wait a bit before trying again
                try { Thread.sleep(1000); } catch (InterruptedException unused){
                    Thread.currentThread().interrupt();
                }
                continue;
            }
            mProcessMessage.OnConnectionEstablished();
            //Now receive and send data until an IO exception occurs or the thread is interrupted
            try {
                long lastMessageFromServer=System.currentTimeMillis();
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        String msg;
                        while ((msg = mSendQueue.element()) != null) {
                            outToServer.writeBytes(msg + "\n");
                            mSendQueue.remove(msg);
                            System.out.println("Wrote to server"+msg);
                        }
                        outToServer.flush();
                    } catch (NoSuchElementException ignored) {
                    } catch (SocketTimeoutException ignored) {
                        //System.out.println("error sending");
                        //ignored.printStackTrace();
                    }
                    try {
                        String line;
                        while((line=inputFromServer.readLine())!=null){
                            lastMessageFromServer=System.currentTimeMillis();
                            mProcessMessage.processMessage(line);
                        }
                    } catch (SocketTimeoutException ignored) {
                    }
                    if(System.currentTimeMillis()-lastMessageFromServer>5000){
                        System.out.println("No message from server >5000ms.Exit");
                        break;
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mProcessMessage.OnConnectionClosed();
        }
    }

    public void sendMessage(final String message){
        mSendQueue.offer(message);
    }

    public void start(final Context c){
        mTCPThread=new Thread(this);
        mTCPThread.start();
    }

    public void stop(){
        mTCPThread.interrupt();
    }

    public void joinSafe(){
        try{
            mTCPThread.join();
            mTCPThread=null;
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }



    public interface ProcessMessage{
        void processMessage(final String message);
        void OnConnectionEstablished();
        void OnConnectionClosed();
    }

}
