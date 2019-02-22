package com.none.non.openhd;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;


public class MainActivity2  extends AppCompatActivity {

    ArrayList<ASetting> mSynchronizedSettings= WFBCDataModel2.createList();

    Button bRefresh;
    Button bSave;

    String IPstr=null;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        setContentView(R.layout.activity_main2);

        bRefresh=findViewById(R.id.buttonRefresh);
        bSave=findViewById(R.id.buttonSave);
        //LinearLayout linearLayout=findViewById(R.id.linearLayout);

        final TableLayout tableLayout=findViewById(R.id.tableLayout);
        //Populate the layout with all Synchronized settings values
        for(ASetting setting: mSynchronizedSettings){
            TableRow tableRow=new TableRow(this);
            TextView textView=new TextView(this);
            EditText editText=new EditText(this);
            tableRow.addView(textView);
            tableRow.addView(editText);
            tableLayout.addView(tableRow);
            textView.setText(setting.key);
            editText.setText("X");
            setting.editText=editText;
            setting.textView=textView;
        }

        bRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //startServerSocket();
        receiveMessagesFromServer();


    }


    private void PackMessageAndSend(final ASetting setting)
    {
        String sendmsg = "RequestChangeSettings";
        sendmsg += setting.key;
        sendmsg += "=";
        sendmsg += setting.value;
        SendUDP(sendmsg);

        try
        {
            Thread.sleep(50);
        }
        catch(InterruptedException ex)
        {
            ex.printStackTrace();
            Toast.makeText(getApplicationContext(), "Sorry, there was an Error", Toast.LENGTH_LONG).show();
        }
    }


    private void SendUDP(final String message) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try
                {
                    DatagramSocket dsSend = new DatagramSocket();
                    InetAddress IPRemote = InetAddress.getByName(IPstr);
                    DatagramPacket dpSend = new DatagramPacket(message.getBytes(), message.length(), IPRemote, 1011);
                    dsSend.send(dpSend);
                    dsSend.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Sorry, there was an Error", Toast.LENGTH_LONG).show();
                }

            }

        });
        thread.start();
    }



    private void receiveMessagesFromServer(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                DatagramSocket socket;
                try {
                    socket = new DatagramSocket(5115);
                    socket.setSoTimeout(5000);
                } catch (SocketException e) {
                    e.printStackTrace();
                    makeToast("cannot open socket");
                    return;
                }
                makeToast("Opened socket");
                byte[] msg = new byte[1000];
                DatagramPacket dp = new DatagramPacket(msg, msg.length);
                while(true)
                {
                    try{
                        socket.receive(dp);
                        if(IPstr == null) {
                            InetAddress ip = dp.getAddress();
                            IPstr = "";
                            IPstr = ip.toString().replace("/", "");
                            makeToast("IP found:"+IPstr+" Requesting Settings from server");
                            //
                            SendUDP("RequestAllSettings");
                        }
                        final String stringData = new String(msg, 0, dp.getLength());
                        //makeToast("Message"+stringData);
                        processPacket(stringData);

                    }catch (IOException e){
                        makeToast("No data");
                    }
                }
            }
        });
        thread.start();
    }

    private void processPacket(final String string){
        if (string.length() < 13){
            makeToast("x "+string);
            return;
        }
        String[] parts = string.split("ConfigResp");
        String ValData = parts[1];
        String ValDataArr[] = ValData.split("=");
        final String Val = ValDataArr[0];
        final String Data = ValDataArr[1];

        makeToast(Val+" "+Data);

        for(final ASetting setting : mSynchronizedSettings){
            if(Val.equals(setting.key)){
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setting.editText.setText(Data);
                    }
                });
            }
        }
    }




    private void makeToast(final String message) {
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


}
