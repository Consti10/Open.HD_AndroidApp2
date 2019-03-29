package com.none.non.openhd.newStuff;

//TODO: Really dirty implementation. Improve !

import android.os.Bundle;

@SuppressWarnings("WeakerAccess")
public class Message {
    public final String src;
    public final String cmd;
    public final String data;
    public final String dataKey,dataValue;

    public Message(final String msg){
        final String[] tmp=msg.split(" ",3);
        src=tmp[0];
        cmd=tmp[1];
        data=(tmp.length==2) ? null : tmp[2];
        if(data!=null){
            final String[] keyValue = data.split("=");
            dataKey=keyValue[0];
            dataValue=keyValue[1];
        }else{
            dataKey=null;
            dataValue=null;
        }
        //System.out.println(toString());
    }

    public boolean ground(){
        return src.equals("G");
    }

    public static String BuildMessageGET(final boolean groundOnly,final String key){
        return BuildMessage(groundOnly? "G":"GA","GET",key);
    }

    public static String BuildMessageCHANGE(final boolean groundOnly,final String key,final String value){
        return BuildMessage(groundOnly? "G":"GA","CHANGE",key+"="+value);
    }

    public static String BuildMessageHELLO_OK(){
        return BuildMessage("G","HELLO_OK","");
    }

    public static String BuildMessageHELLO(final boolean groundOnly){
        return BuildMessage(groundOnly? "G":"GA","HELLO","");
    }

    private static String BuildMessage(final String dst,final String cmd,final String data){
        return dst+" "+cmd+" "+data;
    }

    @Override
    public String toString(){
        StringBuilder builder=new StringBuilder();
        builder.append("src:").append(src);
        builder.append("cmd:").append(cmd);
        //if(data!=null){
        //    builder.append("data:").append(data);
        //}
        if(dataKey!=null){
            builder.append("dataKey:").append(dataKey);
        }
        if(dataValue!=null){
            builder.append("dataValue:").append(dataValue);
        }
        return builder.toString();
    }

}
