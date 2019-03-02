package com.none.non.openhd.newStuff;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.LinkedBlockingQueue;


@SuppressWarnings("WeakerAccess")
public class TCPClient {

    private static final int PORT = 5601;
    //TODO get IP by IPResolver, hardcoded currently
    private static final String IP="192.168.42.136";

    private Thread mTCPThread;

    private final LinkedBlockingQueue<String> mSendQueue;
    private final ProcessMessage mProcessMessage;

    public TCPClient(final ProcessMessage processMessage){
        mSendQueue=new LinkedBlockingQueue<String>(100);
        this.mProcessMessage =processMessage;
    }

    public void sendMessage(final String message){
        mSendQueue.offer(message);
    }

    public void start(){
        mTCPThread=new Thread(new Runnable() {
            @Override
            public void run() {
                while(!mTCPThread.isInterrupted()){
                    System.out.println("Start client");
                    try {
                        Socket clientSocket = new Socket(IP, PORT);
                        clientSocket.setSoTimeout(100);
                        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                        BufferedReader inputFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        mSendQueue.clear();
                        mProcessMessage.connectionEstablished();
                        while (!mTCPThread.isInterrupted()) {
                            try {
                                String msg;
                                while ((msg = mSendQueue.poll()) != null) {
                                    //System.out.println("Sending:" + msg);
                                    outToServer.writeBytes(msg + "\n");
                                }
                            } catch (SocketTimeoutException ignored) {
                            }
                            try {
                                final String line = inputFromServer.readLine();
                                //final String message=line.substring(0,line.length()-1);
                                mProcessMessage.processMessage(line);
                            } catch (SocketTimeoutException ignored) {
                            }
                        }
                        clientSocket.close();
                    } catch (IOException e) {
                        //e.printStackTrace();
                        System.out.println("Client cannot connect");
                    }
                }
            }
        });
        mTCPThread.start();
    }

    public void stop(){
        mTCPThread.interrupt();
    }


    public interface ProcessMessage{
        void processMessage(final String message);
        void connectionEstablished();
        void connectionClosed();
    }
}
